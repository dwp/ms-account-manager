package uk.gov.dwp.health.account.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountSetupExceptionTest {

  @Test
  @DisplayName("Test create account setup exception")
  void testCreateAccountSetupException() {
    var actual = new AccountSetupException("account not setup yet");
    assertThat(actual.getMessage()).isEqualTo("account not setup yet");
  }
}
