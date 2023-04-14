package uk.gov.dwp.health.account.manager.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.utils.InputValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class NinoValidationInterceptor implements HandlerInterceptor {

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {
    final var variables =
        (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (variables == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return false;
    }
    final var nino = variables.get("nino");
    if (InputValidator.validNINO(nino)) {
      return true;
    } else {
      throw new DataValidationException("Validation failed on NINO format");
    }
  }
}
