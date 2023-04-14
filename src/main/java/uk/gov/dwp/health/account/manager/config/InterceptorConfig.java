package uk.gov.dwp.health.account.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.dwp.health.account.manager.interceptor.EmailValidationInterceptor;
import uk.gov.dwp.health.account.manager.interceptor.NinoValidationInterceptor;

@Component
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  private final EmailValidationInterceptor emailValidationInterceptor;
  private final NinoValidationInterceptor ninoValidationInterceptor;

  public InterceptorConfig(
      EmailValidationInterceptor emailValidationInterceptor,
      NinoValidationInterceptor ninoValidationInterceptor) {
    this.emailValidationInterceptor = emailValidationInterceptor;
    this.ninoValidationInterceptor = ninoValidationInterceptor;
  }

  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(emailValidationInterceptor)
        .addPathPatterns("/v*/account/details/email/**");
    registry
        .addInterceptor(ninoValidationInterceptor)
        .addPathPatterns("/v*/account/details/nino/**");
  }
}
