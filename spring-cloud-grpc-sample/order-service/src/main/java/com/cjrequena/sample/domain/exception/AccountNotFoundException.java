package com.cjrequena.sample.domain.exception;

public class AccountNotFoundException extends DomainRuntimeException {

  public AccountNotFoundException(Throwable ex) {
    super(ex);
  }

  public AccountNotFoundException(String message) {
    super(message);
  }

  public AccountNotFoundException(String message, Throwable ex) {
    super(message, ex);
  }
}
