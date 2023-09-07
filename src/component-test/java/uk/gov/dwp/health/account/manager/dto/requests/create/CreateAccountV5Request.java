package uk.gov.dwp.health.account.manager.dto.requests.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Getter
@Builder(toBuilder = true)
public class CreateAccountV5Request {

    @Default
    private String email = faker().bothify("?????#####@dwp.gov.uk");
    @Default
    private String nino = faker().regexify("AC[0-9]{6}[A-D]");
    @Default
    private String dob = new SimpleDateFormat("yyyy-MM-dd").format(faker().date().birthday());
    @Default
    private String forename = faker().name().firstName();
    @Default
    private String surname = faker().name().lastName();
    @Default
    private String postcode = faker().address().zipCode();
    @JsonProperty("mobile_phone")
    @Default
    private String mobilePhone = faker().phoneNumber().cellPhone();
    @Default
    private String language = "EN";
    @JsonProperty("user_journey")
    @Default
    private String userJourney = "STRATEGIC";

    private static Faker faker() {
        return new Faker(new Locale("en-GB"));
    }
}
