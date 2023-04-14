package uk.gov.dwp.health.account.manager.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.dwp.health.account.manager.interceptor.EmailValidationInterceptor;
import uk.gov.dwp.health.account.manager.interceptor.NinoValidationInterceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterceptorConfigTest {

  private static InterceptorConfig cut;
  private static EmailValidationInterceptor emailValidationInterceptor;
  private static NinoValidationInterceptor ninoValidationInterceptor;

  @BeforeAll
  static void setupSpec() {
    emailValidationInterceptor = new EmailValidationInterceptor();
    ninoValidationInterceptor = new NinoValidationInterceptor();
    cut = new InterceptorConfig(emailValidationInterceptor, ninoValidationInterceptor);
  }

  @Test
  @DisplayName("test register validation interceptors and path patterns")
  void testRegisterValidationInterceptorsAndPathPatterns() {
    var registry = mock(InterceptorRegistry.class);
    var emailRegistration = mock(InterceptorRegistration.class);
    var ninoRegistration = mock(InterceptorRegistration.class);

    when(registry.addInterceptor(any())).thenReturn(emailRegistration).thenReturn(ninoRegistration);
    cut.addInterceptors(registry);

    var interceptorCaptor = ArgumentCaptor.forClass(HandlerInterceptor.class);
    var strCaptor = ArgumentCaptor.forClass(String.class);

    verify(registry, times(2)).addInterceptor(interceptorCaptor.capture());
    assertThat(interceptorCaptor.getAllValues())
        .containsExactly(emailValidationInterceptor, ninoValidationInterceptor);

    verify(emailRegistration).addPathPatterns(strCaptor.capture());
    verify(ninoRegistration).addPathPatterns(strCaptor.capture());

    assertThat(strCaptor.getAllValues())
        .containsSequence(
            "/v*/account/details/email/**",
            "/v*/account/details/nino/**");
  }
}
