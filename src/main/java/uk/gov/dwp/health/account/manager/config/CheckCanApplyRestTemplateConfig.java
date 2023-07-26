package uk.gov.dwp.health.account.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.http.CheckCanApplyHttpRespStatusHandler;
import uk.gov.dwp.health.logging.OutgoingInterceptor;

import java.util.List;

@Slf4j
@Configuration
public class CheckCanApplyRestTemplateConfig {

  @Bean
  public RestTemplate checkCanApplyRestTemplate(
      final CheckCanApplyHttpRespStatusHandler checkCanApplyHttpRespStatusHandler,
      final RestTemplateBuilder restTemplateBuilder,
      final OutgoingInterceptor outgoingInterceptor
  ) {
    return restTemplateBuilder
        .errorHandler(checkCanApplyHttpRespStatusHandler)
        .interceptors(List.of(outgoingInterceptor))
        .build();
  }
}
