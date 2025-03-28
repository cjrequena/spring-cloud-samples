package com.cjrequena.sample.exception;

import com.cjrequena.sample.exception.api.ApiException;
import com.cjrequena.sample.exception.service.InsufficientBalanceServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.exception.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.cjrequena.sample.common.Constants.DATE_TIME_FORMAT;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

  private static final String EXCEPTION_LOG = "Exception {}";

  @ExceptionHandler({ServiceException.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseEntity<Object> handleServiceException(ServiceException ex) {
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
  }

  @ExceptionHandler({ApiException.class})
  @ResponseBody
  public ResponseEntity<Object> handleApiException(ApiException ex) {
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(ex.getHttpStatus().value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus()).body(errorDTO);
  }

  @ExceptionHandler({OptimisticLockingFailureException.class})
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ResponseBody
  public ResponseEntity<Object> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.CONFLICT.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
  }

  @ExceptionHandler({OptimisticConcurrencyServiceException.class})
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ResponseBody
  public ResponseEntity<Object> handleOptimisticConcurrencyServiceException(OptimisticConcurrencyServiceException ex) {
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.CONFLICT.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
  }

  @ExceptionHandler({InsufficientBalanceServiceException.class})
  @ResponseBody
  public ResponseEntity<Object> handleInsufficientBalanceServiceException(InsufficientBalanceServiceException ex) {
    log.error(EXCEPTION_LOG, ex.getMessage());
    ErrorDTO errorDTO = new ErrorDTO();
    errorDTO.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
    errorDTO.setStatus(HttpStatus.CONFLICT.value());
    errorDTO.setErrorCode(ex.getClass().getSimpleName());
    errorDTO.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
  }

}
