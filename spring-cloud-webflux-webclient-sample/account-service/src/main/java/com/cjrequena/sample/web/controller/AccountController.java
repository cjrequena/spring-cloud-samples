package com.cjrequena.sample.web.controller;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.cjrequena.sample.web.controller.AccountController.ACCEPT_VERSION;
import static com.cjrequena.sample.web.controller.AccountController.ENDPOINT;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Slf4j
@RestController
@RequestMapping(value = ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountController {

  public static final String ENDPOINT = "/account-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;

  private final AccountService accountService;

  @PostMapping(path = "/accounts/deposit", produces = {APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<Void>> deposit(@RequestBody DepositAccountDTO dto) {
    return this.accountService.deposit(dto)
      .map(_entity -> {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
        return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
      });

  }

  @PostMapping(path = "/accounts/withdraw", produces = {APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<Void>> withdraw(@RequestBody WithdrawAccountDTO dto) {
    return this.accountService
      .withdraw(dto)
      .map(_entity -> {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
        return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
      });
  }
}
