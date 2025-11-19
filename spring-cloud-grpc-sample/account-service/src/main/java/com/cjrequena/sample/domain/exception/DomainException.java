package com.cjrequena.sample.domain.exception;

import lombok.ToString;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@ToString
public abstract class DomainException extends Exception {
  public DomainException(Throwable ex) {
    super(ex);
  }

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable ex) {
    super(message, ex);
  }

}
