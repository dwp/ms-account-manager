package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.config.properties.PipcsGatewayClientProperties;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;
import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.utils.InputValidator;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class CheckCanApplyServiceImplV6 {

  private final ClaimantRepository repository;
  private final RestTemplate restTemplate;
  private final PipcsGatewayClientProperties properties;

  public CheckCanApplyServiceImplV6(
      final ClaimantRepository repository,
      @Qualifier("checkCanApplyRestTemplate") final RestTemplate restTemplate,
      final PipcsGatewayClientProperties properties
  ) {
    this.repository = repository;
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  public List<CheckClaimResponse> checkCanApply(final String nino, final Boolean checkPipApply) {
    log.info("About to check can apply(v6) for nino - {}", nino);
    final var normalisedNino = InputValidator.normaliseInputUpper(nino);

    if (isTrue(checkPipApply)) {
      final int existingAccounts = repository.findByNino(normalisedNino).size();
      if (existingAccounts > 0) {
        log.error("Account already exists - claimant cannot apply");
        throw new CanApplyCheckFailedException();
      }
    }

    final var url =
        properties.getBaseUrl() + "/"
            + properties.getCheckCanApplyPath() + "/" + normalisedNino;
    final var checkClaimResponse = restTemplate.exchange(
        url, HttpMethod.GET, getHttpEntity(), CheckClaimResponse.class
    ).getBody();

    return singletonList(checkClaimResponse);
  }

  private HttpEntity getHttpEntity() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setAccept(singletonList(MediaType.APPLICATION_JSON));
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setAcceptCharset(singletonList(StandardCharsets.UTF_8));

    return new HttpEntity<>(httpHeaders);
  }

}
