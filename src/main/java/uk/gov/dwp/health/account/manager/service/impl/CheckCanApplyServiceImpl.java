package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static uk.gov.dwp.health.account.manager.constant.Strings.CORRELATION_ID;

@Slf4j
@Service
public class CheckCanApplyServiceImpl implements CheckCanApplyService {

  private final ClaimantRepository repository;
  private final RestTemplate restTemplate;
  private PipcsGatewayClientProperties properties;

  public CheckCanApplyServiceImpl(
      final ClaimantRepository repository,
      @Qualifier("checkCanApplyRestTemplate") final RestTemplate restTemplate,
      final PipcsGatewayClientProperties properties
  ) {
    this.repository = repository;
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  @Override
  public List<CheckClaimResponse> checkCanApply(final String nino) {
    final String normalisedNino = InputValidator.normaliseInputUpper(nino);
    final int existingAccounts = repository.findByNino(normalisedNino).size();
    if (existingAccounts > 0) {
      log.error("Account already exists - claimant cannot apply");
      throw new CanApplyCheckFailedException();
    } else {
      final String url =
          properties.getBaseUrl() + "/"
              + properties.getCheckCanApplyPath() + "/" + normalisedNino;
      final CheckClaimResponse checkClaimResponse = restTemplate.exchange(
          url, HttpMethod.GET, getHttpEntity(), CheckClaimResponse.class
      ).getBody();
      return Arrays.asList(checkClaimResponse);
    }
  }

  private HttpEntity getHttpEntity() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

    return new HttpEntity<>(httpHeaders);
  }

}
