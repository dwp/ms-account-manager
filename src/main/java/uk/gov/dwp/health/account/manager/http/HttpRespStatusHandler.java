package uk.gov.dwp.health.account.manager.http;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@NoArgsConstructor
@Component
public class HttpRespStatusHandler implements ResponseErrorHandler {

  private static Logger log = LoggerFactory.getLogger(HttpRespStatusHandler.class);

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().series() == CLIENT_ERROR
        || response.getStatusCode().series() == SERVER_ERROR;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    if (response.getStatusCode().series() == SERVER_ERROR) {
      final String msg =
          String.format("Server error - Response code [%s]", response.getStatusCode().value());
      log.error(msg);
      throw new ExternalServiceException(msg);
    } else if (response.getStatusCode().series() == CLIENT_ERROR) {
      if (response.getStatusCode() == UNAUTHORIZED) {
        final String msg = "Unauthorized";
        log.info(msg);
        throw new UnauthorizedException(msg);
      }
      final String msg =
          String.format("Client error - Response code [%s]", response.getStatusCode());
      log.error(msg);
      throw new ExternalServiceException(msg);
    }
  }
}
