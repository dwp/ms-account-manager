package uk.gov.dwp.health.account.manager;

import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.dwp.health.integration.message.aws.AWSFlowConfiguration;

@SpringBootTest(
    classes = {Application.class},
    properties = {
      "totp.base-url=https://dwp.gov.uk",
      "totp.verify-path=/v1/totp/verify",
      "totp.generate-path=/v1/totp/generate",
      "feature.correlation.enabled=false"
    })
@ActiveProfiles(value = {"test"})
class ApplicationTests {

  @MockBean MongoClient mongoClient;
  @MockBean MongockConnectionDriver mongockConnectionDriver;
  @MockBean MongockSpring5.Builder mongoBuilder;
  @MockBean AWSFlowConfiguration awsFlowConfiguration;

  @Test
  void contextLoads() {}
}
