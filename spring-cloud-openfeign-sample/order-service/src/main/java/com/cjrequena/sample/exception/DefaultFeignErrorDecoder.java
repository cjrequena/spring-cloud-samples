package com.cjrequena.sample.exception;

import com.cjrequena.sample.exception.service.FeignServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 * @version 1.0
 * @since JDK1.8
 * @see
 *
 */
@Service
@Log4j2
@Data
public class DefaultFeignErrorDecoder implements ErrorDecoder {

  /**
   *
   */
  @Autowired(required = false)
  private JacksonDecoder jacksonDecoder = new JacksonDecoder();

  /**
   *
   */
  private ErrorDecoder delegate = new Default();

  /**
   * @param methodKey
   * @param response
   * @return
   */
  @Override
  public Exception decode(String methodKey, Response response) {
    try {
      ErrorDTO errorDTO = (ErrorDTO) jacksonDecoder.decode(response, ErrorDTO.class);
      log.debug(errorDTO);
      if (errorDTO != null) {
        switch (errorDTO.getStatus()) {
          case 404: // HttpStatus.NOT_FOUND.value()
          case 409: // HttpStatus.CONFLICT.value()
          case 400: // HttpStatus.BAD_REQUEST.value()
          default:
            return new FeignServiceException(errorDTO);
        }
      }
    } catch (IOException e) { //NOSONAR
      // Fail silently as a new exception will be thrown in super
    } catch (IllegalArgumentException ex) {
      log.error("Error instantiating the exception declared thrown for the interface ", ex);
    }
    return delegate.decode(methodKey, response);
  }
}
