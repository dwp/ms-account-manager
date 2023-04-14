package uk.gov.dwp.health.account.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.http.HttpRespStatusHandler;

@Slf4j
@Configuration
public class AppConfig {

  @Bean
  @ConditionalOnProperty(
      prefix = "feature.correlation",
      name = {"enabled"},
      havingValue = "false")
  public RestTemplate restTemplate(
      final HttpRespStatusHandler errorHandler, final RestTemplateBuilder restTemplateBuilder) {
    log.info("Initialize application specific RestTemplate");
    return restTemplateBuilder.errorHandler(errorHandler).build();
  }
}
