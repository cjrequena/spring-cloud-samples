package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.domain.model.Account;
import com.cjrequena.sample.domain.model.DepositAccount;
import com.cjrequena.sample.domain.model.WithdrawAccount;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.controller.BadRequestException;
import com.cjrequena.sample.exception.controller.ConflictException;
import com.cjrequena.sample.exception.controller.NotFoundException;
import com.cjrequena.sample.exception.service.AccountNotFoundException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyException;
import com.cjrequena.sample.mapper.AccountMapper;
import com.cjrequena.sample.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = AccountController.ENDPOINT, headers = {AccountController.ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountController {

  public static final String ENDPOINT = "/account-service/api/";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;

  private final AccountService accountService;
  private final AccountMapper accountMapper;

  @PostMapping(
    path = "/accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> create(@Valid @RequestBody AccountDTO dto, HttpServletRequest request, UriComponentsBuilder ucBuilder) {
    Account account = Account.createNewWith(dto.getOwner(), dto.getBalance());
    accountService.create(account);
    URI resourcePath = ucBuilder.path(request.getServletPath() + "/{id}").buildAndExpand(dto.getId()).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    headers.setLocation(resourcePath);
    return ResponseEntity.created(resourcePath).headers(headers).build();
  }

  @GetMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<AccountDTO> retrieveById(@PathVariable(value = "id") UUID id) throws NotFoundException {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      Account account = this.accountService.retrieveById(id);
      AccountDTO dto = this.accountMapper.toDTO(account);
      return new ResponseEntity<>(dto, headers, HttpStatus.OK);
    } catch (AccountNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @GetMapping(
    path = "/accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<List<AccountDTO>> retrieve() {
    List<Account> accountList = this.accountService.retrieve();
    List<AccountDTO> dtoList = this.accountMapper.toDTOList(accountList);
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    return new ResponseEntity<>(dtoList, headers, HttpStatus.OK);
  }

  @PutMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody AccountDTO dto)
    throws NotFoundException, ConflictException {
    try {
      Account account = Account.createNewWith(id, dto.getOwner(), dto.getBalance());
      this.accountService.update(account);
      HttpHeaders headers = new HttpHeaders();
      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    } catch (AccountNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    } catch (OptimisticConcurrencyException ex) {
      throw new ConflictException(ex.getMessage());
    }
  }

  @DeleteMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<Void> delete(@PathVariable(value = "id") UUID id) throws NotFoundException {
    try {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      this.accountService.delete(id);
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (AccountNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  //-----------------------
  // Commands
  //----------------------
  @PostMapping(path = "/accounts/deposit", produces = {APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deposit(@RequestBody DepositAccountDTO dto)
    throws NotFoundException, BadRequestException {
    try {
      DepositAccount depositAccount = DepositAccount
        .builder()
        .accountId(dto.getAccountId())
        .amount(dto.getAmount())
        .build();
      this.accountService.deposit(depositAccount);
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (AccountNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    } catch (OptimisticConcurrencyException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @PostMapping(path = "/accounts/withdraw", produces = {APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> withdraw(@RequestBody WithdrawAccountDTO dto) throws NotFoundException, BadRequestException {
    try {
      WithdrawAccount withdrawAccount = WithdrawAccount
        .builder()
        .accountId(dto.getAccountId())
        .amount(dto.getAmount())
        .build();
      this.accountService.withdraw(withdrawAccount);
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
    } catch (AccountNotFoundException ex) {
      throw new NotFoundException(ex.getMessage());
    } catch (OptimisticConcurrencyException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }
}
