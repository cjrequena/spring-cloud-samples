package com.cjrequena.sample.exception.service;

import com.cjrequena.sample.exception.ErrorDTO;
import org.springframework.lang.Nullable;

public class WebClientServiceException extends ServiceException {

  private ErrorDTO errorDTO;

  public WebClientServiceException(String message) {
    super(message);
  }

  public WebClientServiceException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

  public WebClientServiceException(ErrorDTO errorDTO) {
    super(errorDTO.getMessage());
    this.errorDTO = errorDTO;
  }

  public ErrorDTO getErrorDTO() {
    return errorDTO;
  }
}
