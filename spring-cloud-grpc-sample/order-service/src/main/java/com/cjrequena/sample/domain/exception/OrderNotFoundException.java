package com.cjrequena.sample.domain.exception;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OrderNotFoundException extends DomainRuntimeException {
  public OrderNotFoundException(String message) {
    super(message);
  }
}
