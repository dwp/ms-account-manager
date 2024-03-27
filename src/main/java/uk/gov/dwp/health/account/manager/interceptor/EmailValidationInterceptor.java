package uk.gov.dwp.health.account.manager.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.utils.InputValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class EmailValidationInterceptor implements HandlerInterceptor {

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {
    final var variables =
        (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (variables == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return false;
    }
    final var email = variables.get("email");
    if (InputValidator.validEmail(email)) {
      return true;
    } else {
      throw new DataValidationException("Validation failed on email address format");
    }
  }
}
