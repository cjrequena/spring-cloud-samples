package com.cjrequena.sample.exception.service;

import lombok.ToString;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@ToString
public abstract class RuntimeServiceException extends RuntimeException {
  public RuntimeServiceException(Throwable ex) {
    super(ex);
  }

  public RuntimeServiceException(String message) {
    super(message);
  }

  public RuntimeServiceException(String message, Throwable ex) {
    super(message, ex);
  }

}
