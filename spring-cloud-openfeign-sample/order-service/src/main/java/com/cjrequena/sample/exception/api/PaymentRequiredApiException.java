package com.cjrequena.sample.exception.api;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class PaymentRequiredApiException extends ApiException {
  public PaymentRequiredApiException() {
    super(HttpStatus.PAYMENT_REQUIRED);
  }

  public PaymentRequiredApiException(String message) {
    super(HttpStatus.PAYMENT_REQUIRED, message);
  }
}
