package uk.gov.dwp.health.account.manager.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTest {

  @Test
  void testAddingFailureAttempts() {
    var auth = new Auth();
    givenAuthWithNonEmptyFailureHistory(auth);
    whenResetFailureCounter(auth);
    assertThat(auth.getFailedAttempts().size()).isEqualTo(3);
  }

  private void givenAuthWithNonEmptyFailureHistory(Auth auth) {
    auth.addFailure(new FailedAttempt());
    auth.addFailure(new FailedAttempt());
    auth.addFailure(new FailedAttempt());
    assertThat(auth.getFailureCounter()).isEqualTo(3);
  }

  private void whenResetFailureCounter(Auth auth) {
    auth.setFailureCounter(0);
  }
}
