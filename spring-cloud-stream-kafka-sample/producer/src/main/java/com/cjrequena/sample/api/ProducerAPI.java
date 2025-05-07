package com.cjrequena.sample.api;

import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.service.ProducerService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.UUID;

import static com.cjrequena.sample.api.ProducerAPI.ACCEPT_VERSION;
import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
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
@RequestMapping(value = ProducerAPI.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerAPI {

  public static final String ENDPOINT = "/foo-service/api";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;
  private final ProducerService producerService;

  @PostMapping(
    path = "/producer/produce",
    produces = {APPLICATION_JSON_VALUE}
  )
  @SneakyThrows
  public ResponseEntity<Void> produce(@Parameter @Valid @RequestBody FooDTO dto, BindingResult bindingResult, HttpServletRequest request, UriComponentsBuilder ucBuilder) {
    dto.setId(UUID.randomUUID());
    this.producerService.produce(dto);
    // Headers
    HttpHeaders headers = new HttpHeaders();
    return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
  }
}
