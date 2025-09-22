package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class ConflictException extends ControllerException {
  public ConflictException() {
    super(HttpStatus.CONFLICT);
  }

  public ConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
