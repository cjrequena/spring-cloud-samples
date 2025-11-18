package com.cjrequena.sample.controller;

import com.cjrequena.sample.controller.dto.FooDTO;
import com.cjrequena.sample.domain.mapper.FooMapper;
import com.cjrequena.sample.service.ProducerService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.cjrequena.sample.controller.ProducerController.ACCEPT_VERSION;
import static com.cjrequena.sample.shared.common.Constant.VND_SAMPLE_SERVICE_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@SuppressWarnings("unchecked")
@Slf4j
@RestController
@RequestMapping(value = ProducerController.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerController {

  public static final String ENDPOINT = "/foo-service/api";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;
  private final ProducerService producerService;
  private final FooMapper fooMapper;

  @PostMapping(
    path = "/producer/produce",
    produces = {APPLICATION_JSON_VALUE}
  )
  @SneakyThrows
  public ResponseEntity<Void> produce(@Parameter @Valid @RequestBody FooDTO dto, @RequestHeader(value = "Subscription-Key", required = false) String subscriptionKey) {
    dto.setId(UUID.randomUUID());
    this.producerService.produce(fooMapper.toFooVO(dto), subscriptionKey);
    // Headers
    HttpHeaders headers = new HttpHeaders();
    return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
  }
}
