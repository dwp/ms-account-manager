package uk.gov.dwp.health.account.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.http.HttpRespStatusHandler;
import uk.gov.dwp.health.monitoring.handler.CorrelationIdMetadataHandler;
import uk.gov.dwp.health.monitoring.handler.OutgoingMetadataHandler;
import uk.gov.dwp.health.monitoring.interceptor.OutgoingInterceptor;
import uk.gov.dwp.health.monitoring.logging.LoggerContext;

import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class AppConfig {

  @Bean
  public RestTemplate restTemplate(
      final HttpRespStatusHandler errorHandler,
      final RestTemplateBuilder restTemplateBuilder,
      final OutgoingInterceptor outgoingInterceptor) {

    log.info("Initialize application specific RestTemplate");

    return restTemplateBuilder
        .errorHandler(errorHandler)
        .interceptors(List.of(outgoingInterceptor))
        .build();
  }

  @Bean
  public OutgoingInterceptor outgoingInterceptor() {
    LoggerContext loggerContext = new LoggerContext();
    List<OutgoingMetadataHandler> outgoingInterceptors = List.of(
        new CorrelationIdMetadataHandler(loggerContext)
    );
    return new OutgoingInterceptor(outgoingInterceptors);
  }

}
