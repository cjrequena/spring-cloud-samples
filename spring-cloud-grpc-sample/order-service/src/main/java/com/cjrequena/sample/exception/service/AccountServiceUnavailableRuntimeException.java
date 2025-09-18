package com.cjrequena.sample.exception.service;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class AccountServiceUnavailableRuntimeException extends ServiceRuntimeException {

  public AccountServiceUnavailableRuntimeException(String message) {
    super(message);
  }

  public AccountServiceUnavailableRuntimeException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

}
