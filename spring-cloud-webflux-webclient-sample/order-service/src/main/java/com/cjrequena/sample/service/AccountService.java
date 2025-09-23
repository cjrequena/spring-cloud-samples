package com.cjrequena.sample.service;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundException;
import com.cjrequena.sample.exception.service.AccountServiceUnavailableException;
import com.cjrequena.sample.exception.service.WebClientException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service("accountService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService {

  @Qualifier("accountServiceWebClient")
  private final WebClient accountServiceWebClient;
  @Qualifier("lbAccountServiceWebClient")
  private final WebClient lbAccountServiceWebClient;

  @CircuitBreaker(name = "accountService", fallbackMethod = "retrieveFallback")
  @Bulkhead(name = "accountService")
  @Retry(name = "accountService")
  public Mono<ResponseEntity<AccountDTO>> retrieve(UUID id) {
    log.debug("Calling account-service for accountId={}", id);

    return accountServiceWebClient.get()
      .uri("/account-service/api/accounts/{id}", id)
      .accept(MediaType.APPLICATION_JSON)
      .header(Constants.ACCEPT_VERSION, Constants.VND_ACCOUNT_SERVICE_V1)
      .retrieve()
      .onStatus(HttpStatus.NOT_FOUND::equals, resp ->
        Mono.error(new AccountNotFoundException("Account " + id + " not found")))
      .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, resp ->
        Mono.error(new AccountServiceUnavailableException("Account service unavailable")))
      .toEntity(AccountDTO.class)
      .doOnSuccess(entity -> log.debug("Successfully retrieved account id={}", id))
      .doOnError(error -> log.warn("Error retrieving account id={} :: {}", id, error.getMessage()));
  }

  @SuppressWarnings("unused")
  private Mono<ResponseEntity<AccountDTO>> retrieveFallback(UUID id, Throwable ex) {
    log.warn("Fallback triggered for accountId={}, reason={}", id, ex.toString());

    if (ex instanceof AccountNotFoundException || ex instanceof AccountServiceUnavailableException) {
      return Mono.error(ex); // already meaningful
    }
    if (ex instanceof WebClientRequestException wcre) {
      String message = Optional.ofNullable(wcre.getRootCause())
        .map(Throwable::getMessage)
        .orElse("Unknown network error");
      if ("Connection refused".equalsIgnoreCase(message)) {
        return Mono.error(new AccountServiceUnavailableException("Account service unavailable", ex));
      }
    }
    return Mono.error(new RuntimeException("Unexpected fallback error for account " + id, ex));
  }



  @CircuitBreaker(name = "default", fallbackMethod = "depositFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public Mono<ResponseEntity<Void>> deposit(DepositAccountDTO dto) {
    return accountServiceWebClient
      .post()
      .uri("/account-service/api/accounts/deposit")
      .header(Constants.ACCEPT_VERSION, Constants.VND_ACCOUNT_SERVICE_V1)
      .header("Accept-Version", "vnd.foo-service.v1")
      .body(Mono.just(dto), DepositAccountDTO.class)
      //.exchangeToMono(response -> Mono.just(response.mutate().build()));
      .retrieve()
      .onStatus(httpStatus -> HttpStatus.CONFLICT.equals(httpStatus), clientResponse -> Mono.error(new WebClientException(HttpStatus.CONFLICT.getReasonPhrase())))
      .toBodilessEntity();
  }

  public Mono<ResponseEntity<Void>> depositFallbackMethod(DepositAccountDTO dto, Throwable ex) throws Throwable {
    log.debug("depositFallbackMethod");
    throw ex;
  }

  @CircuitBreaker(name = "default", fallbackMethod = "withdrawFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public Mono<ResponseEntity<Void>> withdraw(WithdrawAccountDTO dto) {
    return accountServiceWebClient
      .post()
      .uri("/account-service/api/accounts/withdraw")
      .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
      .header(Constants.ACCEPT_VERSION, Constants.VND_ACCOUNT_SERVICE_V1)
      .body(Mono.just(dto), WithdrawAccountDTO.class)
      //.exchangeToMono(response -> Mono.just(response.mutate().build()));
      .retrieve()
      //.onStatus(httpStatus -> HttpStatus.CONFLICT.equals(httpStatus), clientResponse -> Mono.error(new WebClientServiceException(HttpStatus.CONFLICT.getReasonPhrase())))
      .toBodilessEntity();
  }

  public Mono<ResponseEntity<Void>> withdrawFallbackMethod(WithdrawAccountDTO dto, Throwable ex) throws Throwable {
    log.debug("withdrawFallbackMethod");
    throw ex;
  }
}
