package uk.gov.dwp.health.account.manager.integration;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.integration.message.aws.AWSFlowConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    properties = {
      "totp.base-url=https://dwp.gov.uk",
      "totp.verify-path=/v1/totp/verify",
      "totp.generate-path=/v1/totp/generate",
      "feature.correlation.enabled=true"
    })
@ActiveProfiles(value = {"test"})
@Disabled // please update to use dwp-meta-data-logger as it was previously using correlationid-springboot-starter
class CorrelationIdIntegrationTest {

  @MockBean MongoClient mongoClient;
  @Autowired RestTemplate restTemplate;
  @MockBean MongockConnectionDriver mongockConnectionDriver;
  @MockBean MongockSpring5.Builder mongoBuilder;
  @MockBean AWSFlowConfiguration awsFlowConfiguration;

  @Test
  @DisplayName("test restTemplate initialized correlationId interceptor added to restTemplate")
  void testRestTemplateInitializedCorrelationIdInterceptorAddedToRestTemplate() {
    var interceptors = restTemplate.getInterceptors();
//    assertThat(interceptors.get(0)).isExactlyInstanceOf(RestTemplateInterceptor.class);
  }

  @Test
  @DisplayName("test httpConfig initialized")
  void testHttpConfigInitialized() {
//    assertThat(httpConfig).isNotNull();
  }
}
