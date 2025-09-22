package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class AccountNotFoundException extends ServiceRuntimeException {
  public AccountNotFoundException(String message) {
    super(message);
  }
}
