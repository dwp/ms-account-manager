package uk.gov.dwp.health.account.manager.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class MobileNumbersResponse {
  @JsonProperty("mobile_phone")
  private String mobilePhone;
  @JsonProperty("claimant_id")
  private String claimantId;
}




