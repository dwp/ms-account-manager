package support;

import uk.gov.dwp.health.account.manager.entity.UserJourney;

import java.time.LocalDate;

public class TestFixtures {

  public static final String EMAIL = "citizen@dwp.gov.uk";
  public static final String NEW_EMAIL = "new@dwp.gov.uk";
  public static final String CURRENT_EMAIL = "current@dwp.gov.uk";
  public static final String NINO = "RN000002C";
  public static final String NEW_NINO = "RN000004W";
  public static final String CURRENT_NINO = "RN000007D";
  public static final String MOBILE = "07777777778";
  public static final LocalDate DOB = LocalDate.of(1990, 01, 25);
  public static final String SURNAME = "Last name";
  public static final String FORENAME = "First name";
  public static final String REF = "b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6";
  public static final String TOTP = "113333";
  public static final String LANGUAGE = "EN";
  public static final String POSTCODE = "LS1 1XX";
  public static final UserJourney USER_JOURNEY = UserJourney.TACTICAL;
}
