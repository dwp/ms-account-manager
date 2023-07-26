package uk.gov.dwp.health.account.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(value = "pipcsgw")
@Getter
@Setter
@Validated
public class PipcsGatewayClientProperties {

  @NotBlank(message = "PIPCS-gw base url required")
  private String baseUrl;

  @NotBlank(message = "PIPCS check can apply path")
  private String checkCanApplyPath;
}
