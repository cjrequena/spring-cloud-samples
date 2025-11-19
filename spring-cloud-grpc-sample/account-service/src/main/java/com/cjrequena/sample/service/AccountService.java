package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.AccountNotFoundException;
import com.cjrequena.sample.domain.exception.DomainException;
import com.cjrequena.sample.domain.exception.OptimisticConcurrencyException;
import com.cjrequena.sample.domain.mapper.AccountMapper;
import com.cjrequena.sample.domain.model.Account;
import com.cjrequena.sample.domain.model.DepositAccount;
import com.cjrequena.sample.domain.model.WithdrawAccount;
import com.cjrequena.sample.persistence.entity.AccountEntity;
import com.cjrequena.sample.persistence.repository.AccountRepository;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Log4j2
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = DomainException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService {

  private final AccountMapper accountMapper;
  private final AccountRepository accountRepository;

  public void create(Account account) {
    AccountEntity entity = this.accountMapper.toEntity(account);
    this.accountRepository.create(entity);
  }

  @Transactional(readOnly = true)
  public Account retrieveById(UUID id) throws AccountNotFoundException {
    return this.accountRepository
      .findById(id)
      .map(this.accountMapper::toAccountDomain)
      .orElseThrow(() -> {
        String errorMessage = String.format("The account :: %s :: was not found", id);
        if (log.isTraceEnabled()) {
          log.trace(errorMessage);
        }
        return new AccountNotFoundException(errorMessage);
      });
  }

  @Transactional(readOnly = true)
  public List<Account> retrieve() {
    return this.accountRepository.findAll().stream().map(this.accountMapper::toAccountDomain).collect(Collectors.toList());
  }

  public void update(Account account) throws AccountNotFoundException, OptimisticConcurrencyException {
    UUID accountId = account.getId();
    this.accountRepository
      .findWithLockingById(accountId)
      .ifPresentOrElse(entity -> {
          final long expectedVersion = entity.getVersion();
          try {
            this.accountMapper.updateEntityFromAccount(account, entity);
            this.accountRepository.save(entity);
          } catch (ObjectOptimisticLockingFailureException ex) {
            String errorMessage = String.format(
              "Optimistic concurrency control error in account :: %s :: actual version doesn't match expected version %s",
              accountId, expectedVersion
            );
            log.trace(errorMessage, ex);
            throw new OptimisticConcurrencyException(errorMessage);
          }
        },
        () -> {
          String errorMessage = String.format("The account :: %s :: was not found", accountId);
          log.trace(errorMessage);
          throw new AccountNotFoundException(errorMessage);
        });
  }

  public Account patch(UUID id, JsonPatch patchDocument) {
    return null;
  }

  public Account patch(UUID id, JsonMergePatch mergePatchDocument) {
    return null;
  }

  public void delete(UUID id) throws AccountNotFoundException {
    this.accountRepository
      .findById(id)
      .ifPresentOrElse(this.accountRepository::delete, () -> {
        String errorMessage = String.format("The account :: %s :: was not found", id);
        log.trace(errorMessage);
        throw new AccountNotFoundException(errorMessage);
      });
  }

  public void deposit(DepositAccount depositAccount) throws AccountNotFoundException, OptimisticConcurrencyException {
    Account account = this.retrieveById(depositAccount.getAccountId());
    account.setBalance(account.getBalance().add(depositAccount.getAmount()));
    this.update(account);
  }

  public void withdraw(WithdrawAccount withdrawAccount) throws AccountNotFoundException, OptimisticConcurrencyException {
    Account account = this.retrieveById(withdrawAccount.getAccountId());
    account.setBalance(account.getBalance().subtract(withdrawAccount.getAmount()));
    this.update(account);
  }
}
