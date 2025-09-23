package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class NotFoundAException extends ControllerException {
  public NotFoundAException() {
    super(HttpStatus.NOT_FOUND);
  }

  public NotFoundAException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
