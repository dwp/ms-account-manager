package uk.gov.dwp.health.account.manager.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.http.HttpRespStatusHandler;
import uk.gov.dwp.health.monitoring.interceptor.OutgoingInterceptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppConfigTest {

  private AppConfig underTest;

  @BeforeEach
  void setup() {
    underTest = new AppConfig();
  }

  @Test
  void testCreateRestTemplateBean() {
    var builder = mock(RestTemplateBuilder.class);
    var errorHandler = mock(HttpRespStatusHandler.class);
    var interceptor = mock(OutgoingInterceptor.class);
    when(builder.errorHandler(errorHandler)).thenReturn(builder);
    when(builder.interceptors(any(List.class))).thenReturn(builder);
    when(builder.build()).thenReturn(new RestTemplate());
    var actual = underTest.restTemplate(errorHandler, builder, interceptor);
    assertThat(actual).isNotNull();
  }
}
