package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class Account1FAuthImplTest {

  private Account1FAuthImpl cut;
  private ClaimantService claimantService;
  private TotpRequestServiceImpl totpRequestService;
  private SecureSecureHashServiceImpl secureHashService;

  @BeforeEach
  void setup() {
    claimantService = mock(ClaimantService.class);
    totpRequestService = mock(TotpRequestServiceImpl.class);
    secureHashService = mock(SecureSecureHashServiceImpl.class);
    cut = new Account1FAuthImpl(claimantService, totpRequestService, secureHashService, 3);
  }

  @Test
  @DisplayName("test 1f authentication throws accountAuthFailException accountNotFound by email")
  void test1FAuthenticationThrowsAccountAuthFailExceptionAccountNotFoundByEmail() {
    var strCaptor = ArgumentCaptor.forClass(String.class);
    when(claimantService.findAccountBy(anyString())).thenReturn(Optional.empty());
    var request = mock(ValidEmailPasswordRequest.class);
    when(request.getEmail()).thenReturn(TestFixtures.EMAIL);
    assertThrows(AccountAuthFailException.class, () -> cut.do1FAuthentication(request));
    verify(claimantService).findAccountBy(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.EMAIL);
  }

  @Test
  @DisplayName("test 1f auth throws accountLockedException")
  void test1FAuthThrowsAccountLockedException() {
    var request = mock(ValidEmailPasswordRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getPassword()).willReturn("my_password");
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
    given(claimantService.findAccountBy(anyString())).willReturn(Optional.of(claimant));
    given(secureHashService.hash("my_password")).willReturn("hashed_my_password");
    assertThrows(AccountLockedException.class, () -> cut.do1FAuthentication(request));
    assertThat(claimant.getAuth().getFailedAttempts().size()).isEqualTo(4);
    assertThat(claimant.getAuth().getStatus()).isEqualTo(STAGE.LOCKED.current());
    verify(claimantService).updateClaimant(claimant);
    verifyNoInteractions(totpRequestService);
  }

  @Test
  @DisplayName("test 1f auth throws AccountAuthFailException")
  void test1FAuthThrowsAccountAuthFailException() {
    var request = mock(ValidEmailPasswordRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getPassword()).willReturn("my_password");
    var claimant =
        Claimant.builder()
            .auth(
                Auth.builder()
                    .password("hash_my_password")
                    .status(STAGE.SECONDPLUS.current())
                    .build())
            .build();
    given(claimantService.findAccountBy(anyString())).willReturn(Optional.of(claimant));
    given(secureHashService.hash("my_password")).willReturn("hashed_my_password");
    assertThrows(AccountAuthFailException.class, () -> cut.do1FAuthentication(request));
    assertThat(claimant.getAuth().getFailedAttempts().size()).isOne();
    assertThat(claimant.getAuth().getFailedAttempts().get(0).getStage()).isEqualTo("1F");
    verify(claimantService).updateClaimant(claimant);
    verifyNoInteractions(totpRequestService);
  }

  @Test
  @DisplayName("test 1f auth authenticationCounterResetToZero")
  void test1FAuthAuthenticationCounterResetToZero() {
    var claimantArgCaptor = ArgumentCaptor.forClass(Claimant.class);
    var commArgCaptor = ArgumentCaptor.forClass(COMM.class);
    var request = mock(ValidEmailPasswordRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getPassword()).willReturn("my_password");
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
    given(claimantService.findAccountBy(anyString())).willReturn(Optional.of(claimant));
    given(secureHashService.hash("my_password")).willReturn("hashed_my_password");

    cut.do1FAuthentication(request);
    assertThat(claimant.getAuth().getFailureCounter()).isZero();
    assertThat(claimant.getAuth().getFailedAttempts().size()).isOne();
    InOrder inOrder = inOrder(claimantService, totpRequestService, secureHashService);
    inOrder.verify(claimantService).findAccountBy(TestFixtures.EMAIL);
    inOrder.verify(secureHashService, times(1)).hash("my_password");
    inOrder
        .verify(totpRequestService, times(1))
        .requestNewTotp(claimantArgCaptor.capture(), commArgCaptor.capture());
    assertThat(claimantArgCaptor.getValue()).isEqualTo(claimant);
    assertThat(commArgCaptor.getValue()).isEqualTo(COMM.MOBILE);
  }
}
