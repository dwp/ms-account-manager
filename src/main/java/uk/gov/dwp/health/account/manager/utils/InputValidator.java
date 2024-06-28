package uk.gov.dwp.health.account.manager.utils;

import java.util.Locale;

public final class InputValidator {

  private static final int NINO_LENGTH = 9;

  @SuppressWarnings("checkstyle:LineLength")
  private static final String EMAIL_REGEX =
      "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?";

  @SuppressWarnings("checkstyle:LineLength")
  private static final String NINO_REGEX =
      "(^(?!BG)(?!GB)(?!NK)(?!KN)(?!TN)(?!NT)(?!ZZ)[A-Z&&[^DFIQUV]][A-Z&&[^DFIOQUV]][0-9]{6}[A-D]$)";

  private InputValidator() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean validNINO(String nino) {
    if (null == nino) {
      return false;
    }
    nino = normaliseInputUpper(nino);
    return nino.length() >= NINO_LENGTH && nino.matches(NINO_REGEX);
  }

  public static String normaliseInputUpper(String input) {
    return input.replaceAll("\\s", "").toUpperCase(Locale.ROOT).trim();
  }

  public static boolean validEmail(String email) {
    if (null == email) {
      return false;
    }
    return normaliseInputLower(email).matches(EMAIL_REGEX);
  }

  public static String normaliseInputLower(String input) {
    return input.replaceAll("\\s", "").toLowerCase(Locale.ROOT).trim();
  }
}
