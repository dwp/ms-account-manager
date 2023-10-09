package uk.gov.dwp.health.account.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.dwp.health.mongo.changestream.extension.MongoChangeStreamIdentifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "account")
@CompoundIndex(
    name = "email_address_nino_idx",
    def = "{ 'email_address' : 1, 'nino' : 1 }",
    unique = true)
public class Claimant extends MongoChangeStreamIdentifier {

  @Id private String id;

  @Field(value = "surname")
  private String surname;

  @Field(value = "forename")
  private String forename;

  @Field(value = "nino")
  @Indexed
  private String nino;

  @Field(value = "date_of_birth")
  private LocalDate dateOfBirth;

  @Field(value = "email_address")
  private String emailAddress;

  @Field(value = "mobile_number")
  private String mobileNumber;

  @Field(value = "auth")
  private Auth auth;

  @Field(value = "language")
  private String language;

  @Field(value = "postcode")
  private String postcode;

  @Field(value = "region")
  private Region region;

  @Field(value = "userJourney")
  private UserJourney userJourney;

  @Field(value = "researchContact")
  private ResearchContact researchContact;

  @Transient private boolean enableCap;

  @Field(value = "transferredToDwpApply")
  private Boolean transferredToDwpApply;

  public void setPostcode(String postcode) {
    this.postcode = postcode.toUpperCase().trim();
  }

  public void setForename(String forename) {
    this.forename = capitalise(forename);
  }

  private String capitalise(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    if (enableCap) {
      return Arrays.stream(input.strip().split("\\s+"))
          .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
          .collect(Collectors.joining(" "));
    }
    return input.trim();
  }

  public void setSurname(String surname) {
    this.surname = capitalise(surname);
  }

  public static class ClaimantBuilder {

    public ClaimantBuilder surname(String surname) {
      this.surname = build().capitalise(surname);
      return this;
    }

    public ClaimantBuilder forename(String forename) {
      this.forename = build().capitalise(forename);
      return this;
    }

    public ClaimantBuilder postcode(String postcode) {
      this.postcode = postcode.trim().toUpperCase();
      return this;
    }

    public ClaimantBuilder enableCap(boolean enableCap) {
      this.enableCap = enableCap;
      return this;
    }
  }
}
