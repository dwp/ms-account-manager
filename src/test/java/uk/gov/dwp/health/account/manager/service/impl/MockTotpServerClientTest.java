package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.config.AppConfig;
import uk.gov.dwp.health.account.manager.config.properties.TotpClientProperties;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;
import uk.gov.dwp.health.account.manager.http.HttpRespStatusHandler;
import uk.gov.dwp.health.account.manager.http.totp.TotpGenerateRequest;
import uk.gov.dwp.health.account.manager.http.totp.TotpVerifyRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

@RestClientTest(
    value = {
      AppConfig.class,
      TotpClientProperties.class,
      HttpRespStatusHandler.class,
      TotpClientServiceImpl.class,
    },
    properties = {
      "totp.base-url=http://totp.com",
      "totp.ni-base-url=http://ni.totp.com",
      "totp.generate-path=/v1/totp/generate",
      "totp.verify-path=/v1/totp/verify",
      "kms.data-key=mock-data-key",
      "feature.correlation.enabled=false"
    })
class MockTotpServerClientTest {

  @Autowired private TotpClientServiceImpl underTest;
  @Autowired private RestTemplate restTemplate;
  @Autowired private MockRestServiceServer totpServiceServer;
  @MockBean private MongoTemplate mongoTemplate;

  @Test
  void testPostGenerateTotpRequest() {
    totpServiceServer
        .expect(once(), requestTo("http://totp.com/v1/totp/generate"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    "{\"comm\":\"MOBILE\",\"contact\":\"0777777778\",\"secret\":\"SECRET\"}", true))
        .andRespond(withSuccess());
    boolean actual =
        underTest.postGenerateRequest(
            TotpGenerateRequest.builder()
                .region(Region.GB)
                .secret("SECRET")
                .contact("0777777778")
                .comm("MOBILE")
                .build());
    totpServiceServer.verify();
    assertThat(actual).isTrue();
  }

  @Test
  void testPostGenerateTotpRequest4xx5xx() {
    totpServiceServer
        .expect(once(), requestTo("http://totp.com/v1/totp/generate"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    "{\"comm\":\"MOBILE\",\"contact\":\"0777777778\",\"secret\":\"SECRET\"}", true))
        .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
    TotpGenerateRequest request =
        TotpGenerateRequest.builder()
            .region(null)
            .secret("SECRET")
            .contact("0777777778")
            .comm("MOBILE")
            .build();
    assertThrows(ExternalServiceException.class, () -> underTest.postGenerateRequest(request));
    totpServiceServer.verify();
  }

  @Test
  void testPostVerifyTotpRequest200() {
    totpServiceServer
        .expect(once(), requestTo("http://totp.com/v1/totp/verify"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"totp\":\"123456\",\"secret\":\"SECRET\"}", true))
        .andRespond(withSuccess());
    boolean actual =
        underTest.postVerifyRequest(
            TotpVerifyRequest.builder().region(Region.GB).secret("SECRET").totp("123456").build());
    totpServiceServer.verify();
    assertThat(actual).isTrue();
  }

  @Test
  void testPostVerifyTotpRequest401() {
    totpServiceServer
        .expect(once(), requestTo("http://ni.totp.com/v1/totp/verify"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"totp\":\"123456\",\"secret\":\"SECRET\"}", true))
        .andRespond(withUnauthorizedRequest());
    TotpVerifyRequest request =
        TotpVerifyRequest.builder().region(Region.NI).secret("SECRET").totp("123456").build();
    assertThrows(UnauthorizedException.class, () -> underTest.postVerifyRequest(request));
    totpServiceServer.verify();
  }
}
