package com.cjrequena.sample.exception.service;

public class AccountNotFoundRuntimeException extends ServiceRuntimeException {

  public AccountNotFoundRuntimeException(Throwable ex) {
    super(ex);
  }

  public AccountNotFoundRuntimeException(String message) {
    super(message);
  }

  public AccountNotFoundRuntimeException(String message, Throwable ex) {
    super(message, ex);
  }
}
