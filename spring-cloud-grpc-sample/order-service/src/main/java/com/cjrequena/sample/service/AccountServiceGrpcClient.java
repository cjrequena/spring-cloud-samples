package com.cjrequena.sample.service;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.ErrorDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundException;
import com.cjrequena.sample.exception.service.GrpcException;
import com.cjrequena.sample.mapper.AccountMapper;
import com.cjrequena.sample.proto.AccountServiceGrpc.AccountServiceBlockingStub;
import com.cjrequena.sample.proto.*;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service("accountServiceGrpcClient")
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceGrpcClient {

//  @Autowired
//  @Qualifier("accountServiceBlockingStub")
  @GrpcClient("account-service")
  private AccountServiceBlockingStub accountServiceBlockingStub;

  @Autowired
  private AccountMapper accountMapper;

  @CircuitBreaker(name = "default", fallbackMethod = "retrieveFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public AccountDTO retrieveById(UUID id) throws AccountNotFoundException {
    try {
      return Optional.ofNullable(id)
        .map(UUID::toString)
        .map(val -> RetrieveAccountByIdRequest.newBuilder().setId(val).build())
        .map(accountServiceBlockingStub::retrieveAccountById)
        .map(RetrieveAccountByIdResponse::getAccount)
        .map(accountMapper::toDTO)
        .orElseThrow(() -> new IllegalArgumentException("ID must not be null"));
    } catch (StatusRuntimeException ex) {
      if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
        throw new AccountNotFoundException("Account not found ID: " + id, ex);
      } else if (ex.getStatus().getCode() == Status.Code.UNAVAILABLE) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)));
        errorDTO.setErrorCode(ex.getClass().getSimpleName());
        errorDTO.setMessage("account-service UNAVAILABLE");
        errorDTO.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
        throw new GrpcException(errorDTO);
      }
      throw ex;
    }
  }

  public AccountDTO retrieveFallbackMethod(UUID id, Throwable ex) throws Throwable {
    log.debug("retrieveFallbackMethod", ex.getCause());
    throw ex;
  }

  @CircuitBreaker(name = "default", fallbackMethod = "depositFallback")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public void deposit(DepositAccountDTO dto) {
    Objects.requireNonNull(dto, "DepositAccountDTO cannot be null");
    Objects.requireNonNull(dto.getAccountId(), "AccountId cannot be null");
    Objects.requireNonNull(dto.getAmount(), "Amount cannot be null");

    DepositRequest request = DepositRequest.newBuilder()
      .setAccountId(dto.getAccountId().toString())
      .setAmount(dto.getAmount().toString())
      .build();

    try {
      final DepositResponse response = accountServiceBlockingStub.deposit(request);
      log.info("Deposit successful for accountId={}, amount={}", dto.getAccountId(), dto.getAmount());
    } catch (Exception ex) {
      log.error("Deposit failed for accountId={}, amount={}, error={}",
        dto.getAccountId(), dto.getAmount(), ex.getMessage(), ex);
      throw ex;
    }
  }

  @SuppressWarnings("unused")
  private void depositFallback(DepositAccountDTO dto, Throwable ex) throws Throwable {
    log.warn("Fallback triggered for deposit. accountId={}, amount={}, reason={}",
      dto != null ? dto.getAccountId() : "N/A",
      dto != null ? dto.getAmount() : "N/A",
      ex.getMessage(), ex);

    // Depending on your business needs:
    // - Save to a retry queue
    // - Publish an event
    // - Notify monitoring system
    // For now, rethrow the exception
    throw ex;
  }


  @CircuitBreaker(name = "default", fallbackMethod = "withdrawFallback")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public void withdraw(WithdrawAccountDTO dto) throws Throwable  {
    Objects.requireNonNull(dto, "WithdrawAccountDTO cannot be null");
    Objects.requireNonNull(dto.getAccountId(), "AccountId cannot be null");
    Objects.requireNonNull(dto.getAmount(), "Amount cannot be null");

    WithdrawRequest request = WithdrawRequest.newBuilder()
      .setAccountId(dto.getAccountId().toString())
      .setAmount(dto.getAmount().toString())
      .build();

    try {
      accountServiceBlockingStub.withdraw(request);
      log.info("Withdraw successful for accountId={}, amount={}",
        dto.getAccountId(), dto.getAmount());
    } catch (Exception ex) {
      log.error("Withdraw failed for accountId={}, amount={}, error={}",
        dto.getAccountId(), dto.getAmount(), ex.getMessage(), ex);
      throw new GrpcException("Withdraw request failed", ex);
    }
  }

  @SuppressWarnings("unused")
  private void withdrawFallback(WithdrawAccountDTO dto, Throwable ex) throws Throwable  {
    log.warn("Fallback triggered for withdraw. accountId={}, amount={}, reason={}",
      dto != null ? dto.getAccountId() : "N/A",
      dto != null ? dto.getAmount() : "N/A",
      ex.getMessage(), ex);

    // Business options:
    // - Persist the request for retry later
    // - Publish to a dead-letter queue
    // - Trigger alerting/monitoring
    // For now, wrap and rethrow
    throw ex;
  }

}
