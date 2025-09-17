package com.cjrequena.sample.service;

import com.cjrequena.sample.exception.GrpcExceptionHandler;
import com.cjrequena.sample.exception.service.AccountNotFoundException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
import com.cjrequena.sample.persistence.entity.AccountEntity;
import com.cjrequena.sample.persistence.repository.AccountRepository;
import com.cjrequena.sample.proto.*;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Log4j2
@GrpcService
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceGrpc extends com.cjrequena.sample.proto.AccountServiceGrpc.AccountServiceImplBase {

  private final AccountMapper accountMapper;
  private final AccountRepository accountRepository;
  private final GrpcExceptionHandler grpcExceptionHandler;

  @Override
  public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {

    Account account = Account
      .newBuilder()
      .setId(UUID.randomUUID().toString())
      .setOwner(request.getAccount().getOwner())
      .setBalance(request.getAccount().getBalance())
      .build();

    final AccountEntity entity = this.accountMapper.toEntity(account);
    this.accountRepository.create(entity);

    CreateAccountResponse response = CreateAccountResponse
      .newBuilder()
      .setSuccess(true)
      .setMessage("Account created successfully")
      .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void retrieveAccountById(RetrieveAccountByIdRequest request, StreamObserver<RetrieveAccountByIdResponse> responseObserver) {
    UUID accountId = UUID.fromString(request.getId());
    this.accountRepository.findById(accountId)
      .map(accountMapper::toAccount)
      .map(account -> RetrieveAccountByIdResponse.newBuilder().setAccount(account).build())
      .ifPresentOrElse(
        response -> {
          responseObserver.onNext(response);
          responseObserver.onCompleted();
        },
        () -> {
          String errorMessage = String.format("The account :: %s :: was not found", accountId);
          final StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new AccountNotFoundException(errorMessage));
          responseObserver.onError(err);
        }
      );
  }

  @Override
  public void retrieveAccounts(RetrieveAccountsRequest request, StreamObserver<RetrieveAccountsResponse> responseObserver) {
    log.debug("Retrieving accounts with request: {}", request);
    final List<Account> accounts = this.accountRepository.findAll()
      .stream()
      .map(accountMapper::toAccount)
      .toList();
    RetrieveAccountsResponse response = RetrieveAccountsResponse
      .newBuilder()
      .addAllAccounts(accounts)
      .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateAccount(UpdateAccountRequest request, StreamObserver<UpdateAccountResponse> responseObserver) {
    Account account = request.getAccount();
    UUID accountId = UUID.fromString(account.getId());
    this.accountRepository
      .findWithLockingById(accountId)
      .ifPresentOrElse(accountEntity -> {
          final long expectedVersion = accountEntity.getVersion();
          try {
            this.accountMapper.updateEntityFromAccount(account, accountEntity);
            this.accountRepository.save(accountEntity);
            UpdateAccountResponse response = UpdateAccountResponse
              .newBuilder()
              .setSuccess(true)
              .setMessage("Account updated successfully")
              .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          } catch (ObjectOptimisticLockingFailureException ex) {
            String errorMessage = String.format(
              "Optimistic concurrency control error in account :: %s :: actual version doesn't match expected version %s",
              accountId, expectedVersion
            );
            log.trace(errorMessage);
            StatusRuntimeException err = this.buildErrorResponse(new OptimisticConcurrencyException(errorMessage));
            responseObserver.onError(err);
          }
        },
        () -> {
          String errorMessage = String.format("The account :: %s :: was not found", accountId);
          log.trace(errorMessage);
          StatusRuntimeException err = this.buildErrorResponse(new AccountNotFoundException(errorMessage));
          responseObserver.onError(err);
        }
      );
  }

  @Override
  public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
    UUID accountId = UUID.fromString(request.getId());
    this.accountRepository
      .findById(accountId)
      .ifPresentOrElse(this.accountRepository::delete, () -> {
        String errorMessage = String.format("The account :: %s :: was not found", accountId);
        final StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new AccountNotFoundException(errorMessage));
        responseObserver.onError(err);
      });

    DeleteAccountResponse response = DeleteAccountResponse.newBuilder().setSuccess(true).setMessage("Account deleted successfully").build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void deposit(DepositRequest request, StreamObserver<DepositResponse> responseObserver) {
    UUID accountId = UUID.fromString(request.getAccountId());
    BigDecimal amount = BigDecimal.valueOf(Long.parseLong(request.getAmount()));
    this.accountRepository
      .findWithLockingById(accountId)
      .ifPresentOrElse(accountEntity -> {
          final long expectedVersion = accountEntity.getVersion();
          try {
            BigDecimal currentBalance = accountEntity.getBalance();
            BigDecimal newBalance = currentBalance.add(amount);
            accountEntity.setBalance(newBalance);
            this.accountRepository.save(accountEntity);
            DepositResponse response = DepositResponse
              .newBuilder()
              .setSuccess(true)
              .setMessage("Account deposited successfully")
              .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          } catch (ObjectOptimisticLockingFailureException ex) {
            String errorMessage = String.format(
              "Optimistic concurrency control error in account :: %s :: actual version doesn't match expected version %s",
              accountId, expectedVersion
            );
            log.trace(errorMessage);
            StatusRuntimeException err = this.buildErrorResponse(new OptimisticConcurrencyException(errorMessage));
            responseObserver.onError(ex);
          }
        },
        () -> {
          String errorMessage = String.format("The account :: %s :: was not found", accountId);
          log.trace(errorMessage);
          StatusRuntimeException ex = this.buildErrorResponse(new AccountNotFoundException(errorMessage));
          responseObserver.onError(ex);
        }
      );
  }

  @Override
  public void withdraw(WithdrawRequest request, StreamObserver<WithdrawResponse> responseObserver) {
    UUID accountId = UUID.fromString(request.getAccountId());
    BigDecimal amount = new BigDecimal(request.getAmount()).setScale(2, RoundingMode.HALF_UP);
    ;
    this.accountRepository
      .findWithLockingById(accountId)
      .ifPresentOrElse(accountEntity -> {
          final long expectedVersion = accountEntity.getVersion();
          try {
            BigDecimal currentBalance = accountEntity.getBalance();
            BigDecimal newBalance = currentBalance.subtract(amount);
            accountEntity.setBalance(newBalance);
            this.accountRepository.save(accountEntity);
            WithdrawResponse response = WithdrawResponse
              .newBuilder()
              .setSuccess(true)
              .setMessage("Account withdrew successfully")
              .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          } catch (ObjectOptimisticLockingFailureException ex) {
            String errorMessage = String.format(
              "Optimistic concurrency control error in account :: %s :: actual version doesn't match expected version %s",
              accountId, expectedVersion
            );
            log.trace(errorMessage);
            StatusRuntimeException err = this.buildErrorResponse(new OptimisticConcurrencyException(errorMessage));
            responseObserver.onError(ex);
          }
        },
        () -> {
          String errorMessage = String.format("The account :: %s :: was not found", accountId);
          log.trace(errorMessage);
          StatusRuntimeException ex = this.buildErrorResponse(new AccountNotFoundException(errorMessage));
          responseObserver.onError(ex);
        }
      );
  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private StatusRuntimeException buildErrorResponse(Throwable err) {
    var code = switch (err) {
      case AccountNotFoundException ignored -> Code.NOT_FOUND;
      case OptimisticConcurrencyException ignored -> Code.ABORTED;
      //      case UnimplementedFilterException ignored -> Code.UNIMPLEMENTED;
      //      case ValidationException ignored -> Code.INVALID_ARGUMENT;
      case NumberFormatException ignored -> Code.INVALID_ARGUMENT;
      default -> {
        log.error("Unexpected exception: {}", err.getMessage(), err);
        yield Code.INTERNAL;
      }
    };

    if (!code.equals(Code.INTERNAL)) {
      log.warn(String.format("Warning: %s", err.getMessage()), err);
    }

    var builder = Status.newBuilder().setCode(code.getNumber());
    if (err.getMessage() != null) {
      builder = builder.setMessage(err.getMessage());
    }

    return StatusProto.toStatusRuntimeException(builder.build());
  }
}
