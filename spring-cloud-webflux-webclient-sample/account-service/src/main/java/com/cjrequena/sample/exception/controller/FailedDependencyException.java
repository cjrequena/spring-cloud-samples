package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class FailedDependencyException extends ControllerException {
  public FailedDependencyException() {
    super(HttpStatus.FAILED_DEPENDENCY);
  }

  public FailedDependencyException(String message) {
    super(HttpStatus.FAILED_DEPENDENCY, message);
  }
}
