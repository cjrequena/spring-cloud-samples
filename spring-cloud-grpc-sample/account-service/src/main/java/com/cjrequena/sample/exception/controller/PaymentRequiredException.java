package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class PaymentRequiredException extends ControllerException {
  public PaymentRequiredException() {
    super(HttpStatus.PAYMENT_REQUIRED);
  }

  public PaymentRequiredException(String message) {
    super(HttpStatus.PAYMENT_REQUIRED, message);
  }
}
