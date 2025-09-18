package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OrderNotFoundException extends ServiceRuntimeException {
  public OrderNotFoundException(String message) {
    super(message);
  }
}
