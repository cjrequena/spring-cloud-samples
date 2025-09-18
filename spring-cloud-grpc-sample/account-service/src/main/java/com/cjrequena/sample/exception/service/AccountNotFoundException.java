package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class AccountNotFoundException extends RuntimeServiceException {
  public AccountNotFoundException(String message) {
    super(message);
  }
}
