package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OptimisticConcurrencyException extends RuntimeServiceException {
  public OptimisticConcurrencyException(String message) {
    super(message);
  }
}
