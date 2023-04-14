package uk.gov.dwp.health.account.manager.http.totp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.account.manager.entity.Region;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotpVerifyRequest implements Request {

  @JsonIgnore private final ObjectMapper mapper = new ObjectMapper();
  private String secret;
  private String totp;
  @JsonIgnore private Region region;

  @Override
  public String toJson() {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      log.error("Unable to map to json string {}", e.getMessage());
    }
    return "{}";
  }
}
