package com.cjrequena.sample.controller.exception;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class ServiceUnavailableException extends ControllerException {
  public ServiceUnavailableException() {
    super(HttpStatus.SERVICE_UNAVAILABLE);
  }

  public ServiceUnavailableException(String message) {
    super(HttpStatus.SERVICE_UNAVAILABLE, message);
  }
}
