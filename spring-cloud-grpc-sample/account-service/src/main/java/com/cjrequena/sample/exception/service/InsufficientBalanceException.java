package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class InsufficientBalanceException extends ServiceException {
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
