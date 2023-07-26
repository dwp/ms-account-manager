package uk.gov.dwp.health.account.manager.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class AccountDetailResponse {
    private String email;
    private String nino;
    private String dob;
    @JsonProperty("mobile_phone")
    private String mobilePhone;
    private String surname;
    private String forename;
    private String postcode;
    private String language;
    private String region;
    @JsonProperty("user_journey")
    private String userJourney;
    private String researchContact;
    @JsonProperty("has_password")
    private Boolean hasPassword;
}
