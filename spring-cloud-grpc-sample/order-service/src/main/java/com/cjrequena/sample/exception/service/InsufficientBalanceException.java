package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class InsufficientBalanceException extends ServiceRuntimeException {
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
