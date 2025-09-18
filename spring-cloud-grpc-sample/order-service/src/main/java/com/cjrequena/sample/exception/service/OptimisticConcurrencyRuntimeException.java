package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OptimisticConcurrencyRuntimeException extends ServiceRuntimeException {
  public OptimisticConcurrencyRuntimeException(String message) {
    super(message);
  }
}
