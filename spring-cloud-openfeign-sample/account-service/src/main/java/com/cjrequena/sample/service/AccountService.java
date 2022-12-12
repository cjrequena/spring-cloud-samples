package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.AccountEntity;
import com.cjrequena.sample.db.repository.AccountRepository;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService {

  private final AccountMapper accountMapper;
  private final AccountRepository accountRepository;

  public void create(AccountDTO dto) {
    dto.setId(UUID.randomUUID());
    AccountEntity entity = this.accountMapper.toEntity(dto);
    this.accountRepository.create(entity);
  }

  @Transactional(readOnly = true)
  public AccountDTO retrieveById(UUID id) throws AccountNotFoundServiceException {
    Optional<AccountEntity> optional = this.accountRepository.findById(id);
    if (optional.isEmpty()) {
      throw new AccountNotFoundServiceException("The account :: " + id + " :: was not Found");
    }
    return accountMapper.toDTO(optional.get());
  }

  @Transactional(readOnly = true)
  public List<AccountDTO> retrieve() {
    return this.accountRepository.findAll().stream().map(this.accountMapper::toDTO).collect(Collectors.toList());
  }

  public void update(AccountDTO dto) throws AccountNotFoundServiceException, OptimisticConcurrencyServiceException {
    Optional<AccountEntity> optional = this.accountRepository.findWithLockingById(dto.getId());
    if (optional.isEmpty()) {
      throw new AccountNotFoundServiceException("The account :: " + dto.getId() + " :: was not Found");
    }
    AccountDTO _dto = this.accountMapper.toDTO(optional.get());
    if (_dto.getVersion().equals(dto.getVersion())) {
      AccountEntity entity = this.accountMapper.toEntity(dto);
      this.accountRepository.save(entity);
      log.debug("Updated account with id {}", entity.getId());
    } else {
      log.trace(
        "Optimistic concurrency control error in account :: {} :: actual version doesn't match expected version {}",
        _dto.getId(),
        _dto.getVersion());
      throw new OptimisticConcurrencyServiceException(
        "Optimistic concurrency control error in account :: " + _dto.getId() + " :: actual version doesn't match expected version "
          + _dto.getVersion());
    }
  }

  public AccountDTO patch(UUID id, JsonPatch patchDocument) {
    return null;
  }

  public AccountDTO patch(UUID id, JsonMergePatch mergePatchDocument) {
    return null;
  }

  public void delete(UUID id) throws AccountNotFoundServiceException {
    Optional<AccountEntity> optional = this.accountRepository.findById(id);
    if (optional.isEmpty()) {
      throw new AccountNotFoundServiceException("The account :: " + id + " :: was not Found");
    }
    this.accountRepository.deleteById(id);
  }

  public void deposit(DepositAccountDTO depositAccountDTO) throws AccountNotFoundServiceException, OptimisticConcurrencyServiceException {
    AccountDTO dto = this.retrieveById(depositAccountDTO.getAccountId());
    dto.setBalance(dto.getBalance().add(depositAccountDTO.getAmount()));
    this.update(dto);
  }

  public void withdraw(WithdrawAccountDTO withdrawAccountDTO) throws AccountNotFoundServiceException, OptimisticConcurrencyServiceException {
    AccountDTO dto = this.retrieveById(withdrawAccountDTO.getAccountId());
    dto.setBalance(dto.getBalance().subtract(withdrawAccountDTO.getAmount()));
    this.update(dto);
  }
}
