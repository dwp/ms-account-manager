package uk.gov.dwp.health.account.manager.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.entity.UserJourney;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataMongoTest
class ClaimantRepositoryTest {

  @Autowired private ClaimantRepository underTest;

  @Test
  void testVerifyClaimantExistWithGivenDetails() {
    createFixture();
    boolean actual =
        underTest.existsByEmailAddressAndNinoAndDateOfBirth(
            "citizen@dwp.gov.uk", "RN000065B", LocalDate.of(1990, 8, 12));
    assertThat(actual).isTrue();
  }

  private void createFixture() {
    final LocalDate dob = LocalDate.of(1990, 8, 12);
    var claimant =
        Claimant.builder()
            .emailAddress("citizen@dwp.gov.uk")
            .nino("RN000065B")
            .enableCap(true)
            .forename("first name")
            .surname("last Name")
            .postcode("ls1 7xX")
            .dateOfBirth(dob)
            .region(Region.GB)
            .userJourney(UserJourney.STRATEGIC)
            .language("EN")
            .build();
    underTest.save(claimant);
  }

  @Test
  void testVerifyClaimantNotExistWithGivenDetails() {
    createFixture();
    boolean actual =
        underTest.existsByEmailAddressAndNinoAndDateOfBirth(
            "claimant@dwp.gov.uk", "RN000065B", LocalDate.of(1990, 8, 12));
    assertThat(actual).isFalse();
  }

  @Test
  void testClaimantFindByExactEmailAndAuthPassword() {
    createFixture();
    boolean isFound = underTest.findByEmailAddress("Citizen@dwp.gov.uk").isPresent();
    assertThat(isFound).isFalse();
    isFound = underTest.findByEmailAddress("citizen@dwp.gov.uk").isPresent();
    assertThat(isFound).isTrue();
  }

  @Test
  @DisplayName("Test claimant save and retrieve")
  void testClaimantSaveAndRetrieve() {
    createFixture();
    var actual = underTest.findByEmailAddress("citizen@dwp.gov.uk").orElseThrow();
    assertAll(
        "assert all values saved and retrieved",
        () -> {
          assertThat(actual.getPostcode()).isEqualTo("LS1 7XX");
          assertThat(actual.getForename()).isEqualTo("First Name");
          assertThat(actual.getSurname()).isEqualTo("Last Name");
          assertThat(actual.getRegion()).isEqualTo(Region.GB);
          assertThat(actual.getUserJourney()).isEqualTo(UserJourney.STRATEGIC);
          assertThat(actual.getLanguage()).isEqualTo("EN");
        });
  }

  @Test
  @DisplayName("test should verify region code is saved in the db in correct form")
  void testShouldVerifyRegionCodeIsSavedInTheDbInCorrectForm() {
    createFixture();
    Claimant actual = underTest.findByNino("RN000065B").get(0);
    assertThat(actual.getRegion()).isEqualTo(Region.GB);
  }

  @Test
  @DisplayName("Test overriding setters")
  void testOverridingSetters() {
    var actual = new Claimant();
    actual.setEnableCap(false);
    actual.setForename("first NaMe");
    actual.setSurname("Last namE");
    actual.setPostcode("Ls1 7Xx");
    assertThat(actual.getSurname()).isEqualTo("Last namE");
    assertThat(actual.getForename()).isEqualTo("first NaMe");
    assertThat(actual.getPostcode()).isEqualTo("LS1 7XX");
  }

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
  }
}
