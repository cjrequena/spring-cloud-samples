package com.cjrequena.sample.exception.service;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class AccountServiceUnavailableException extends ServiceRuntimeException {

  public AccountServiceUnavailableException(String message) {
    super(message);
  }

  public AccountServiceUnavailableException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

}
