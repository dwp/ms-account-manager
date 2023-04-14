package uk.gov.dwp.health.account.manager.interceptor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailValidationInterceptorTest {

  private static HttpServletResponse response;
  private static Object handler;
  private static EmailValidationInterceptor cut;
  private HttpServletRequest request;

  @BeforeAll
  static void setupSpec() {
    response = mock(HttpServletResponse.class);
    handler = mock(Object.class);
    cut = new EmailValidationInterceptor();
  }

  @BeforeEach
  void setup() {
    request = mock(HttpServletRequest.class);
  }

  @Test
  @DisplayName("test email validation interceptor return true with valid email")
  void testEmailValidationInterceptorReturnTrueWithValidEmail() throws IOException {
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(Collections.singletonMap("email", "test@dwp.gov.uk"));
    var actual = cut.preHandle(request, response, handler);
    assertThat(actual).isTrue();
  }

  @Test
  @DisplayName("test email validation throws validation exception")
  void testEmailValidationThrowsValidationException() {
    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
        .thenReturn(Collections.singletonMap("email", "illegal_email"));
    var actual =
        assertThrows(
            DataValidationException.class,
            () -> {
              cut.preHandle(request, response, handler);
            });
    assertThat(actual.getMessage()).isEqualTo("Validation failed on email address format");
  }
}
