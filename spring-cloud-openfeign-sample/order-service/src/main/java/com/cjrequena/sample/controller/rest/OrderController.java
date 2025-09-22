package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.common.EStatus;
import com.cjrequena.sample.domain.model.Order;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.exception.controller.BadRequestException;
import com.cjrequena.sample.exception.controller.FailedDependencyException;
import com.cjrequena.sample.exception.controller.NotFoundException;
import com.cjrequena.sample.exception.controller.PaymentRequiredException;
import com.cjrequena.sample.exception.service.*;
import com.cjrequena.sample.mapper.OrderMapper;
import com.cjrequena.sample.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = OrderController.ENDPOINT, headers = {OrderController.ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

  public static final String ENDPOINT = "/order-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;
  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @SneakyThrows
  @PostMapping(
    path = "/orders",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(@Valid @RequestBody OrderDTO dto, HttpServletRequest request, UriComponentsBuilder ucBuilder) {
    try {
      Order order = Order
        .builder()
        .accountId(dto.getAccountId())
        .description(dto.getDescription())
        .status(EStatus.PENDING)
        .total(dto.getTotal())
        .build();

      this.orderService.create(order);
      URI resourcePath = ucBuilder.path(request.getServletPath() + "/{id}").buildAndExpand(dto.getId()).toUri();
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      headers.setLocation(resourcePath);
      return ResponseEntity.created(resourcePath).headers(headers).build();
    } catch (InsufficientBalanceException ex) {
      throw new PaymentRequiredException(ex.getMessage());
    } catch (AccountNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    } catch (AccountServiceUnavailableException ex) {
      throw new FailedDependencyException(ex.getMessage());
    }
  }

  @SneakyThrows
  @GetMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<OrderDTO> retrieveById(@PathVariable(value = "id") UUID id) {
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      Order order = this.orderService.retrieveById(id);
      OrderDTO dto = this.orderMapper.toDTO(order);
      return new ResponseEntity<>(dto, responseHeaders, HttpStatus.OK);
    } catch (OrderNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @SneakyThrows
  @GetMapping(
    path = "/orders",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<List<OrderDTO>> retrieve() {

    List<Order> orderList = this.orderService.retrieve();
    List<OrderDTO > orderDTOList = this.orderMapper.toDTOList(orderList);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(orderDTOList, responseHeaders, HttpStatus.OK);
  }

  @SneakyThrows
  @PutMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody OrderDTO dto)  {
    try {
      dto.setId(id);
      this.orderService.update(this.orderMapper.toOrderDomain(dto));
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (OrderNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    } catch (OptimisticConcurrencyException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @SneakyThrows
  @DeleteMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> delete(@PathVariable(value = "id") UUID id) {
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      this.orderService.delete(id);
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (OrderNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

}
