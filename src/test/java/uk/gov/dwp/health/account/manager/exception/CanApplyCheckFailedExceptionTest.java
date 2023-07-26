package uk.gov.dwp.health.account.manager.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanApplyCheckFailedExceptionTest {

  @Test
  public void testEmptyConstructor() {
    final CanApplyCheckFailedException exception = new CanApplyCheckFailedException();
    assertEquals(412, exception.getStatusCode());
    assertEquals("Account already exists", exception.getMessage());
  }

  @Test
  public void testStatusCodeSpecificConstructor() {
    final CanApplyCheckFailedException exception = new CanApplyCheckFailedException(413);
    assertEquals(413, exception.getStatusCode());
    assertEquals("Account already exists", exception.getMessage());
  }
}
