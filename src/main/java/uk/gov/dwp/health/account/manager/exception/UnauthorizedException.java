package uk.gov.dwp.health.account.manager.exception;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(final String message) {
    super(message);
  }
}
