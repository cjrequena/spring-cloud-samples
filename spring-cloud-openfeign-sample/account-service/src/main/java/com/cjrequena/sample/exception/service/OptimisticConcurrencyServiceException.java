package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OptimisticConcurrencyServiceException extends RuntimeServiceException {
  public OptimisticConcurrencyServiceException(String message) {
    super(message);
  }
}
