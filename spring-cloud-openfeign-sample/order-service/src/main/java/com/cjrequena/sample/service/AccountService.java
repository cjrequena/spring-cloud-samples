package com.cjrequena.sample.service;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.ErrorDTO;
import com.cjrequena.sample.exception.service.FeignServiceException;
import com.cjrequena.sample.service.feign.IAccountServiceFeignClient;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service("accountServiceFeignClient")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService implements IAccountServiceFeignClient {

  private final IAccountServiceFeignClient accountServiceFeignClient;

  @Override
  @CircuitBreaker(name = "default", fallbackMethod = "retrieveFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public AccountDTO retrieve(UUID id) throws FeignServiceException {
    return this.accountServiceFeignClient.retrieve(id);
  }

  public AccountDTO retrieveFallbackMethod(UUID id, Throwable ex) throws Throwable {
    log.debug("retrieveFallbackMethod", ex.getCause());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)));
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());

    if (ex instanceof FeignServiceException) {
      throw ex;
    }

    if (ex instanceof FeignException) {
      errorDTO.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
    }

    if (ex.getCause() != null && ex.getCause().getMessage().contains("Connection refused")) {
      errorDTO.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
    }

    throw new FeignServiceException(errorDTO);
  }

  @Override
  @CircuitBreaker(name = "default", fallbackMethod = "depositFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public ResponseEntity<Void> deposit(DepositAccountDTO dto) throws FeignServiceException {
    return this.accountServiceFeignClient.deposit(dto);
  }

  public ResponseEntity<Void> depositFallbackMethod(DepositAccountDTO dto, Throwable ex) throws FeignServiceException {
    log.debug("depositFallbackMethod");
    throw new FeignServiceException(ex.getMessage(), ex);
  }

  @Override
  @CircuitBreaker(name = "default", fallbackMethod = "withdrawFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public ResponseEntity<Void> withdraw(WithdrawAccountDTO dto) throws FeignServiceException {
    return this.accountServiceFeignClient.withdraw(dto);
  }

  public ResponseEntity<Void> withdrawFallbackMethod(WithdrawAccountDTO dto, Throwable ex) throws FeignServiceException {
    log.debug("withdrawFallbackMethod");
    throw new FeignServiceException(ex.getMessage(), ex);
  }
}
