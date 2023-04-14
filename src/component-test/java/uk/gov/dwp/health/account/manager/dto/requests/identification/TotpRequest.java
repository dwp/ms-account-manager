package uk.gov.dwp.health.account.manager.dto.requests.identification;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder(toBuilder = true)
public class TotpRequest {
  @Default private String ref = "b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6";
  @Default private Boolean generate_email_totp = false;
  @Default private Boolean generate_sms_totp = false;
  @Default private final TotpBlock totp = TotpBlock.builder().build();
    @Getter
    @Setter
    @Builder(toBuilder = true)
    public static class TotpBlock {
      @Default private String code = "123456";
      @Default private String source = "MOBILE";
    }
}
