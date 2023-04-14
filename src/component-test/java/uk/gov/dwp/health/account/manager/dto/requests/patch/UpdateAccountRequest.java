package uk.gov.dwp.health.account.manager.dto.requests.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Getter
@Builder(toBuilder = true)
public class UpdateAccountRequest {
    @Default private String ref = "b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6";
    @JsonProperty("current_nino")
    @Default private String currentNino = faker().regexify("AC[0-9]{6}[A-D]");
    @JsonProperty("new_nino")
    @Default private String newNino = faker().regexify("AC[0-9]{6}[A-D]");
    @Default private String forename = faker().name().firstName();
    @Default private String surname = faker().name().lastName();
    @JsonProperty("current_email")
    @Default private String currentEmail = faker().bothify("?????#####@dwp.gov.uk");
    @JsonProperty("new_email")
    @Default private String newEmail = faker().bothify("?????#####@dwp.gov.uk");
    @Default private String dob = new SimpleDateFormat("yyyy-MM-dd").format(faker().date().birthday());
    @Default private String postcode = faker().address().zipCode();
    @JsonProperty("mobile_phone")
    @Default private String mobilePhone = faker().phoneNumber().cellPhone();
    @Default private String researchContact = "No";

    private static Faker faker() {
        return new Faker(new Locale("en-GB"));
    }
}
