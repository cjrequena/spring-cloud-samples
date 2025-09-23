package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.domain.model.Order;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.exception.controller.FailedDependencyException;
import com.cjrequena.sample.exception.controller.NotFoundAException;
import com.cjrequena.sample.exception.service.AccountNotFoundException;
import com.cjrequena.sample.exception.service.AccountServiceUnavailableException;
import com.cjrequena.sample.exception.service.OrderNotFoundException;
import com.cjrequena.sample.mapper.OrderMapper;
import com.cjrequena.sample.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static com.cjrequena.sample.controller.rest.OrderController.ACCEPT_VERSION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = OrderController.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

  public static final String ENDPOINT = "/order-service/api";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;
  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @PostMapping(
    path = "/orders",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> create(@Valid @RequestBody OrderDTO dto) {
    Order order = Order.createNewWith(
      dto.getAccountId(),
      dto.getDescription(),
      dto.getStatus(),
      dto.getTotal()
    );

    return orderService.create(order)
      .map(_dto -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no-store, private, max-age=0");
        headers.set("Accept-Version", ACCEPT_VERSION);
        headers.set("id", _dto.getId().toString());

        URI location = URI.create(ENDPOINT + "/orders/id/" + _dto.getId());
        return ResponseEntity.created(location).headers(headers).build();
      })
      .onErrorMap(ex -> {
        if (ex instanceof AccountNotFoundException) {
          return new NotFoundAException(ex.getMessage());
        }
        if (ex instanceof AccountServiceUnavailableException) {
          log.warn("Order creation failed: account service unavailable while processing accountId={}", dto.getAccountId());
          return new FailedDependencyException(ex.getMessage());
        }
        log.error("Unexpected error during order creation for accountId={}: {}", dto.getAccountId(), ex.getMessage(), ex);
        return ex;
      });
  }


  @GetMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<OrderDTO>> retrieveById(@PathVariable(value = "id") UUID id) {
    return this.orderService
      .retrieveById(id)
      .map(this.orderMapper::toDTO)
      .map(_dto -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
        headers.set("id", _dto.getId().toString());
        return ResponseEntity.ok().headers(headers).body(_dto);
      })
      .onErrorResume(ex -> {
          if (ex instanceof OrderNotFoundException) {
            return Mono.error(new NotFoundAException());
          }
          return Mono.error(ex);
        }
      );
  }

  @GetMapping(
    path = "/orders",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Flux<OrderDTO>>> retrieve() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    final Flux<OrderDTO> dtos$ = this.orderService.retrieve().map(this.orderMapper::toDTO);
    return Mono.just(ResponseEntity.ok().headers(headers).body(dtos$));
  }

  @PutMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody OrderDTO dto, @RequestHeader("version") Long version) {
    Order order = Order.createNewWith(dto.getId(), dto.getAccountId(), dto.getDescription(), dto.getStatus(), dto.getTotal());
    return this.orderService.update(order)
      .map(_entity -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
        return ResponseEntity.noContent().headers(headers).build();
      })
      .onErrorMap(ex -> {
          if (ex instanceof OrderNotFoundException) {
            return new NotFoundAException();
          }
          return ex;
        }
      );
  }

  @DeleteMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> delete(@PathVariable(value = "id") UUID id) {
    return this.orderService.delete(id)
      .map(entity -> {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
        return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
      })
      .onErrorMap(ex -> {
          if (ex instanceof OrderNotFoundException) {
            return new NotFoundAException();
          }
          return ex;
        }
      );
  }

}
