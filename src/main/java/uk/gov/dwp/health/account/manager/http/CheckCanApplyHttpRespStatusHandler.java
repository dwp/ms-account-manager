package uk.gov.dwp.health.account.manager.http;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Slf4j
@NoArgsConstructor
@Component(value = "checkCanApplyHttpRespStatusHandler")
public class CheckCanApplyHttpRespStatusHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().series() == CLIENT_ERROR
        || response.getStatusCode().series() == SERVER_ERROR;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    final String msg =
        String.format("Server error - Response code [%s]", response.getStatusCode().value());
    log.error(msg);
    throw new CanApplyCheckFailedException(response.getStatusCode().value());
  }
}
