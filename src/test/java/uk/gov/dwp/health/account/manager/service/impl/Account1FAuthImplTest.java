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
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountLockedService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountPasswordService;

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
  private VerifyClaimantAccountLockedService<Claimant> verifyClaimantAccountLockedService;
  private VerifyClaimantAccountPasswordService<Claimant, String> verifyClaimantAccountPasswordService;

  @BeforeEach
  void setup() {
    claimantService = mock(ClaimantService.class);
    totpRequestService = mock(TotpRequestServiceImpl.class);
    secureHashService = mock(SecureSecureHashServiceImpl.class);
    verifyClaimantAccountLockedService = mock(VerifyClaimantAccountLockedServiceImpl.class);
    verifyClaimantAccountPasswordService = mock(VerifyClaimantAccountPasswordServiceImpl.class);
    cut = new Account1FAuthImpl(claimantService, totpRequestService, verifyClaimantAccountLockedService, verifyClaimantAccountPasswordService);
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
  @DisplayName("test 1f auth passed without exception")
  void test1FAuthAuthenticationPassesWithoutException() {
    var claimantArgCaptor = ArgumentCaptor.forClass(Claimant.class);
    var commArgCaptor = ArgumentCaptor.forClass(COMM.class);
    var request = mock(ValidEmailPasswordRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getPassword()).willReturn("my_password");
    var claimant =
            Claimant.builder()
                    .emailAddress(TestFixtures.EMAIL)
                    .mobileNumber(TestFixtures.MOBILE)
                    .nino(TestFixtures.NINO)
                    .build();
    given(claimantService.findAccountBy(anyString())).willReturn(Optional.of(claimant));

    cut.do1FAuthentication(request);

    InOrder inOrder = inOrder(claimantService, totpRequestService);
    inOrder.verify(claimantService).findAccountBy(TestFixtures.EMAIL);
    inOrder
            .verify(totpRequestService, times(1))
            .requestNewTotp(claimantArgCaptor.capture(), commArgCaptor.capture());
    assertThat(claimantArgCaptor.getValue()).isEqualTo(claimant);
    assertThat(commArgCaptor.getValue()).isEqualTo(COMM.MOBILE);
  }
}
