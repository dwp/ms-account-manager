package uk.gov.dwp.health.account.manager.dto.requests.identification;

import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Getter
@Builder(toBuilder = true)
public class IdentificationRequest {
  @Default private String email = faker().bothify("?????#####@dwp.gov.uk");
  @Default private String nino = faker().regexify("AC[0-9]{6}[A-D]");

  @Default
  private String dob = new SimpleDateFormat("yyyy-MM-dd").format(faker().date().birthday());

  @Default private Boolean generate_email_totp = false;
  @Default private Boolean generate_sms_totp = false;

  private static Faker faker() {
    return new Faker(new Locale("en-GB"));
  }
}
