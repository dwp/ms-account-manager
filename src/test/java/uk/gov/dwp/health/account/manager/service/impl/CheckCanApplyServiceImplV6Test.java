package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.config.properties.PipcsGatewayClientProperties;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.CanApplyCheckFailedException;
import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class CheckCanApplyServiceImplV6Test {

    private CheckCanApplyServiceImplV6 checkCanApplyServiceV6;
    @Mock private RestTemplate restTemplate;
    @Mock private ClaimantRepository repository;
    @Mock private PipcsGatewayClientProperties properties;
    @Mock private ResponseEntity<CheckClaimResponse> response;
    final CheckClaimResponse checkClaimResponse = new CheckClaimResponse();
    final Claimant claimant = Claimant.builder().build();

    @BeforeEach
    void setup() {
        checkCanApplyServiceV6 = new CheckCanApplyServiceImplV6(repository, restTemplate, properties);
    }

    @Test
    void check_can_apply_does_not_check_repository_when_pip_apply_is_false() {
        when(properties.getBaseUrl()).thenReturn("baseUrl");
        when(properties.getCheckCanApplyPath()).thenReturn("checkCanApplyPath");
        when(response.getBody()).thenReturn(checkClaimResponse);
        when(restTemplate.exchange(
                eq("baseUrl/checkCanApplyPath/NINO"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CheckClaimResponse.class)
        )).thenReturn(response);
        final var checkClaimResponses = checkCanApplyServiceV6.checkCanApply("nino", FALSE);

        verifyNoInteractions(repository);
        verify(restTemplate).exchange(eq("baseUrl/checkCanApplyPath/NINO"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CheckClaimResponse.class));
        assertEquals(checkClaimResponse, checkClaimResponses.get(0));
    }

    @Test
    void check_can_apply_does_not_check_repository_when_pip_apply_is_null() {
        when(properties.getBaseUrl()).thenReturn("baseUrl");
        when(properties.getCheckCanApplyPath()).thenReturn("checkCanApplyPath");
        when(response.getBody()).thenReturn(checkClaimResponse);
        when(restTemplate.exchange(
                eq("baseUrl/checkCanApplyPath/NINO"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CheckClaimResponse.class)
        )).thenReturn(response);
        final var checkClaimResponses = checkCanApplyServiceV6.checkCanApply("nino", null);

        verifyNoInteractions(repository);
        verify(restTemplate).exchange(eq("baseUrl/checkCanApplyPath/NINO"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CheckClaimResponse.class));
        assertEquals(checkClaimResponse, checkClaimResponses.get(0));
    }

    @Test
    void check_can_apply_check_repository_when_pip_apply_is_true() {
        when(properties.getBaseUrl()).thenReturn("baseUrl");
        when(properties.getCheckCanApplyPath()).thenReturn("checkCanApplyPath");
        when(response.getBody()).thenReturn(checkClaimResponse);
        when(restTemplate.exchange(
                eq("baseUrl/checkCanApplyPath/NINO"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CheckClaimResponse.class)
        )).thenReturn(response);
        when(repository.findByNino("NINO")).thenReturn(emptyList());

        final var checkClaimResponses = checkCanApplyServiceV6.checkCanApply("nino", TRUE);

        verify(repository).findByNino("NINO");
        verify(restTemplate).exchange(eq("baseUrl/checkCanApplyPath/NINO"), eq(HttpMethod.GET), any(HttpEntity.class), eq(CheckClaimResponse.class));
        assertEquals(checkClaimResponse, checkClaimResponses.get(0));
    }

    @Test
    void check_can_apply_check_repository_when_pip_apply_is_true_throw_exception() {
        when(repository.findByNino("NINO")).thenReturn(singletonList(claimant));

        assertThrows(CanApplyCheckFailedException.class, () ->
                checkCanApplyServiceV6.checkCanApply("nino", TRUE));

        verify(repository).findByNino("NINO");
        verifyNoInteractions(restTemplate);
    }

}