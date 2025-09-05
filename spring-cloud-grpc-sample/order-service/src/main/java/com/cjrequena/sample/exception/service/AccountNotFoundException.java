package com.cjrequena.sample.exception.service;

public class AccountNotFoundException extends RuntimeServiceException {

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
