package uk.gov.dwp.health.account.manager.mock;

import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;

public class claimant {
  public static Claimant getClaimant() {
    var claimant = new Claimant();
    claimant.setRegion(Region.GB);
    claimant.setDateOfBirth(TestFixtures.DOB);
    claimant.setNino(TestFixtures.NINO);
    claimant.setForename(TestFixtures.FORENAME);
    claimant.setSurname(TestFixtures.SURNAME);
    claimant.setEmailAddress(TestFixtures.EMAIL);
    claimant.setMobileNumber(TestFixtures.MOBILE);
    claimant.setPostcode(TestFixtures.POSTCODE);
    claimant.setLanguage(TestFixtures.LANGUAGE);
    claimant.setUserJourney(TestFixtures.USER_JOURNEY);
    return claimant;
  }
}