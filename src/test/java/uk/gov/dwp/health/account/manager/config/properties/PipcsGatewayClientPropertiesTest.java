package uk.gov.dwp.health.account.manager.config.properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PipcsGatewayClientPropertiesTest {

  private static final String BASEURL = "baseurl";
  private static final String CANAPPLYPATH = "canapplypath";
  private static Validator validator;

  @BeforeAll
  static void setup() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void testModifiers() {
    final PipcsGatewayClientProperties properties = new PipcsGatewayClientProperties();
    properties.setBaseUrl(BASEURL);
    properties.setCheckCanApplyPath(CANAPPLYPATH);
    assertEquals(BASEURL, properties.getBaseUrl());
    assertEquals(CANAPPLYPATH, properties.getCheckCanApplyPath());
  }

  @Test
  void testValidation() {
    final PipcsGatewayClientProperties properties = new PipcsGatewayClientProperties();
    properties.setBaseUrl(null);
    properties.setCheckCanApplyPath(null);
    countViolations(properties);
    properties.setBaseUrl("");
    properties.setCheckCanApplyPath("");
    countViolations(properties);
  }

  private static void countViolations(final PipcsGatewayClientProperties properties) {
    assertThat(validator.validate(properties).size()).isEqualTo(2);
  }
}
