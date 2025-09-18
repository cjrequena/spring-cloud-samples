package com.cjrequena.sample.service;

import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundRuntimeException;
import com.cjrequena.sample.exception.service.AccountServiceUnavailableRuntimeException;
import com.cjrequena.sample.proto.*;
import com.cjrequena.sample.proto.AccountServiceGrpc.AccountServiceBlockingStub;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.ErrorInfo;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

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

  @CircuitBreaker(name = "default", fallbackMethod = "retrieveFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public Account retrieveById(UUID id) throws AccountNotFoundRuntimeException {
    try {
      if (log.isTraceEnabled()) {
        log.trace("Entering retrieveById with ID={}", id);
      }
      Account account = Optional.ofNullable(id)
        .map(UUID::toString)
        .map(val -> {
          if (log.isTraceEnabled()) {
            log.trace("Building RetrieveAccountByIdRequest with ID={}", val);
          }
          return RetrieveAccountByIdRequest.newBuilder().setId(val).build();
        })
        .map(req -> {
          if (log.isTraceEnabled()) {
            log.trace("Calling account-service with request={}", req);
          }
          return accountServiceBlockingStub.retrieveAccountById(req);
        })
        .map(resp -> {
          if (log.isTraceEnabled()) {
            log.trace("Received response from account-service: {}", resp);
          }
          return resp.getAccount();
        })
        .orElseThrow(() -> new IllegalArgumentException("ID must not be null"));

      if (log.isTraceEnabled()) {
        log.trace("Exiting retrieveById successfully for ID={}", id);
      }
      return account;

    } catch (StatusRuntimeException ex) {
      com.google.rpc.Status status = StatusProto.fromThrowable(ex);

      if (status != null) {
        for (Any detail : status.getDetailsList()) {
          if (detail.is(ErrorInfo.class)) {
            try {
              ErrorInfo errorInfo = detail.unpack(ErrorInfo.class);
              log.warn("ErrorInfo received from service -> domain={}, reason={}, metadata={}",
                errorInfo.getDomain(),
                errorInfo.getReason(),
                errorInfo.getMetadataMap());
              if (log.isTraceEnabled()) {
                log.trace("Raw ErrorInfo detail: {}", detail);
              }
            } catch (InvalidProtocolBufferException ipbex) {
              log.warn("Failed to unpack ErrorInfo details", ipbex);
            }
          }
        }

        if (status.getCode() == Status.Code.NOT_FOUND.value()) {
          log.info("Account not found for ID={}", id);
          throw new AccountNotFoundRuntimeException("Account not found ID: " + id, ex);
        } else if (status.getCode() == Status.Code.UNAVAILABLE.value()) {
          log.error("Account-service is UNAVAILABLE while retrieving ID={}", id);
          throw new AccountServiceUnavailableRuntimeException("account-service UNAVAILABLE");
        }
      }

      log.error("Unhandled gRPC error when retrieving ID={}", id, ex);
      throw ex;
    }
  }


  public Account retrieveFallbackMethod(UUID id, Throwable ex) throws Throwable {
    log.warn("retrieveFallbackMethod", ex.getCause());
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
    Objects.requireNonNull(dto, "AccountWithdraw cannot be null");
    Objects.requireNonNull(dto.getAccountId(), "AccountId cannot be null");
    Objects.requireNonNull(dto.getAmount(), "Amount cannot be null");

    WithdrawRequest request = WithdrawRequest.newBuilder()
      .setAccountId(dto.getAccountId().toString())
      .setAmount(dto.getAmount().toString())
      .build();

    try {
      accountServiceBlockingStub.withdraw(request);
      log.info("Withdraw successful for accountId={}, amount={}", dto.getAccountId(), dto.getAmount());
    } catch (Exception ex) {
      log.error("Withdraw failed for accountId={}, amount={}, error={}",
        dto.getAccountId(), dto.getAmount(), ex.getMessage(), ex);
      throw new AccountServiceUnavailableRuntimeException("Withdraw request failed", ex);
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
