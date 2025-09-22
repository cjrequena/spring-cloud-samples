package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.domain.model.Account;
import com.cjrequena.sample.dto.AccountDTO;
import com.cjrequena.sample.dto.DepositAccountDTO;
import com.cjrequena.sample.dto.WithdrawAccountDTO;
import com.cjrequena.sample.exception.controller.NotFoundException;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
import com.cjrequena.sample.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static com.cjrequena.sample.controller.rest.AccountController.ACCEPT_VERSION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = AccountController.ENDPOINT, headers = {ACCEPT_VERSION})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountController {

  public static final String ENDPOINT = "/account-service/api";
  public static final String ACCEPT_VERSION = "Accept-Version=" + Constants.VND_SAMPLE_SERVICE_V1;
  private final AccountService accountService;
  private final AccountMapper accountMapper;

  @PostMapping(
    path = "/accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> create(@Valid @RequestBody AccountDTO dto) {
    Account account = Account.createNewWith(dto.getOwner(), dto.getBalance());
    return accountService.create(account)
      .map(_dto -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
        headers.set("Accept-Version", ACCEPT_VERSION);
        headers.set("id", _dto.getId().toString());
        final URI location = URI.create(ENDPOINT.concat("/accounts/id/").concat(_dto.getId().toString()));
        return ResponseEntity.created(location).headers(headers).build();
      }).onErrorMap(ex -> {
        if (ex instanceof AccountNotFoundServiceException) {
          return new NotFoundException();
        }
        return ex;
      });
  }

  @GetMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<AccountDTO>> retrieveById(@PathVariable(value = "id") String id) {
    return this.accountService
      .retrieveById(UUID.fromString(id))
      .map(this.accountMapper::toDTO)
      .map(_dto -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
        headers.set("id", _dto.getId().toString());
        return ResponseEntity.ok().headers(headers).body(_dto);
      })
      .onErrorResume(ex -> {
          if (ex instanceof AccountNotFoundServiceException) {
            return Mono.error(new NotFoundException());
          }
          return Mono.error(ex);
        }
      );
  }

  @GetMapping(
    path = "/accounts",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Flux<AccountDTO>>> retrieve() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CACHE_CONTROL, "no store, private, max-age=0");
    final Flux<AccountDTO> dtos$ = this.accountService.retrieve().map(this.accountMapper::toDTO);
    return Mono.just(ResponseEntity.ok().headers(headers).body(dtos$));
  }

  @PutMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> update(@PathVariable(value = "id") UUID id, @Valid @RequestBody AccountDTO dto) {
    Account account = Account.createNewWith(id, dto.getOwner(), dto.getBalance());
    return this.accountService.update(account)
      .map(_entity -> {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CACHE_CONTROL, "no store, private, max-age=0");
        return ResponseEntity.noContent().headers(headers).build();
      })
      .onErrorMap(ex -> {
          if (ex instanceof AccountNotFoundServiceException) {
            return new NotFoundException();
          }
          return ex;
        }
      );
  }

  @DeleteMapping(
    path = "/accounts/{id}",
    produces = {APPLICATION_JSON_VALUE}
  )
  public Mono<ResponseEntity<Object>> delete(@PathVariable(value = "id") UUID id) {
    return this.accountService.delete(id)
      .map(entity -> {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
        return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
      })
      .onErrorMap(ex -> {
          if (ex instanceof AccountNotFoundServiceException) {
            return new NotFoundException();
          }
          return ex;
        }
      );
  }

  @PostMapping(path = "/accounts/deposit", produces = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<Void>> deposit(@RequestBody DepositAccountDTO dto) {
    return this.accountService.deposit(dto)
      .map(_entity -> {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
        return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
      });

  }

  @PostMapping(path = "/accounts/withdraw", produces = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<Void>> withdraw(@RequestBody WithdrawAccountDTO dto) {
    return this.accountService
      .withdraw(dto)
      .map(_entity -> {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CACHE_CONTROL, "no store, private, max-age=0");
        return new ResponseEntity<>(responseHeaders, HttpStatus.NO_CONTENT);
      });
  }

}
