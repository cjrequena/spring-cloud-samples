package com.cjrequena.sample.controller;

import com.cjrequena.sample.controller.dto.FooDTO;
import com.cjrequena.sample.domain.mapper.FooMapper;
import com.cjrequena.sample.domain.model.event.FooEvent;
import com.cjrequena.sample.service.ProducerService1;
import com.cjrequena.sample.service.ProducerService2;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.OffsetDateTime;
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
  private final ProducerService1 producerService1;
  private final ProducerService2 producerService2;
  private final FooMapper fooMapper;

  @PostMapping(
    path = "/producer/produce",
    produces = {APPLICATION_JSON_VALUE}
  )
  @SneakyThrows
  public ResponseEntity<Void> produce(@Parameter @Valid @RequestBody FooDTO dto) {
    dto.setId(UUID.randomUUID());
    FooEvent event = FooEvent
      .builder()
      .id(UUID.randomUUID())
      .time(OffsetDateTime.now())
      .type("com.cjrequena.sample.sse.v1")
      .source("sse-producer")
      .data(fooMapper.toFooVO(dto))
      .build();

    //this.producerService1.produce(event);
    this.producerService2.produce(event);

    // Headers
    HttpHeaders headers = new HttpHeaders();
    return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
  }
}
