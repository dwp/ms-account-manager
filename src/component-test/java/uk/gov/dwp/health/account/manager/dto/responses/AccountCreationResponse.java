package uk.gov.dwp.health.account.manager.dto.responses;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class AccountCreationResponse {
  private String ref;
  private String message;
}
