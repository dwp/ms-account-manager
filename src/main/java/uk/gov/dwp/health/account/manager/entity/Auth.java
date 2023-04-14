package uk.gov.dwp.health.account.manager.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Auth {

  @Field(value = "status")
  private int status;

  @Field(value = "password")
  private String password;

  @Setter(AccessLevel.NONE)
  @Field(value = "failedAttempts")
  private List<FailedAttempt> failedAttempts = null;

  @Field(value = "failureCount")
  private int failureCounter;

  @Builder
  public Auth(int status, String password, int failureCounter) {
    setStatus(status);
    setPassword(password);
    setFailureCounter(failureCounter);
  }

  public void addFailure(FailedAttempt failedAttempt) {
    if (getFailedAttempts() == null) {
      failedAttempts = new ArrayList<>();
    }
    failedAttempts.add(failedAttempt);
    failureCounter++;
  }
}
