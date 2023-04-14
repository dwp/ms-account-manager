package uk.gov.dwp.health.account.manager.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.entity.ResearchContact;
import uk.gov.dwp.health.account.manager.entity.UserJourney;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V4AccountDetails;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountDataMapperTest {

  private static AccountDataMapper cut;

  @BeforeAll
  static void setupSpec() {
    cut = new AccountDataMapper();
  }

  @Test
  @DisplayName("test map claimant to AccountDetails")
  void testMapClaimantToAccountD() {
    var claimant = claimantFixture();
    var actual = cut.mapToAccountDetails(claimant);
    assertAll(
        "assert v1 account details",
        () -> {
          assertEquals(TestFixtures.EMAIL, actual.getEmail());
          assertEquals(TestFixtures.DOB, actual.getDob());
          assertEquals(TestFixtures.SURNAME, actual.getSurname());
          assertEquals(TestFixtures.FORENAME, actual.getForename());
          assertEquals(TestFixtures.NINO, actual.getNino());
          assertEquals(AccountDetails.LanguageEnum.EN, actual.getLanguage());
          assertEquals(TestFixtures.MOBILE, actual.getMobilePhone());
          assertEquals("NI", actual.getRegion().name());
        });
  }

  private Claimant claimantFixture() {
    var claimant = new Claimant();
    claimant.setEmailAddress(TestFixtures.EMAIL);
    claimant.setSurname(TestFixtures.SURNAME);
    claimant.setForename(TestFixtures.FORENAME);
    claimant.setNino(TestFixtures.NINO);
    claimant.setDateOfBirth(TestFixtures.DOB);
    claimant.setRegion(Region.NI);
    claimant.setLanguage(TestFixtures.LANGUAGE);
    claimant.setMobileNumber(TestFixtures.MOBILE);
    claimant.setUserJourney(UserJourney.TACTICAL);
    return claimant;
  }

  @Test
  @DisplayName("test map claimant to V3AccountDetails")
  void testNaoClaimantToV3AccountDetails() {
    var claimant = claimantFixture();
    var actual = cut.mapToV3AccountDetails(claimant);
    assertAll(
        "assert v1 account details",
        () -> {
          assertEquals(TestFixtures.EMAIL, actual.getEmail());
          assertEquals(TestFixtures.DOB, actual.getDob());
          assertEquals(TestFixtures.SURNAME, actual.getSurname());
          assertEquals(TestFixtures.FORENAME, actual.getForename());
          assertEquals(TestFixtures.NINO, actual.getNino());
          assertEquals(V3AccountDetails.LanguageEnum.EN, actual.getLanguage());
          assertEquals(TestFixtures.MOBILE, actual.getMobilePhone());
          assertEquals("NI", actual.getRegion().name());
          assertEquals("TACTICAL", actual.getUserJourney().toString());
        });
  }

  @Test
  @DisplayName("test map claimant to V4AccountDetails")
  void testNaoClaimantToV4AccountDetails() {
    var claimant = claimantFixture();
    var actual = cut.mapToV4AccountDetails(claimant);
    assertAll(
        "assert v1 account details",
        () -> {
          assertEquals(TestFixtures.EMAIL, actual.getEmail());
          assertEquals(TestFixtures.DOB, actual.getDob());
          assertEquals(TestFixtures.SURNAME, actual.getSurname());
          assertEquals(TestFixtures.FORENAME, actual.getForename());
          assertEquals(TestFixtures.NINO, actual.getNino());
          assertEquals(V4AccountDetails.LanguageEnum.EN, actual.getLanguage());
          assertEquals(TestFixtures.MOBILE, actual.getMobilePhone());
          assertEquals("NI", actual.getRegion().name());
          assertEquals("TACTICAL", actual.getUserJourney().toString());
          assertEquals(V4AccountDetails.ResearchContactEnum.NO, actual.getResearchContact());
        });
  }

  @Test
  @DisplayName("test map research contact claimant to V4AccountDetails")
  void testNaoRcClaimantToV4AccountDetails() {
    var claimant = claimantFixture();
    claimant.setResearchContact(ResearchContact.Yes);
    var actual = cut.mapToV4AccountDetails(claimant);
    assertAll(
        "assert v1 account details",
        () -> {
          assertEquals(TestFixtures.EMAIL, actual.getEmail());
          assertEquals(TestFixtures.DOB, actual.getDob());
          assertEquals(TestFixtures.SURNAME, actual.getSurname());
          assertEquals(TestFixtures.FORENAME, actual.getForename());
          assertEquals(TestFixtures.NINO, actual.getNino());
          assertEquals(V4AccountDetails.LanguageEnum.EN, actual.getLanguage());
          assertEquals(TestFixtures.MOBILE, actual.getMobilePhone());
          assertEquals("NI", actual.getRegion().name());
          assertEquals("TACTICAL", actual.getUserJourney().toString());
          assertEquals(V4AccountDetails.ResearchContactEnum.YES, actual.getResearchContact());
        });
  }

}
