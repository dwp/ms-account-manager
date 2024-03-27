package uk.gov.dwp.health.account.manager.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.entity.ResearchContact;
import uk.gov.dwp.health.account.manager.entity.UserJourney;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V4AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V7AccountDetails;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountDataMapperTest {

  private static AccountDataMapper accountDataMapper;

  @BeforeAll
  static void setupSpec() {
    accountDataMapper = new AccountDataMapper();
  }

  @Test
  @DisplayName("test map claimant to AccountDetails")
  void testMapClaimantToAccountDetails() {
    var claimant = claimantFixture();
    var actual = accountDataMapper.mapToAccountDetails(claimant);
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

  @Test
  @DisplayName("test map claimant to V3AccountDetails")
  void testMapClaimantToV3AccountDetails() {
    var claimant = claimantFixture();
    var actual = accountDataMapper.mapToV3AccountDetails(claimant);
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
  @DisplayName("test map claimant to V5AccountDetails")
  void testMapClaimantToV5AccountDetails() {
    var claimant = claimantFixture();
    claimant.setUserJourney(UserJourney.PIP2_INVITED);
    var actual = accountDataMapper.mapToV7AccountDetails(claimant);
    assertAll(
        "assert v7 account details",
        () -> {
          assertEquals(TestFixtures.EMAIL, actual.getEmail());
          assertEquals(TestFixtures.DOB, actual.getDob());
          assertEquals(TestFixtures.SURNAME, actual.getSurname());
          assertEquals(TestFixtures.FORENAME, actual.getForename());
          assertEquals(TestFixtures.NINO, actual.getNino());
          assertEquals(V7AccountDetails.LanguageEnum.EN, actual.getLanguage());
          assertEquals(TestFixtures.MOBILE, actual.getMobilePhone());
          assertEquals("NI", actual.getRegion().name());
          assertEquals("PIP2_INVITED", actual.getUserJourney().toString());
          assertEquals(Boolean.FALSE, actual.getHasPassword());
          assertNull(actual.getTransferredToDwpApply());
          assertEquals(TestFixtures.REF, actual.getRef());
        });
  }

  @Test
  @DisplayName("test map claimant to V4AccountDetails")
  void testMapClaimantToV4AccountDetails() {
    var claimant = claimantFixture();
    var actual = accountDataMapper.mapToV4AccountDetails(claimant);
    assertAll(
        "assert v4 account details",
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
          assertEquals(Boolean.FALSE, actual.getHasPassword());
          assertNull(actual.getTransferredToDwpApply());
          assertEquals(TestFixtures.REF, actual.getRef());
        });
  }

  @Test
  @DisplayName("test map research contact claimant to V4AccountDetails")
  void testMapResearchContactClaimantToV4AccountDetails() {
    var claimant = claimantFixture();
    claimant.setResearchContact(ResearchContact.Yes);
    var actual = accountDataMapper.mapToV4AccountDetails(claimant);
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

  @Test
  @DisplayName("test map claimant with `auth` but no `password` to V4AccountDetails")
  void testMapClaimantWithAuthNoPasswordToV4AccountDetails() {
    var claimant = claimantFixture();
    claimant.setAuth(Auth.builder().build());
    var actual = accountDataMapper.mapToV4AccountDetails(claimant);
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
          assertEquals(Boolean.FALSE, actual.getHasPassword());
        });
  }

  @Test
  @DisplayName("test map claimant with `auth` and `password` to V4AccountDetails")
  void testMapClaimantWithAuthAndPasswordToV4AccountDetails() {
    var claimant = claimantFixture();
    claimant.setAuth(Auth.builder().password("").build());
    var actual = accountDataMapper.mapToV4AccountDetails(claimant);
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
          assertEquals(Boolean.TRUE, actual.getHasPassword());
        });
  }

  @Test
  @DisplayName("test map transfer status from claimant to V4AccountDetails")
  void testMapTransferStatusFromClaimantToV4AccountDetails() {
    var claimant = claimantFixture();
    claimant.setTransferredToDwpApply(true);
    var actual = accountDataMapper.mapToV4AccountDetails(claimant);
    assertAll(
        "assert v4 account details including transfer status",
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
          assertEquals(true, actual.getTransferredToDwpApply());
        });
  }

  private Claimant claimantFixture() {
    var claimant = new Claimant();
    claimant.setId(TestFixtures.REF);
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
}
