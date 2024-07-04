package uk.gov.dwp.health.account.manager.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.FailedAttempt;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.SecureHashService;

@ExtendWith(MockitoExtension.class)
public class VerifyClaimantAccountPasswordServiceTest {
  @Mock ClaimantService claimantService;
  @Mock SecureHashService<String, String> secureHashService;
  VerifyClaimantAccountPasswordServiceImpl verifyClaimantAccountPasswordService;

  @BeforeEach
  public void setup() {
    verifyClaimantAccountPasswordService =  new VerifyClaimantAccountPasswordServiceImpl(claimantService, secureHashService, 3);
  }

  @Test
  @DisplayName("test 1f auth throws accountLockedException")
  void test1FAuthThrowsAccountLockedException() {
    var auth =
        Auth.builder()
            .password("hash_my_password")
            .status(STAGE.SECONDPLUS.current())
            .failureCounter(3)
            .build();
    auth.addFailure(FailedAttempt.builder().build());
    auth.addFailure(FailedAttempt.builder().build());
    auth.addFailure(FailedAttempt.builder().build());
    var claimant = Claimant.builder().auth(auth).build();
    given(secureHashService.hash("my_password")).willReturn("hashed_my_password");

    assertThrows(
        AccountLockedException.class,
        () ->
            verifyClaimantAccountPasswordService.verifyClaimantAccountPassword(
                claimant, "my_password"));

    assertThat(claimant.getAuth().getFailedAttempts().size()).isEqualTo(4);
    assertThat(claimant.getAuth().getStatus()).isEqualTo(STAGE.LOCKED.current());
    verify(claimantService).updateClaimant(claimant);
  }

  @Test
  @DisplayName("test 1f auth throws AccountAuthFailException")
  void test1FAuthThrowsAccountAuthFailException() {
    var claimant =
            Claimant.builder()
                    .auth(
                            Auth.builder()
                                    .password("hash_my_password")
                                    .status(STAGE.SECONDPLUS.current())
                                    .build())
                    .build();
    given(secureHashService.hash("my_password")).willReturn("hashed_my_password");

    assertThrows(AccountAuthFailException.class, () -> verifyClaimantAccountPasswordService.verifyClaimantAccountPassword(
            claimant, "my_password"));

    assertThat(claimant.getAuth().getFailedAttempts().size()).isOne();
    assertThat(claimant.getAuth().getFailedAttempts().get(0).getStage()).isEqualTo("1F");
    verify(claimantService).updateClaimant(claimant);
  }

  @Test
  @DisplayName("test 1f auth authenticationCounterResetToZero")
  void test1FAuthAuthenticationCounterResetToZero() {
    var auth =
            Auth.builder().password("hashed_my_password").status(STAGE.SECONDPLUS.current()).build();
    auth.addFailure(FailedAttempt.builder().build());
    var claimant =
            Claimant.builder()
                    .emailAddress(TestFixtures.EMAIL)
                    .mobileNumber(TestFixtures.MOBILE)
                    .nino(TestFixtures.NINO)
                    .auth(auth)
                    .build();
    given(secureHashService.hash("my_password")).willReturn("hashed_my_password");

    verifyClaimantAccountPasswordService.verifyClaimantAccountPassword(claimant, "my_password");
    assertThat(claimant.getAuth().getFailureCounter()).isZero();
    assertThat(claimant.getAuth().getFailedAttempts().size()).isOne();
    InOrder inOrder = inOrder(secureHashService, claimantService);
    inOrder.verify(secureHashService, times(1)).hash("my_password");
  }
}
