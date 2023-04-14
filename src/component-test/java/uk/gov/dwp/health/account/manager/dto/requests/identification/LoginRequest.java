package uk.gov.dwp.health.account.manager.dto.requests.identification;

import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.util.Locale;

@Getter
@Builder(toBuilder = true)
public class LoginRequest {
    @Default private String email = faker().bothify("?????#####@dwp.gov.uk");
    @Default private String password = faker().bothify("??????#");

    private static Faker faker() {
        return new Faker(new Locale("en-GB"));
    }
}
