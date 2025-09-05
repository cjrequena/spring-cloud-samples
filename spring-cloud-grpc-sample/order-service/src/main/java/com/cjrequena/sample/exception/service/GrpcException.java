package com.cjrequena.sample.exception.service;

import com.cjrequena.sample.exception.ErrorDTO;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class GrpcException extends RuntimeServiceException {

  private ErrorDTO errorDTO;

  public GrpcException(String message) {
    super(message);
  }

  public GrpcException(String message, @Nullable Throwable cause) {
    super(message, cause);
  }

  public GrpcException(ErrorDTO errorDTO) {
    super(errorDTO.getMessage());
    this.errorDTO = errorDTO;
  }

}
