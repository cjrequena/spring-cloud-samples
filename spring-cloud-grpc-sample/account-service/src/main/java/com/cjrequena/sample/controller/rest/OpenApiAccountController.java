//package com.cjrequena.sample.controller.rest;
//
//import com.cjrequena.sample.common.Constants;
//import com.cjrequena.sample.domain.model.Account;
//import com.cjrequena.sample.domain.model.DepositAccount;
//import com.cjrequena.sample.domain.model.WithdrawAccount;
//import com.cjrequena.sample.exception.controller.BadRequestException;
//import com.cjrequena.sample.exception.controller.ConflictException;
//import com.cjrequena.sample.exception.controller.NotFoundException;
//import com.cjrequena.sample.exception.service.AccountNotFoundException;
//import com.cjrequena.sample.exception.service.OptimisticConcurrencyException;
//import com.cjrequena.sample.mapper.AccountMapper;
//import com.cjrequena.sample.openapi.controller.dto.AccountDTO;
//import com.cjrequena.sample.openapi.controller.dto.DepositAccountDTO;
//import com.cjrequena.sample.openapi.controller.dto.WithdrawAccountDTO;
//import com.cjrequena.sample.openapi.controller.rest.AccountsApi;
//import com.cjrequena.sample.service.AccountService;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import java.net.URI;
//import java.util.List;
//import java.util.UUID;
//
//import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
//
//@Slf4j
//@RestController
//@RequestMapping(value = OpenApiAccountController.ENDPOINT, headers = {OpenApiAccountController.ACCEPT_VERSION})
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class OpenApiAccountController implements AccountsApi {
//
//  public static final String ENDPOINT = "/account-service/api/";
//  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;
//  private final AccountService accountService;
//  private final AccountMapper accountMapper;
//
//  @Override
//  public ResponseEntity<Void> create(String acceptVersion, AccountDTO dto) {
//    // Create new account entity
//    Account account = Account.createNewWith(dto.getOwner(), dto.getBalance());
//    accountService.create(account); // sets account.id
//
//    // Build URI for Location header from current request
//    URI resourcePath = ServletUriComponentsBuilder.fromCurrentRequest()         // uses current request URL
//      .path("/{id}")               // append /{id}
//      .buildAndExpand(account.getId()).toUri();
//
//    // Set headers
//    HttpHeaders headers = new HttpHeaders();
//    headers.set("Cache-Control", "no-store, private, max-age=0");
//    headers.setLocation(resourcePath);
//
//    // Return ResponseEntity with Location header
//    return ResponseEntity.created(resourcePath).headers(headers).build();
//  }
//
//  @Override
//  public ResponseEntity<List<AccountDTO>> retrieve(String acceptVersion) {
//    List<Account> accountList = this.accountService.retrieve();
//    List<AccountDTO> dtoList = this.accountMapper.toOpenApiDTODTOList(accountList);
//    HttpHeaders headers = new HttpHeaders();
//    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
//    return new ResponseEntity<>(dtoList, headers, HttpStatus.OK);
//  }
//
//  @SneakyThrows
//  @Override
//  public ResponseEntity<AccountDTO> retrieveById(UUID id, String acceptVersion) {
//    try {
//      HttpHeaders headers = new HttpHeaders();
//      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
//      Account account = this.accountService.retrieveById(id);
//      AccountDTO dto = this.accountMapper.toOpenApiDTO(account);
//      return new ResponseEntity<>(dto, headers, HttpStatus.OK);
//    } catch (AccountNotFoundException ex) {
//      throw new NotFoundException(ex.getMessage());
//    }
//  }
//
//  @SneakyThrows
//  @Override
//  public ResponseEntity<Void> update(UUID id, String acceptVersion, AccountDTO dto) {
//    try {
//      Account account = Account.createNewWith(id, dto.getOwner(), dto.getBalance());
//      this.accountService.update(account);
//      HttpHeaders headers = new HttpHeaders();
//      headers.set(CACHE_CONTROL, "no store, private, max-age=0");
//      return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
//    } catch (AccountNotFoundException ex) {
//      throw new NotFoundException(ex.getMessage());
//    } catch (OptimisticConcurrencyException ex) {
//      throw new ConflictException(ex.getMessage());
//    }
//  }
//
//  @SneakyThrows
//  @Override
//  public ResponseEntity<Void> delete(UUID id, String acceptVersion) {
//    try {
//      HttpHeaders responseHeaders = new HttpHeaders();
//      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
//      this.accountService.delete(id);
//      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
//    } catch (AccountNotFoundException ex) {
//      throw new NotFoundException(ex.getMessage());
//    }
//  }
//
//  @SneakyThrows
//  @Override
//  public ResponseEntity<Void> deposit(String acceptVersion, DepositAccountDTO dto) {
//    try {
//      DepositAccount depositAccount = DepositAccount.builder().accountId(dto.getAccountId()).amount(dto.getAmount()).build();
//      this.accountService.deposit(depositAccount);
//      HttpHeaders responseHeaders = new HttpHeaders();
//      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
//      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
//    } catch (AccountNotFoundException ex) {
//      throw new NotFoundException(ex.getMessage());
//    } catch (OptimisticConcurrencyException ex) {
//      throw new BadRequestException(ex.getMessage());
//    }
//  }
//
//  @SneakyThrows
//  @Override
//  public ResponseEntity<Void> withdraw(String acceptVersion, WithdrawAccountDTO dto) {
//    try {
//      WithdrawAccount withdrawAccount = WithdrawAccount.builder().accountId(dto.getAccountId()).amount(dto.getAmount()).build();
//      this.accountService.withdraw(withdrawAccount);
//      HttpHeaders responseHeaders = new HttpHeaders();
//      responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
//      return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
//    } catch (AccountNotFoundException ex) {
//      throw new NotFoundException(ex.getMessage());
//    } catch (OptimisticConcurrencyException ex) {
//      throw new BadRequestException(ex.getMessage());
//    }
//  }
//}
