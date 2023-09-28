package com.cjrequena.sample.web.controller;

import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.service.ProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.UUID;

import static com.cjrequena.sample.common.Constants.VND_SAMPLE_SERVICE_V1;
import static com.cjrequena.sample.web.controller.ProducerController.ACCEPT_VERSION;
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

  public static final String ENDPOINT = "/producer";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;
  private final ProducerService producerService;

  @Operation(
    summary = "Command for new bank account creation ",
    description = "Command for new bank account creation ",
    parameters = {
      @Parameter(
        name = "Accept-Version",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(
          name = "Accept-Version",
          type = "string",
          allowableValues = {VND_SAMPLE_SERVICE_V1}
        )
      ),
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FooDTO.class)))
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "202",
        description = "Accepted - The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place.."),
      @ApiResponse(responseCode = "400", description = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
      @ApiResponse(responseCode = "401", description = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
      @ApiResponse(responseCode = "408", description = "Request Timeout"),
      @ApiResponse(responseCode = "409", description = "Conflict - The request could not be processed because of conflict in the request"),
      @ApiResponse(responseCode = "429", description = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - We couldn't create the resource. Please try again."),
      @ApiResponse(responseCode = "503", description = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
    }
  )
  @PostMapping(
    path = "/produce",
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
