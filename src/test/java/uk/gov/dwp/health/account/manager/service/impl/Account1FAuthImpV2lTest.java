package uk.gov.dwp.health.account.manager.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountLockedService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountPasswordService;

@ExtendWith(MockitoExtension.class)
class Account1FAuthImpV2lTest {
  @InjectMocks private Account1FAuthImplV2 account1FAuthImplV2;
  @Mock private ClaimantService claimantService;
  @Mock private VerifyClaimantAccountLockedService<Claimant> verifyClaimantAccountLockedService;

  @Mock
  private VerifyClaimantAccountPasswordService<Claimant, String>
      verifyClaimantAccountPasswordService;

  @Test
  @DisplayName("test v21f authentication throws accountAuthFailException accountNotFound by email")
  void testV21FAuthenticationThrowsAccountAuthFailExceptionAccountNotFoundByEmail() {
    var strCaptor = ArgumentCaptor.forClass(String.class);
    when(claimantService.findAccountBy(anyString())).thenReturn(Optional.empty());
    var request = mock(ValidEmailPasswordRequest.class);
    when(request.getEmail()).thenReturn(TestFixtures.EMAIL);

    assertThrows(
        AccountAuthFailException.class, () -> account1FAuthImplV2.do1FAuthentication(request));

    verify(claimantService).findAccountBy(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.EMAIL);
  }

  @Test
  @DisplayName("test v21f passes without exception")
  void testV21FAuthAuthenticationPassesWithoutException() {
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

    account1FAuthImplV2.do1FAuthentication(request);
    verify(claimantService).findAccountBy(TestFixtures.EMAIL);
  }
}
