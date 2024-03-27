package uk.gov.dwp.health.account.manager.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.account.manager.http.CheckCanApplyHttpRespStatusHandler;
import uk.gov.dwp.health.monitoring.interceptor.OutgoingInterceptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckCanApplyRestTemplateConfigTest {

  private CheckCanApplyRestTemplateConfig checkCanApplyRestTemplateConfig;

  @BeforeEach
  void setup() {
    checkCanApplyRestTemplateConfig = new CheckCanApplyRestTemplateConfig();
  }

  @Test
  void testCreateCheckCanApplyRestTemplateBean() {
    var builder = mock(RestTemplateBuilder.class);
    var errorHandler = mock(CheckCanApplyHttpRespStatusHandler.class);
    var interceptor = mock(OutgoingInterceptor.class);
    when(builder.errorHandler(errorHandler)).thenReturn(builder);
    when(builder.interceptors(any(List.class))).thenReturn(builder);
    when(builder.build()).thenReturn(new RestTemplate());
    var actual = checkCanApplyRestTemplateConfig.checkCanApplyRestTemplate(errorHandler, builder, interceptor);
    assertThat(actual).isNotNull();
  }
}
