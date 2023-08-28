package com.cjrequena.sample.service;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.ErrorDTO;
import com.cjrequena.sample.exception.service.WebClientServiceException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service("accountServiceFeignClient")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService  {

  @Qualifier("accountServiceWebClient")
  private final WebClient accountServiceWebClient;
  @Qualifier("lbAccountServiceWebClient")
  private final WebClient lbAccountServiceWebClient;

  @CircuitBreaker(name = "default", fallbackMethod = "retrieveFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public Mono<ResponseEntity<AccountDTO>> retrieve(UUID id){
    return accountServiceWebClient
      .get()
      .uri("/account-service/api/accounts/" + id)
      .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
      .header(Constants.ACCEPT_VERSION, Constants.VND_ACCOUNT_SERVICE_V1)
      .retrieve()
      .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus), clientResponse -> Mono.error(new WebClientServiceException("The resource or the path :: was not Found")))
      .toEntity(AccountDTO.class);
  }

  public Mono<ResponseEntity<AccountDTO>> retrieveFallbackMethod(UUID id, Throwable ex) throws Throwable {
    log.debug("retrieveFallbackMethod", ex.getCause());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)));
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());

    if (ex instanceof WebClientServiceException) {
      throw ex;
    }
    if (ex instanceof WebClientException) {
      errorDTO.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
    }
    if (ex.getCause() != null && ex.getCause().getMessage().contains("Connection refused")) {
      errorDTO.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
    }
    throw new WebClientServiceException(errorDTO);
  }

  @CircuitBreaker(name = "default", fallbackMethod = "depositFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public Mono<ResponseEntity<Void>> deposit(DepositAccountDTO dto)  {
    return accountServiceWebClient
      .post()
      .uri("/account-service/api/accounts/deposit")
      .header(Constants.ACCEPT_VERSION, Constants.VND_ACCOUNT_SERVICE_V1)
      .header("Accept-Version", "vnd.foo-service.v1")
      .body(Mono.just(dto), DepositAccountDTO.class)
      //.exchangeToMono(response -> Mono.just(response.mutate().build()));
      .retrieve()
      .onStatus(httpStatus -> HttpStatus.CONFLICT.equals(httpStatus), clientResponse -> Mono.error(new WebClientServiceException(HttpStatus.CONFLICT.getReasonPhrase())))
      .toBodilessEntity();
  }

  public Mono<ResponseEntity<Void>> depositFallbackMethod(DepositAccountDTO dto, Throwable ex) throws Throwable {
    log.debug("depositFallbackMethod");
    throw ex;
  }

  @CircuitBreaker(name = "default", fallbackMethod = "withdrawFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public Mono<ResponseEntity<Void>> withdraw(WithdrawAccountDTO dto)  {
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
