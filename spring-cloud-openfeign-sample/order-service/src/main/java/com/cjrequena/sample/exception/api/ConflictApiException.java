package com.cjrequena.sample.exception.api;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class ConflictApiException extends ApiException {
  public ConflictApiException() {
    super(HttpStatus.CONFLICT);
  }

  public ConflictApiException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
