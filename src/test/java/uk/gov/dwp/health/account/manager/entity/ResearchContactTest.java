package uk.gov.dwp.health.account.manager.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResearchContactTest {

  @Test
  public void getEnum() {
    assertEnumMatches(ResearchContact.No, "No");
    assertEnumMatches(ResearchContact.Yes, "Yes");
  }

  private static void assertEnumMatches(final ResearchContact researchContact, final String string) {
    assertTrue(ResearchContact.valueOf(string).equals(researchContact));
  }
}
