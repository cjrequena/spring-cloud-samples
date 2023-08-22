package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.AccountEntity;
import com.cjrequena.sample.db.repository.AccountRepository;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
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

  public Mono<AccountEntity> create(AccountEntity entity) {
    entity.setId(UUID.randomUUID());
    return accountRepository.save(entity);
  }

  public Mono<AccountEntity> retrieveById(UUID id) {
    return accountRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new AccountNotFoundServiceException("The account :: " + id + " :: was not Found")));
  }

  public Flux<AccountEntity> retrieve() {
    return this.accountRepository.findAll();
  }

  public Mono<AccountEntity> update(AccountEntity entity) {

    return accountRepository
      .findById(entity.getId())
      .switchIfEmpty(Mono.error(new AccountNotFoundServiceException("The account :: " + entity.getId() + " :: was not Found")))
      .map(Optional::of)
      .flatMap(optionalAccount -> {
        if (optionalAccount.isPresent()) {
          AccountEntity _entity = optionalAccount.get();
          if (_entity.getVersion().equals(entity.getVersion())) {
            return accountRepository.save(entity);
          } else {
            log.trace(
              "Optimistic concurrency control error in account :: {} :: actual version doesn't match expected version {}",
              _entity.getId(),
              _entity.getVersion());
            return Mono.error(new OptimisticConcurrencyServiceException(
              "Optimistic concurrency control error in account :: " + _entity.getId() + " :: actual version doesn't match expected version "
                + _entity.getVersion()));
          }

        }
        return Mono.empty();
      });
  }

  public Mono<Void> delete(UUID id) {
    return this.accountRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new AccountNotFoundServiceException("The account :: " + id + " :: was not Found")))
      .flatMap(entity -> this.accountRepository.deleteById(entity.getId()));
  }

  public Mono<AccountEntity> deposit(DepositAccountDTO depositAccountDTO) {
    return this.retrieveById(depositAccountDTO.getAccountId())
      .flatMap(_entity -> {
        _entity.setBalance(_entity.getBalance().add(depositAccountDTO.getAmount()));
        return this.update(_entity);
      });
  }

  public Mono<AccountEntity> withdraw(WithdrawAccountDTO withdrawAccountDTO) {
    return this.retrieveById(withdrawAccountDTO.getAccountId())
      .flatMap(_entity -> {
        _entity.setBalance(_entity.getBalance().subtract(withdrawAccountDTO.getAmount()));
        return this.update(_entity);
      });
  }

}
