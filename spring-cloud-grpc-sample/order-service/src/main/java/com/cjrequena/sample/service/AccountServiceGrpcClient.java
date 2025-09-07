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
import com.cjrequena.sample.proto.RetrieveAccountByIdRequest;
import com.cjrequena.sample.proto.RetrieveAccountByIdResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  @CircuitBreaker(name = "default", fallbackMethod = "depositFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public ResponseEntity<Void> deposit(DepositAccountDTO dto) {
    //    return this.accountServiceFeignClient.deposit(dto);
    return null;
  }

  public ResponseEntity<Void> depositFallbackMethod(DepositAccountDTO dto, Throwable ex) throws GrpcException {
    log.debug("depositFallbackMethod");
    throw new GrpcException(ex.getMessage(), ex);
  }

  @CircuitBreaker(name = "default", fallbackMethod = "withdrawFallbackMethod")
  @Bulkhead(name = "default")
  @Retry(name = "default")
  public ResponseEntity<Void> withdraw(WithdrawAccountDTO dto) throws GrpcException {
    //return this.accountServiceFeignClient.withdraw(dto);
    return null;
  }

  public ResponseEntity<Void> withdrawFallbackMethod(WithdrawAccountDTO dto, Throwable ex) throws GrpcException {
    log.debug("withdrawFallbackMethod");
    throw new GrpcException(ex.getMessage(), ex);
  }
}
