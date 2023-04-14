package uk.gov.dwp.health.account.manager.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class STAGETest {

  private static Stream<Arguments> enumTestCases() {
    return Stream.of(
        Arguments.of(-1, STAGE.LOCKED),
        Arguments.of(0, STAGE.PENDING),
        Arguments.of(1, STAGE.FIRST),
        Arguments.of(2, STAGE.SECONDPLUS));
  }

  @ParameterizedTest
  @MethodSource("enumTestCases")
  void testGetStageFromInt(int code, STAGE expected) {
    assertThat(STAGE.fromInt(code)).isEqualTo(expected);
  }

  @Test
  void testUnknownStageThrowIllegalStateException() {
    assertThrows(IllegalStateException.class, () -> STAGE.fromInt(9));
  }
}
