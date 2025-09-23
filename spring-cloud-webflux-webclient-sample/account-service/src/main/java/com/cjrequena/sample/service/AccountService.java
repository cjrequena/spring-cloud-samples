package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.model.Account;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.exception.service.InsufficientBalanceServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyException;
import com.cjrequena.sample.mapper.AccountMapper;
import com.cjrequena.sample.persistence.entity.AccountEntity;
import com.cjrequena.sample.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService {

  private final AccountMapper accountMapper;
  private final AccountRepository accountRepository;

  public Mono<Account> create(Account account) {
    log.info("Creating new account with initial data={}", account);
    AccountEntity entity = accountMapper.toEntity(account);
    return accountRepository.save(entity)
      .map(accountMapper::toAccountDomain)
      .doOnSuccess(saved -> log.info("Account created successfully with id={}", saved.getId()))
      .doOnError(error -> log.error("Failed to create account: {}", error.getMessage(), error));
  }

  public Mono<Account> retrieveById(UUID id) {
    log.debug("Retrieving account by id={}", id);
    return accountRepository.findById(id)
      .switchIfEmpty(Mono.error(() -> new AccountNotFoundServiceException("Account not found for id=" + id)))
      .map(accountMapper::toAccountDomain)
      .doOnSuccess(account -> log.info("Account retrieved successfully with id={}", id))
      .doOnError(error -> log.warn("Failed to retrieve account with id={}: {}", id, error.getMessage()));
  }

  public Flux<Account> retrieve() {
    log.debug("Retrieving all accounts");
    return accountRepository.findAll()
      .map(accountMapper::toAccountDomain)
      .doOnComplete(() -> log.info("Accounts list retrieved successfully"))
      .doOnError(error -> log.error("Failed to retrieve accounts list: {}", error.getMessage(), error));
  }

  public Mono<AccountEntity> update(Account account) {
    UUID accountId = account.getId();
    log.debug("Updating account with id={}", accountId);

    return accountRepository.findById(accountId)
      .switchIfEmpty(Mono.error(() -> new AccountNotFoundServiceException("Account not found for id=" + accountId)))
      .flatMap(existingEntity -> {
        long expectedVersion = existingEntity.getVersion();

        accountMapper.updateEntityFromAccount(account, existingEntity);

        return accountRepository.save(existingEntity)
          .switchIfEmpty(Mono.error(() -> {
            String errorMessage = String.format(
              "Optimistic concurrency control error for account %s: expected version=%s but version mismatch",
              accountId, expectedVersion
            );
            log.warn(errorMessage);
            return new OptimisticConcurrencyException(errorMessage);
          }))
          .doOnSuccess(saved -> log.info("Account {} updated successfully, version {} → {}",
            accountId, expectedVersion, saved.getVersion()))
          .doOnError(error -> log.error("Failed to update account with id={}: {}", accountId, error.getMessage(), error));
      });
  }

  public Mono<Void> delete(UUID id) {
    log.debug("Deleting account with id={}", id);
    return accountRepository.findById(id)
      .switchIfEmpty(Mono.error(() -> new AccountNotFoundServiceException("Account not found for id=" + id)))
      .flatMap(entity -> accountRepository.deleteById(entity.getId()))
      .doOnSuccess(v -> log.info("Account deleted successfully with id={}", id))
      .doOnError(error -> log.error("Failed to delete account with id={}: {}", id, error.getMessage(), error));
  }

  public Mono<AccountEntity> deposit(DepositAccountDTO depositAccountDTO) {
    log.info("Depositing amount={} into account id={}", depositAccountDTO.getAmount(), depositAccountDTO.getAccountId());
    return retrieveById(depositAccountDTO.getAccountId())
      .flatMap(_entity -> {
        _entity.setBalance(_entity.getBalance().add(depositAccountDTO.getAmount()));
        return update(_entity);
      })
      .doOnSuccess(updated -> log.info("Deposit successful for account id={}, new balance={}", updated.getId(), updated.getBalance()))
      .doOnError(error -> log.error("Deposit failed for account id={}: {}", depositAccountDTO.getAccountId(), error.getMessage(), error));
  }

  public Mono<AccountEntity> withdraw(WithdrawAccountDTO withdrawAccountDTO) {
    log.info("Withdrawing amount={} from account id={}", withdrawAccountDTO.getAmount(), withdrawAccountDTO.getAccountId());
    return retrieveById(withdrawAccountDTO.getAccountId())
      .flatMap(_entity -> {
        BigDecimal remaining = _entity.getBalance().subtract(withdrawAccountDTO.getAmount());
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
          String msg = String.format("Insufficient balance on account id=%s for withdraw amount=%s",
            withdrawAccountDTO.getAccountId(), withdrawAccountDTO.getAmount());
          log.warn(msg);
          return Mono.error(new InsufficientBalanceServiceException(msg));
        } else {
          _entity.setBalance(remaining);
          return update(_entity);
        }
      })
      .doOnSuccess(updated -> log.info("Withdraw successful for account id={}, new balance={}", updated.getId(), updated.getBalance()))
      .doOnError(error -> log.error("Withdraw failed for account id={}: {}", withdrawAccountDTO.getAccountId(), error.getMessage(), error));
  }
}
