package com.cjrequena.sample.exception.controller;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@ToString
public abstract class ControllerRuntimeException extends Exception {
  @Getter
  private final HttpStatus httpStatus;

  /**
   * Constructor
   * @param httpStatus status code
   */
  public ControllerRuntimeException(HttpStatus httpStatus) {
    super(httpStatus.getReasonPhrase());
    this.httpStatus = httpStatus;
  }

  /**
   * Constructor
   * @param httpStatus status code
   * @param message Custom message
   */
  public ControllerRuntimeException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }

  /**
   * Constructor
   * @param httpStatus status code
   * @param message custom message
   * @param throwable Exception
   */
  public ControllerRuntimeException(HttpStatus httpStatus, String message, Throwable throwable) {
    super(message, throwable);
    this.httpStatus = httpStatus;
  }

}
