package uk.gov.dwp.health.account.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FailedAttempt {

  @Field(value = "timestamp")
  private LocalDateTime timestamp;

  @Field(value = "stage")
  private String stage;
}
