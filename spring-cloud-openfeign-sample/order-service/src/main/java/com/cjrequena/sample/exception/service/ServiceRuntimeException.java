package com.cjrequena.sample.exception.service;

import lombok.ToString;

@ToString
public abstract class ServiceRuntimeException extends RuntimeException {
  public ServiceRuntimeException(Throwable ex) {
    super(ex);
  }

  public ServiceRuntimeException(String message) {
    super(message);
  }

  public ServiceRuntimeException(String message, Throwable ex) {
    super(message, ex);
  }

}
