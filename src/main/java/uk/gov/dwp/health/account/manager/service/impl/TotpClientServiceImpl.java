package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.config.properties.TotpClientProperties;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.http.totp.Request;
import uk.gov.dwp.health.account.manager.service.RestClientService;

import static uk.gov.dwp.health.account.manager.entity.Region.GB;
import static uk.gov.dwp.health.account.manager.entity.Region.NI;

@Slf4j
@Service
public class TotpClientServiceImpl implements RestClientService<Request> {

  private final TotpClientProperties properties;
  private final RestTemplate restTemplate;

  public TotpClientServiceImpl(
      final RestTemplate restTemplate, final TotpClientProperties properties) {
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  @Override
  public boolean postGenerateRequest(final Request request) {
    ResponseEntity<Void> resp =
        postRequest(
            restTemplate,
            serviceUrl(resolveTOTPBaseURI(request.getRegion()), properties.getGeneratePath()),
            request.toJson());
    return handleGenerateResponse(resp);
  }

  @Override
  public boolean postVerifyRequest(final Request request) {
    ResponseEntity<Void> resp =
        postRequest(
            restTemplate,
            serviceUrl(resolveTOTPBaseURI(request.getRegion()), properties.getVerifyPath()),
            request.toJson());
    log.info("POST totp verify");
    return handleVerifyResponse(resp);
  }

  private boolean handleVerifyResponse(final ResponseEntity<Void> resp) {
    if (resp.getStatusCode() == HttpStatus.OK) {
      log.info("TOTP request OK");
      return true;
    } else {
      throw new ExternalServiceException(
          String.format(
              "Unexpected Error request verification of totp STATUS %d",
              resp.getStatusCode().value()));
    }
  }

  private String serviceUrl(final String base, final String path) {
    return String.format("%s%s", base, path);
  }

  private String resolveTOTPBaseURI(Region region) {
    if (region == null) {
      return properties.getBaseUrl();
    } else if (region == GB) {
      return properties.getBaseUrl();
    } else if (region == NI) {
      return properties.getNiBaseUrl() != null
          ? properties.getNiBaseUrl()
          : properties.getBaseUrl();
    } else {
      return properties.getBaseUrl();
    }
  }

  private boolean handleGenerateResponse(final ResponseEntity<Void> resp) {
    if (resp.getStatusCode() == HttpStatus.OK) {
      log.info("TOTP request OK");
      return true;
    } else {
      final String msg =
          String.format(
              "Unexpected error request generate totp STATUS %d", resp.getStatusCode().value());
      throw new ExternalServiceException(msg);
    }
  }
}
