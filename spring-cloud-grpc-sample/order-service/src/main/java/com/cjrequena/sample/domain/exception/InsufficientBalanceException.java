package com.cjrequena.sample.domain.exception;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class InsufficientBalanceException extends DomainRuntimeException {
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
