package uk.gov.dwp.health.account.manager.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.FailedAttempt;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.exception.AccountSetupException;

@ExtendWith(MockitoExtension.class)
public class VerifyClaimantAccountLockedServiceTest {

  @InjectMocks
  private VerifyClaimantAccountLockedServiceImpl verifyClaimantAccountLockedService;

  @Test
  @DisplayName("test 1f auth throws accountLockedException")
  void test1FAuthThrowsAccountLockedExceptionIfAuthStatusIsLocked() {
    var auth =
        Auth.builder()
            .password("hash_my_password")
            .status(STAGE.LOCKED.current())
            .failureCounter(4)
            .build();
    auth.addFailure(FailedAttempt.builder().build());
    auth.addFailure(FailedAttempt.builder().build());
    auth.addFailure(FailedAttempt.builder().build());
    auth.addFailure(FailedAttempt.builder().build());
    var claimant = Claimant.builder().auth(auth).build();

    assertThrows(
        AccountLockedException.class,
        () -> verifyClaimantAccountLockedService.verifyClaimantAccountLocked(claimant));

    assertThat(claimant.getAuth().getFailedAttempts().size()).isEqualTo(4);
    assertThat(claimant.getAuth().getStatus()).isEqualTo(STAGE.LOCKED.current());
  }

  @Test
  @DisplayName("test 1f auth throws AccountSetupException")
  void test1FAuthThrowsAccountSetupException() {
    var claimant = Claimant.builder().build();

    assertThrows(
            AccountSetupException.class,
            () -> verifyClaimantAccountLockedService.verifyClaimantAccountLocked(claimant));
  }

  @Test
  @DisplayName("test 1f auth throws AccountSetupException")
  void test1FAuthThrowsAccountLockedException() {
    var auth =
            Auth.builder()
                    .password("hash_my_password")
                    .status(STAGE.FIRST.current())
                    .failureCounter(1)
                    .build();
    auth.addFailure(FailedAttempt.builder().build());

    var claimant = Claimant.builder().auth(auth).build();

    verifyClaimantAccountLockedService.verifyClaimantAccountLocked(claimant);
  }
}
