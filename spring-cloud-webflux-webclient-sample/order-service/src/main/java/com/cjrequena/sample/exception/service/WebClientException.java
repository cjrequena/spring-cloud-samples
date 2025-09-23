package com.cjrequena.sample.exception.service;

import com.cjrequena.sample.exception.ErrorDTO;
import org.springframework.lang.Nullable;

public class WebClientException extends ServiceException {

  private ErrorDTO errorDTO;

  public WebClientException(String message) {
    super(message);
  }

  public WebClientException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

  public WebClientException(ErrorDTO errorDTO) {
    super(errorDTO.getMessage());
    this.errorDTO = errorDTO;
  }

  public ErrorDTO getErrorDTO() {
    return errorDTO;
  }
}
