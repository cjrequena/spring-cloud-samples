package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OptimisticConcurrencyException extends ServiceException {
  public OptimisticConcurrencyException(String message) {
    super(message);
  }
}
