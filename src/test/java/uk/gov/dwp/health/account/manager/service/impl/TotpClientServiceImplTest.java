package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.config.properties.TotpClientProperties;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.http.totp.TotpGenerateRequest;
import uk.gov.dwp.health.account.manager.http.totp.TotpVerifyRequest;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotpClientServiceImplTest {

  @InjectMocks private TotpClientServiceImpl underTest;
  @Mock private RestTemplate restTemplate;
  @Mock private TotpClientProperties totpClientProperties;

  private static Stream<Arguments> testGenerateCases() {
    return Stream.of(
        Arguments.of(new ResponseEntity<>(HttpStatus.OK), true),
        Arguments.of(new ResponseEntity<>(HttpStatus.BAD_REQUEST), false),
        Arguments.of(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), false),
        Arguments.of(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE), false));
  }

  private static Stream<Arguments> testVerifyCases() {
    return Stream.of(
        Arguments.of(new ResponseEntity<>(HttpStatus.OK), true),
        Arguments.of(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR), false),
        Arguments.of(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE), false));
  }

  @ParameterizedTest
  @MethodSource(value = "testGenerateCases")
  void testPostRequestGenerateTotpSuccessfully(ResponseEntity responseEntity, boolean expect) {
    given(totpClientProperties.getBaseUrl()).willReturn("https://totp");
    given(totpClientProperties.getGeneratePath()).willReturn("/v1/generate");

    TotpGenerateRequest request = mock(TotpGenerateRequest.class);
    given(request.toJson())
        .willReturn("{\"comm\":\"MOBILE\",\"contact\":\"07777777778\",\"secret\":\"SECRET\"}");
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> requestEntity =
        new HttpEntity<>(
            TotpGenerateRequest.builder()
                .contact("07777777778")
                .comm("MOBILE")
                .secret("SECRET")
                .build()
                .toJson(),
            headers);

    when(restTemplate.exchange(
            "https://totp/v1/generate", HttpMethod.POST, requestEntity, Void.class))
        .thenReturn(responseEntity);
    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      assertThat(underTest.postGenerateRequest(request)).isEqualTo(expect);
    } else {
      assertThrows(ExternalServiceException.class, () -> underTest.postGenerateRequest(request));
    }
  }

  @ParameterizedTest
  @MethodSource(value = "testVerifyCases")
  void testPostRequestVerifyTotpSuccessfully(ResponseEntity responseEntity, boolean expect) {
    given(totpClientProperties.getBaseUrl()).willReturn("https://totp");
    given(totpClientProperties.getVerifyPath()).willReturn("/v1/verify");

    TotpVerifyRequest request = mock(TotpVerifyRequest.class);
    given(request.toJson()).willReturn("{\"secret\":\"SECRET\",\"totp\":\"123456\"}");

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> requestEntity =
        new HttpEntity<>(
            TotpVerifyRequest.builder().totp("123456").secret("SECRET").build().toJson(), headers);

    when(restTemplate.exchange(
            "https://totp/v1/verify", HttpMethod.POST, requestEntity, Void.class))
        .thenReturn(responseEntity);

    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      assertThat(underTest.postVerifyRequest(request)).isEqualTo(expect);
    } else {
      assertThrows(ExternalServiceException.class, () -> underTest.postVerifyRequest(request));
    }
  }
}
