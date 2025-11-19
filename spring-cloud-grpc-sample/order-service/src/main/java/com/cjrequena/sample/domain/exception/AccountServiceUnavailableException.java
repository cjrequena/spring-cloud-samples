package com.cjrequena.sample.domain.exception;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class AccountServiceUnavailableException extends DomainRuntimeException {

  public AccountServiceUnavailableException(String message) {
    super(message);
  }

  public AccountServiceUnavailableException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

}
