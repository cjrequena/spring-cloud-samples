package com.cjrequena.sample.exception.api;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class FailedDependencyApiException extends ApiException {
  public FailedDependencyApiException() {
    super(HttpStatus.FAILED_DEPENDENCY);
  }

  public FailedDependencyApiException(String message) {
    super(HttpStatus.FAILED_DEPENDENCY, message);
  }
}
