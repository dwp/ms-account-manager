package uk.gov.dwp.health.account.manager.interceptor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NinoValidationInterceptorTest {

  private static HttpServletResponse response;
  private static Object handler;
  private static NinoValidationInterceptor cut;
  private HttpServletRequest request;

  @BeforeAll
  static void setupSpec() {
    response = mock(HttpServletResponse.class);
    handler = mock(Object.class);
    cut = new NinoValidationInterceptor();
  }

  @BeforeEach
  void setup() {
    request = mock(HttpServletRequest.class);
  }

  @Test
  @DisplayName("test nino validation interceptor return true with valid email")
  void testNINOlValidationInterceptorReturnTrueWithValidEmail() throws IOException {
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(Collections.singletonMap("nino", "RN000002C"));
    var actual = cut.preHandle(request, response, handler);
    assertThat(actual).isTrue();
  }

  @Test
  @DisplayName("test nino validation throws validation exception")
  void testNinoValidationThrowsValidationException() {
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(Collections.singletonMap("nino", "illegal_nino"));
    var actual =
        assertThrows(
            DataValidationException.class,
            () -> {
              cut.preHandle(request, response, handler);
            });
    assertThat(actual.getMessage()).isEqualTo("Validation failed on NINO format");
  }
}
