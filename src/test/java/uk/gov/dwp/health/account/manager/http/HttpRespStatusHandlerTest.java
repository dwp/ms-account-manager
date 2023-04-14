package uk.gov.dwp.health.account.manager.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpRespStatusHandlerTest {

  private TestLogger testLogger = TestLoggerFactory.getTestLogger(HttpRespStatusHandler.class);
  private HttpRespStatusHandler cut;

  private static Stream<Arguments> testCase() {
    return Stream.of(
        Arguments.of(HttpStatus.BAD_REQUEST, true),
        Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, true));
  }

  @BeforeEach
  void setup() {
    cut = new HttpRespStatusHandler();
  }

  @ParameterizedTest
  @MethodSource(value = "testCase")
  void testHasErrorWithAllowedStatusCode(HttpStatus respStatus, boolean expect) throws IOException {
    HttpRespStatusHandler underTest = new HttpRespStatusHandler();
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(respStatus);
    assertThat(underTest.hasError(response)).isEqualTo(expect);
  }

  @Test
  void testHandleErrorLogError() throws Exception {
    testLogger.clearAll();
    ReflectionTestUtils.setField(cut, "log", testLogger);
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.METHOD_NOT_ALLOWED);
    when(response.getBody())
        .thenReturn(new ByteArrayInputStream("MOCK_ERROR_MSG".getBytes(StandardCharsets.UTF_8)));
    assertThatThrownBy(() -> cut.handleError(response))
        .isInstanceOf(ExternalServiceException.class)
        .hasMessageStartingWith("Client error - Response code");
    assertThat(testLogger.getLoggingEvents()).hasSize(1);
  }
}
