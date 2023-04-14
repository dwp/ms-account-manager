package uk.gov.dwp.health.account.manager.exception;

import lombok.Generated;

@Generated
public class AccountLockedException extends RuntimeException {
  public AccountLockedException(final String msg) {
    super(msg);
  }
}
