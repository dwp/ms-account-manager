package uk.gov.dwp.health.account.manager.api;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AppControllerAdviseTest {

  private static AppControllerAdvise cut;
  private TestLogger testLogger = TestLoggerFactory.getTestLogger(AppControllerAdvise.class);

  @BeforeAll
  static void setupSpec() {
    cut = new AppControllerAdvise();
  }

  @BeforeEach
  void setup() {
    testLogger.clearAll();
    ReflectionTestUtils.setField(cut, "log", testLogger);
  }

  @Test
  void testHandle409AccountAlreadyExistException() {
    var exp = mock(AccountExistException.class);
    given(exp.getMessage()).willReturn("EMAIL");
    var actual = cut.handleConflict(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(Objects.requireNonNull(actual.getBody()).getMessage()).isEqualTo("EMAIL");
  }

  @Test
  void testHandleCanApplyCheckFailedException() {
    var exp = new CanApplyCheckFailedException();
    ResponseEntity<Void> actual = cut.canApplyCheckFailedException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
  }

  @Test
  @DisplayName("testHandle400BadRequestException")
  void testHandle400BadRequestException() {
    var message = "nino format validation fail";
    var exp = mock(DataValidationException.class);
    given(exp.getMessage()).willReturn(message);
    var actual = cut.handle400(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(actual.getBody()).getMessage()).isEqualTo(message);
  }

  @Test
  void testHandle401UnauthorizedException() {
    var exp = mock(AccountAuthFailException.class);
    given(exp.getMessage()).willReturn("authentication fail");
    var actual = cut.handleBadRequestException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(actual.getBody()).isNull();
    final ImmutableList<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
    assertThat(loggingEvents).hasSize(1);
    final LoggingEvent loggingEvent = loggingEvents.get(0);
    assertThat(loggingEvent.getMessage()).isEqualTo("authentication fail");
    assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
  }

  @Test
  void testHandle403AccountLockedException() {
    var exp = mock(AccountLockedException.class);
    given(exp.getMessage()).willReturn("account locked");
    var actual = cut.handle403(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(actual.getBody()).isNull();
    final ImmutableList<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
    assertThat(loggingEvents).hasSize(1);
    final LoggingEvent loggingEvent = loggingEvents.get(0);
    assertThat(loggingEvent.getMessage()).isEqualTo("account locked");
    assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
  }

  @Test
  void testHandle503TotpOrNotifyException() {
    var exp = mock(ExternalServiceException.class);
    given(exp.getMessage()).willReturn("fail to request with totp service");
    var actual = cut.handleExternalServerErrorException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(actual.getBody()).isNull();
    final ImmutableList<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
    assertThat(loggingEvents).hasSize(1);
    final LoggingEvent loggingEvent = loggingEvents.get(0);
    assertThat(loggingEvent.getMessage()).isEqualTo("External Service/API not behave correctly {}");
    assertThat(loggingEvent.getLevel()).isEqualTo(Level.ERROR);
    assertThat(loggingEvent.getArguments()).containsExactly("fail to request with totp service");
  }

  @Test
  void testHandle503ResourceNotFoundException() {
    var exp = mock(ResourceAccessException.class);
    given(exp.getMessage()).willReturn("downstream service is not online");
    var actual = cut.handleExternalServerErrorException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(actual.getBody()).isNull();
    final ImmutableList<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
    assertThat(loggingEvents).hasSize(1);
    final LoggingEvent loggingEvent = loggingEvents.get(0);
    assertThat(loggingEvent.getMessage()).isEqualTo("External Service/API not behave correctly {}");
    assertThat(loggingEvent.getLevel()).isEqualTo(Level.ERROR);
    assertThat(loggingEvent.getArguments()).containsExactly("downstream service is not online");
  }

  @Test
  @DisplayName("test handle totp verify fail unauthorised exception")
  void testHandleTotpVerifyFailUnauthorisedException() {
    var exp = mock(UnauthorizedException.class);
    given(exp.getMessage()).willReturn("totp not valid");
    ResponseEntity<Void> actual = cut.handleUnauthorizedException(exp);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(actual.getBody()).isNull();
  }

  @Test
  @DisplayName("test handle 500 prints out stack trace")
  void testHandle500PrintsOutStackTrace() {
    var ex = new Exception();
    var trace =
        new StackTraceElement[] {
          new StackTraceElement("test-class", "test-method", "test.java", 10)
        };
    ex.setStackTrace(trace);
    var actual = cut.handle500(ex);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    var event = testLogger.getLoggingEvents().get(0);
    assertAll(
        "assert logging event",
        () -> {
          assertThat(event.getLevel()).isEqualTo(Level.ERROR);
          assertThat(event.getMessage()).isEqualTo("Unknown server error {}");
          assertThat((String) event.getArguments().get(0))
              .contains("at test-class.test-method(test.java:10)");
        });
  }
}
