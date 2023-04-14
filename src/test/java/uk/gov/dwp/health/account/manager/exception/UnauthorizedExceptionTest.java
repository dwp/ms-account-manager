package uk.gov.dwp.health.account.manager.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnauthorizedExceptionTest {

  @Test
  @DisplayName("test create totp verification exception")
  void testCreateTotpVerificationException() {
    var cut = new UnauthorizedException("fail verify totp");
    assertThat(cut.getMessage()).isEqualTo("fail verify totp");
  }
}
