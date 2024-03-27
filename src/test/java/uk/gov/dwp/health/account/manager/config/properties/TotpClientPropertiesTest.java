package uk.gov.dwp.health.account.manager.config.properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TotpClientPropertiesTest {

  private static Validator validator;
  private TotpClientProperties underTest;

  @BeforeAll
  static void setupSpec() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  private static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of("base_url", "verify_url", "generate_url", 0),
        Arguments.of(null, null, null, 3),
        Arguments.of("", "", null, 3),
        Arguments.of(null, "", "", 3),
        Arguments.of("", null, "generate", 2));
  }

  @BeforeEach
  void setup() {
    underTest = new TotpClientProperties();
  }

  @Test
  void testGetSetTotpBaseUrl() {
    final String expected = "totp_base_url";
    underTest.setBaseUrl("totp_base_url");
    assertThat(underTest.getBaseUrl()).isEqualTo(expected);
  }

  @Test
  @DisplayName("test get set NI totp base url")
  void testGetSetNiTotpBaseUrl() {
    final String expected = "ni_totp_base_url";
    underTest.setNiBaseUrl("ni_totp_base_url");
    assertThat(underTest.getNiBaseUrl()).isEqualTo(expected);
  }

  @Test
  void testGetSetTotpVerifyUrl() {
    underTest.setVerifyPath("totp_verify_url");
    assertThat(underTest.getVerifyPath()).isEqualTo("totp_verify_url");
  }

  @Test
  void testGetSetTotpGenerateUrl() {
    underTest.setGeneratePath("totp_generate_url");
    assertThat(underTest.getGeneratePath()).isEqualTo("totp_generate_url");
  }

  @ParameterizedTest
  @MethodSource(value = "testCases")
  void testValidationConstraintOnFields(
      final String baseUrl, final String verifyPath, final String generatePath, int violations) {
    underTest.setBaseUrl(baseUrl);
    underTest.setGeneratePath(generatePath);
    underTest.setVerifyPath(verifyPath);
    assertThat(validator.validate(underTest).size()).isEqualTo(violations);
  }
}
