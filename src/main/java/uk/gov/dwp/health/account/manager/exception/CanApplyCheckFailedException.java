package uk.gov.dwp.health.account.manager.exception;

public class CanApplyCheckFailedException extends RuntimeException {

  /** Defaults to our primary usage - account already exists, 412. */
  private int statusCode = 412;

  public CanApplyCheckFailedException() {
  }

  public CanApplyCheckFailedException(final int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public String getMessage() {
    return "Account already exists";
  }

  public int getStatusCode() {
    return statusCode;
  }

}
