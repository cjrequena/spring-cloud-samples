package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.AccountEntity;
import com.cjrequena.sample.db.repository.AccountRepository;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.exception.service.InsufficientBalanceServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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

  public Mono<AccountDTO> create(AccountDTO dto) {
    AccountEntity entity = this.accountMapper.toEntity(dto);
    entity.setId(UUID.randomUUID());
    Mono<AccountDTO> dto$ = this.accountRepository.save(entity).map(this.accountMapper::toDTO);
    return dto$;
  }

  public Mono<AccountDTO> retrieveById(UUID id) {
    return accountRepository
      .findById(id)
      .switchIfEmpty(Mono.error(new AccountNotFoundServiceException("The account :: " + id + " :: was not Found")))
      .map(this.accountMapper::toDTO);
  }

  public Flux<AccountDTO> retrieve() {
    Flux<AccountDTO> dtos$ = this.accountRepository.findAll().map(this.accountMapper::toDTO);
    return dtos$;
  }

  public Mono<AccountEntity> update(AccountDTO dto) {
    return accountRepository
      .findById(dto.getId())
      .switchIfEmpty(Mono.error(new AccountNotFoundServiceException("The account :: " + dto.getId() + " :: was not Found")))
      .map(Optional::of)
      .flatMap(optionalAccount -> {
        if (optionalAccount.isPresent()) {
          AccountEntity _entity = optionalAccount.get();
          if (_entity.getVersion().equals(dto.getVersion())) {
            return accountRepository.save(this.accountMapper.toEntity(dto));
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
      }).onErrorResume(ex -> {
        log.error(ex.getMessage());
        return Mono.error(ex);
      });
  }

  public Mono<AccountEntity> withdraw(WithdrawAccountDTO withdrawAccountDTO) {
    return this.retrieveById(withdrawAccountDTO.getAccountId())
      .flatMap(_entity -> {
        BigDecimal amount = _entity.getBalance().subtract(withdrawAccountDTO.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) == -1) {
          return Mono.error(new InsufficientBalanceServiceException("Insufficient balance on account with id " + _entity.getId()));
        } else {
          _entity.setBalance(amount);
          return this.update(_entity);
        }
      })
      .onErrorResume(ex -> {
        log.error(ex.getMessage());
        return Mono.error(ex);
      });
  }

}
