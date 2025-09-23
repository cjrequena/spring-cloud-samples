package com.cjrequena.sample.exception.service;

import com.cjrequena.sample.exception.ErrorDTO;
import org.springframework.lang.Nullable;

public class FeignServiceException extends ServiceException {

  private ErrorDTO errorDTO;

  public FeignServiceException(String message) {
    super(message);
  }

  public FeignServiceException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

  public FeignServiceException(ErrorDTO errorDTO) {
    super(errorDTO.getMessage());
    this.errorDTO = errorDTO;
  }

  public ErrorDTO getErrorDTO() {
    return errorDTO;
  }
}
