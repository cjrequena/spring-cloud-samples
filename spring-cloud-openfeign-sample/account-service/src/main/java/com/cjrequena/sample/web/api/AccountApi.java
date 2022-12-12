package com.cjrequena.sample.web.api;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.exception.api.ConflictApiException;
import com.cjrequena.sample.exception.api.NotFoundApiException;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.service.AccountService;
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
import java.util.UUID;

import static com.cjrequena.sample.web.api.AccountApi.ACCEPT_VERSION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = AccountApi.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountApi {

  public static final String ENDPOINT = "/account-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;

  private final AccountService accountService;

  @PostMapping(
    path = "/accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(@Valid @RequestBody AccountDTO dto, HttpServletRequest request, UriComponentsBuilder ucBuilder) {
    accountService.create(dto);
    URI resourcePath = ucBuilder.path(new StringBuilder().append(request.getServletPath()).append("/{id}").toString()).buildAndExpand(dto.getId()).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    headers.setLocation(resourcePath);
    return ResponseEntity.created(resourcePath).headers(headers).build();
  }

  @GetMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<AccountDTO> retrieveById(@PathVariable(value = "id") UUID id) throws NotFoundApiException {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      AccountDTO dto = this.accountService.retrieveById(id);
      return new ResponseEntity<>(dto, headers, HttpStatus.OK);
    } catch (AccountNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
  }

  @GetMapping(
    path = "/accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<List<AccountDTO>> retrieve() {
    List<AccountDTO> dtoList = this.accountService.retrieve();
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(dtoList, headers, HttpStatus.OK);
  }

  @PutMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody AccountDTO dto, @RequestHeader("version") Long version) throws NotFoundApiException, ConflictApiException {
    try {
      dto.setId(id);
      dto.setVersion(version);
      this.accountService.update(dto);
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    } catch (AccountNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    } catch (OptimisticConcurrencyServiceException ex) {
      throw new ConflictApiException(ex.getMessage());
    }
  }

  @DeleteMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> delete(@PathVariable(value = "id") UUID id) throws NotFoundApiException {
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      this.accountService.delete(id);
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (AccountNotFoundServiceException ex) {
      throw new NotFoundApiException(ex.getMessage());
    }
  }

}
