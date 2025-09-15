package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.AccountEntity;
import com.cjrequena.sample.db.repository.AccountRepository;
import com.cjrequena.sample.exception.service.AccountNotFoundException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
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
public class AccountService extends AccountServiceGrpc.AccountServiceImplBase {

  private final AccountMapper accountMapper;
  private final AccountRepository accountRepository;

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
          StatusRuntimeException ex = this.buildErrorResponse(
            new AccountNotFoundException("The account :: " + accountId + " :: was not Found")
          );
          responseObserver.onError(ex);
        }
      );
  }

  @Override
  public void retrieveAccounts(RetrieveAccountsRequest request, StreamObserver<RetrieveAccountsResponse> responseObserver) {
    try {
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
    } catch (IllegalArgumentException ex) {
      log.warn("Invalid request parameters: {}", ex.getMessage());
      // TODO buildErrorResponse
      // responseObserver.onError(builtResponseError);
    } catch (Exception ex) {
      log.error("Error retrieving accounts", ex);
      // TODO buildErrorResponse
      // responseObserver.onError(builtResponseError);
    }

  }

  @Override
  public void updateAccount(UpdateAccountRequest request, StreamObserver<UpdateAccountResponse> responseObserver) {
    log.debug("Updating account with request: {}", request);
    Account account = request.getAccount();
    UUID accountId = UUID.fromString(account.getId());
    this.accountRepository
      .findWithLockingById(accountId)
      .ifPresentOrElse(accountEntity -> {
          final long expectedVersion = accountEntity.getVersion();
          try {
            this.accountMapper.updateEntityFromAccount(account, accountEntity);
            this.accountRepository.update(accountEntity);
            UpdateAccountResponse response = UpdateAccountResponse
              .newBuilder()
              .setSuccess(true)
              .setMessage("Account updated successfully")
              .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          } catch (ObjectOptimisticLockingFailureException ex) {
            log.trace(
              "Optimistic concurrency control error in account :: {} :: actual version doesn't match expected version {}",
              accountId,
              expectedVersion);
            StatusRuntimeException err = this.buildErrorResponse(
              new OptimisticConcurrencyException(
                "Optimistic concurrency control error in account :: " + accountId + " :: actual version doesn't match expected version "
                  + expectedVersion));
            responseObserver.onError(ex);
          }
        },
        () -> {
          StatusRuntimeException ex = this.buildErrorResponse(
            new AccountNotFoundException("The account :: " + accountId + " :: was not Found")
          );
          responseObserver.onError(ex);
        }
      );
  }

  @Override
  public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
    UUID accountId = UUID.fromString(request.getId());
    this.accountRepository
      .findById(accountId)
      .ifPresentOrElse(this.accountRepository::delete, () -> {
        StatusRuntimeException ex = this.buildErrorResponse(
          new AccountNotFoundException("The account :: " + accountId + " :: was not Found")
        );
        responseObserver.onError(ex);
      });
  }

  @Override
  public void deposit(DepositRequest request, StreamObserver<DepositResponse> responseObserver) {
    super.deposit(request, responseObserver);
  }

  @Override
  public void withdraw(WithdrawRequest request, StreamObserver<WithdrawResponse> responseObserver) {
    log.debug("Withdrawing account with request: {}", request);
    UUID accountId = UUID.fromString(request.getAccountId());
    BigDecimal amount = BigDecimal.valueOf(Long.parseLong(request.getAmount()));

    this.accountRepository
      .findWithLockingById(accountId)
      .ifPresentOrElse(accountEntity -> {
          final long expectedVersion = accountEntity.getVersion();
          try {
            BigDecimal currentBalance = accountEntity.getBalance();
            BigDecimal newBalance = currentBalance.subtract(amount);
            accountEntity.setBalance(newBalance);
            this.accountRepository.update(accountEntity);
            WithdrawResponse response = WithdrawResponse
              .newBuilder()
              .setSuccess(true)
              .setMessage("Account withdrew successfully")
              .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
          } catch (ObjectOptimisticLockingFailureException ex) {
            log.trace(
              "Optimistic concurrency control error in account :: {} :: actual version doesn't match expected version {}",
              accountId,
              expectedVersion);
            StatusRuntimeException err = this.buildErrorResponse(
              new OptimisticConcurrencyException(
                "Optimistic concurrency control error in account :: " + accountId + " :: actual version doesn't match expected version "
                  + expectedVersion));
            responseObserver.onError(ex);
          }
        },
        () -> {
          StatusRuntimeException ex = this.buildErrorResponse(
            new AccountNotFoundException("The account :: " + accountId + " :: was not Found")
          );
          responseObserver.onError(ex);
        }
      );
  }

  // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  private StatusRuntimeException buildErrorResponse(Throwable err) {
    var code = switch (err) {
      case AccountNotFoundException ignored -> Code.NOT_FOUND;
      case OptimisticConcurrencyException ignored -> Code.ABORTED;
      //      case ExperienceNotFoundException ignored -> Code.NOT_FOUND;
      //      case NullExperienceSourceException ignored -> Code.NOT_FOUND;
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
