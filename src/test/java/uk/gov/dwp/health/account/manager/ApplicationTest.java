package uk.gov.dwp.health.account.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import uk.gov.dwp.health.integration.message.configuration.properties.EventCryptoConfigProperties;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;

@SpringBootTest
class ApplicationTest {

  @MockBean
  WatcherConfigProperties watcherConfigProperties;

  @Autowired
  ApplicationContext applicationContext;


  @Test
  @DisplayName("Can load spring context")
  void contextLoads() {

    EventCryptoConfigProperties names = applicationContext.getBean(EventCryptoConfigProperties.class);

    System.out.println("");

  }

}
