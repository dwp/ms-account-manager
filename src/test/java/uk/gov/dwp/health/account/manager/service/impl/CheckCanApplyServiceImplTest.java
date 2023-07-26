package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.config.properties.PipcsGatewayClientProperties;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;
import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckCanApplyServiceImplTest {

  private CheckCanApplyServiceImpl checkCanApplyService;
  private RestTemplate restTemplate;
  private ClaimantRepository repository;
  private PipcsGatewayClientProperties properties;
  final CheckClaimResponse checkClaimResponse = new CheckClaimResponse();

  @BeforeEach
  void setup() {
    properties = new PipcsGatewayClientProperties();
    properties.setBaseUrl("1");
    properties.setCheckCanApplyPath("2");
    restTemplate = mock(RestTemplate.class);
    repository = mock(ClaimantRepository.class);
    checkCanApplyService = new CheckCanApplyServiceImpl(repository, restTemplate, properties);
    final ResponseEntity<CheckClaimResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(checkClaimResponse);
    when(restTemplate.exchange(
        anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)
    )).thenReturn(response);
  }

  @Test
  void checkCanApplyWhenAccountExists() {
    when(repository.findByNino(anyString())).thenReturn(Collections.emptyList());
    final List<CheckClaimResponse> checkClaimResponses = checkCanApplyService.checkCanApply("wibble");
    verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    assertEquals(checkClaimResponse, checkClaimResponses.get(0));
  }

  @Test
  void checkCanApplyWhenAccountDoesNotExist() {
    when(repository.findByNino(anyString())).thenReturn(Arrays.asList(new Claimant()));
    try {
      checkCanApplyService.checkCanApply("wibble");
      fail("Expected CanApplyCheckFailedException");
    } catch (final CanApplyCheckFailedException e) {
      verify(restTemplate, times(0)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }
  }
}
