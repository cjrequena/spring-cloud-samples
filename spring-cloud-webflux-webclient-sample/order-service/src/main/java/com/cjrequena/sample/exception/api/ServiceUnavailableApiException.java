package com.cjrequena.sample.exception.api;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class ServiceUnavailableApiException extends ApiException {
  public ServiceUnavailableApiException() {
    super(HttpStatus.SERVICE_UNAVAILABLE);
  }

  public ServiceUnavailableApiException(String message) {
    super(HttpStatus.SERVICE_UNAVAILABLE, message);
  }
}
