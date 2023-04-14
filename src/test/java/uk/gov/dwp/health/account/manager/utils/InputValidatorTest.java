package uk.gov.dwp.health.account.manager.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.InputValidator.validEmail;
import static uk.gov.dwp.health.account.manager.utils.InputValidator.validNINO;

class InputValidatorTest {

  @Test
  @DisplayName("test valid national insurance number returns true")
  void testValidNationalInsuranceNumberReturnsTrue() {
    String nino = "RN000078c";
    assertThat(validNINO(nino)).isTrue();
  }

  @Test
  @DisplayName("test valid email returns true")
  void testValidEmailReturnsTrue() {
    String email = "test@dwp.gov.uk";
    assertThat(validEmail(email)).isTrue();
  }

  @Test
  @DisplayName("test valid email fails on null returns false")
  void testValidEmailFailsOnNullReturnsFalse() {
    String email = null;
    assertThat(validEmail(email)).isFalse();
  }

  @Test
  @DisplayName("test valid national insurance fails on null returns false")
  void testValidNIFailsOnNullReturnsFalse() {
    String nino = null;
    assertThat(validNINO(nino)).isFalse();
  }

  @Test
  @DisplayName("test valid fails on nino longer than 9 returns false")
  void testValidFailsOnNinoLongerThan9ReturnsFalse() {
    String nino = "RN000056CD";
    assertThat(validNINO(nino)).isFalse();
  }
}
