package uk.gov.dwp.health.account.manager.api;

import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.exception.AccountSetupException;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;
import uk.gov.dwp.health.account.manager.openapi.model.FailureResponse;

import jakarta.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@ControllerAdvice
public class AppControllerAdvise {

  private static Logger log = LoggerFactory.getLogger(AppControllerAdvise.class);

  @ExceptionHandler(value = {AccountExistException.class})
  public ResponseEntity<FailureResponse> handleConflict(AccountExistException ex) {
    FailureResponse respBody = new FailureResponse();
    respBody.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(respBody);
  }

  @ExceptionHandler(
      value = {
        ExternalServiceException.class,
        ResourceAccessException.class
      })
  public ResponseEntity<Void> handleExternalServerErrorException(Exception ex) {
    log.error("External Service/API not behave correctly {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }

  @ExceptionHandler(value = {UnauthorizedException.class})
  public ResponseEntity<Void> handleUnauthorizedException(UnauthorizedException ex) {
    log.info("Failed authorize login {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(value = {CanApplyCheckFailedException.class})
  public ResponseEntity<Void> canApplyCheckFailedException(CanApplyCheckFailedException ex) {
    log.info("Account already exists");
    return ResponseEntity.status(ex.getStatusCode()).build();
  }

  @ExceptionHandler(
      value = {
        AccountAuthFailException.class,
        AccountNotFoundException.class,
        AccountSetupException.class
      })
  public ResponseEntity<Void> handleBadRequestException(RuntimeException ex) {
    log.info(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(value = {AccountLockedException.class, IllegalStateException.class})
  public ResponseEntity<Void> handle403(RuntimeException ex) {
    log.info(ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  @Generated
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Void> handle405(HttpRequestMethodNotSupportedException ex) {
    log.warn("Request method fail on {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
  }

  @Generated
  @ExceptionHandler(
      value = {
        ConstraintViolationException.class,
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class,
        DataValidationException.class
      })
  public ResponseEntity<FailureResponse> handle400(Exception ex) {
    log.warn("Request validation failed on {}", ex.getMessage());
    var failureMessage = new FailureResponse();
    failureMessage.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureMessage);
  }

  @Generated
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Void> handle500(Exception ex) {
    var writer = new StringWriter();
    var printer = new PrintWriter(writer);
    ex.printStackTrace(printer);
    printer.flush();
    log.error("Unknown server error {}", stackTracePrinter(ex));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  private String stackTracePrinter(Exception ex) {
    var writer = new StringWriter();
    var printer = new PrintWriter(writer);
    ex.printStackTrace(printer);
    printer.flush();
    return writer.toString();
  }
}
