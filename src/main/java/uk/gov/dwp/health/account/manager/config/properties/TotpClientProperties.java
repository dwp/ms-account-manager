package uk.gov.dwp.health.account.manager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(value = "totp")
@Getter
@Setter
@Validated
public class TotpClientProperties {

  @NotBlank(message = "TOTP base url required")
  private String baseUrl;

  private String niBaseUrl;

  @NotBlank(message = "TOTP verify path required")
  private String verifyPath;

  @NotBlank(message = "TOTP generate path required")
  private String generatePath;
}
