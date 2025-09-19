//package com.cjrequena.sample.controller.rest;
//
//import com.cjrequena.sample.common.Constants;
//import com.cjrequena.sample.domain.model.Account;
//import com.cjrequena.sample.mapper.AccountMapper;
//import com.cjrequena.sample.openapi.controller.dto.AccountDTO;
//import com.cjrequena.sample.openapi.controller.dto.DepositAccountDTO;
//import com.cjrequena.sample.openapi.controller.dto.WithdrawAccountDTO;
//import com.cjrequena.sample.openapi.controller.rest.AccountsApi;
//import com.cjrequena.sample.service.AccountService;
//import lombok.RequiredArgsConstructor;
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
//    URI resourcePath = ServletUriComponentsBuilder
//      .fromCurrentRequest()         // uses current request URL
//      .path("/{id}")               // append /{id}
//      .buildAndExpand(account.getId())
//      .toUri();
//
//    // Set headers
//    HttpHeaders headers = new HttpHeaders();
//    headers.set("Cache-Control", "no-store, private, max-age=0");
//    headers.setLocation(resourcePath);
//
//    // Return ResponseEntity with Location header
//    return ResponseEntity.created(resourcePath)
//      .headers(headers)
//      .build();
//  }
//
//  @Override
//  public ResponseEntity<List<AccountDTO>> retrieve(String acceptVersion) {
//        List<Account> accountList = this.accountService.retrieve();
//        List<AccountDTO> dtoList = this.accountMapper.toDTOList(accountList);
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
//        return new ResponseEntity<>(dtoList, headers, HttpStatus.OK);
//  }
//
//  @Override
//  public ResponseEntity<AccountDTO> retrieveById(UUID id, String acceptVersion) {
//    return null;
//  }
//
//  @Override
//  public ResponseEntity<Void> update(UUID id, String acceptVersion, AccountDTO accountDTO) {
//    return null;
//  }
//
//  @Override
//  public ResponseEntity<Void> delete(UUID id, String acceptVersion) {
//    return null;
//  }
//
//  @Override
//  public ResponseEntity<Void> deposit(String acceptVersion, DepositAccountDTO depositAccountDTO) {
//    return null;
//  }
//
//  @Override
//  public ResponseEntity<Void> withdraw(String acceptVersion, WithdrawAccountDTO withdrawAccountDTO) {
//    return null;
//  }
//}
