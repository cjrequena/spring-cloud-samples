package com.cjrequena.sample.web.api;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.exception.ErrorDTO;
import com.cjrequena.sample.exception.api.BadRequestApiException;
import com.cjrequena.sample.exception.api.FailedDependencyApiException;
import com.cjrequena.sample.exception.api.NotFoundApiException;
import com.cjrequena.sample.exception.api.PaymentRequiredApiException;
import com.cjrequena.sample.exception.service.FeignServiceException;
import com.cjrequena.sample.exception.service.InsufficientBalanceServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.exception.service.OrderNotFoundServiceException;
import com.cjrequena.sample.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = OrderApi.ENDPOINT, headers = {OrderApi.ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderApi {

  public static final String ENDPOINT = "/order-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;
  private final OrderService orderService;

  @PostMapping(
    path = "/orders",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(@Valid @RequestBody OrderDTO dto, HttpServletRequest request, UriComponentsBuilder ucBuilder)
    throws BadRequestApiException, FailedDependencyApiException, PaymentRequiredApiException {
    try {
      this.orderService.create(dto);
      URI resourcePath = ucBuilder.path(new StringBuilder().append(request.getServletPath()).append("/{id}").toString()).buildAndExpand(dto.getId()).toUri();
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      headers.setLocation(resourcePath);
      return ResponseEntity.created(resourcePath).headers(headers).build();
    } catch (InsufficientBalanceServiceException ex) {
      throw new PaymentRequiredApiException(ex.getMessage());
    } catch (FeignServiceException ex){
      ErrorDTO errorDTO = ex.getErrorDTO();
      log.error("{}", errorDTO);
      if(errorDTO.getStatus()==HttpStatus.FAILED_DEPENDENCY.value()){
        throw new FailedDependencyApiException(ex.getMessage());
      }
      throw new BadRequestApiException(ex.getMessage());
    }
  }

  @GetMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<OrderDTO> retrieveById(@PathVariable(value = "id") Integer id) throws NotFoundApiException {
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      OrderDTO dto = this.orderService.retrieveById(id);
      return new ResponseEntity<>(dto, responseHeaders, HttpStatus.OK);
    } catch (OrderNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
  }

  @GetMapping(
    path = "/orders",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<List<OrderDTO>> retrieve() {

    List<OrderDTO> dtoList = this.orderService.retrieve();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(dtoList, responseHeaders, HttpStatus.OK);
  }

  @PutMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "id") Integer id, @Valid @RequestBody OrderDTO dto) throws NotFoundApiException, BadRequestApiException {
    try {
      dto.setId(id);
      this.orderService.update(dto);
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (OrderNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    } catch (OptimisticConcurrencyServiceException ex) {
      throw new BadRequestApiException(ex.getMessage());
    }
  }

  @DeleteMapping(
    path = "/orders/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> delete(@PathVariable(value = "id") Integer id) throws NotFoundApiException {
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      this.orderService.delete(id);
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (OrderNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
  }

}
