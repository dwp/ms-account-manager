package uk.gov.dwp.health.account.manager.dto.requests.password;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class CreatePasswordRequest {
    @Default private String ref = "b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6";
    @Default private String password = "Password123!";
    @Default private List<TotpBlock> totp = List.of(TotpBlock.builder().build());
    @Getter
    @Setter
    @Builder(toBuilder = true)
    public static class TotpBlock {
      @Default private String code = "123456";
      @Default private String source = "MOBILE";
    }
}
