package uk.gov.dwp.health.account.manager.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.service.*;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV2Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.ClaimantServiceImpl;
import uk.gov.dwp.health.account.manager.service.impl.TotpRequestServiceImpl;
import uk.gov.dwp.health.account.manager.service.impl.TotpVerifyServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ServiceFactoryTest {

  @InjectMocks private ServiceFactory cut;
  @Mock private ClaimantServiceImpl claimantService;
  @Mock private TotpVerifyServiceImpl totpVerifyService;
  @Mock private TotpRequestServiceImpl totpRequestService;
  @Mock private AccountDataMapper dataMapper;

  @Test
  @DisplayName("test create v1 accountCreate bean instance")
  void testCreateV1AccountCreateBeanInstance() {
    assertThat(cut.accountCreate()).isExactlyInstanceOf(AccountCreateImpl.class);
  }

  @Test
  @DisplayName("test create v1 accountCreate bean instance")
  void testCreateV3AccountCreateBeanInstance() {
    assertThat(cut.accountCreateV3()).isExactlyInstanceOf(AccountCreateV3Impl.class);
  }

  @Test
  @DisplayName("test create 1fa auth bean instance")
  void testCreate1FaAuthBeanInstance() {
    assertThat(cut.account1FAuth()).isInstanceOf(Account1FAuth.class);
  }

  @Test
  @DisplayName("test create 2fa auth bean instance")
  void testCreate2FaAuthBeanInstance() {
    assertThat(cut.account2FAuth()).isInstanceOf(Account2FAuth.class);
  }

  @Test
  @DisplayName("test create identification  bean instance")
  void testCreateIdentificationInstance() {
    assertThat(cut.accountIdentification()).isInstanceOf(AccountIdentification.class);
  }

  @Test
  @DisplayName("test create update password  bean instance")
  void testCreateUpdatePasswordInstance() {
    assertThat(cut.accountUpdatePassword()).isInstanceOf(AccountUpdatePassword.class);
  }

  @Test
  @DisplayName("test create get v1 claimant details bean instance")
  void testCreateGetV1ClaimantDetails() {
    assertThat(cut.accountGetClaimantDetailsV1())
        .isExactlyInstanceOf(AccountGetClaimantDetailsImpl.class);
  }

  @Test
  @DisplayName("test create get v2 claimant details bean instance")
  void testCreateGetV2ClaimantDetails() {
    assertThat(cut.accountGetClaimantDetailsV2())
        .isExactlyInstanceOf(AccountGetClaimantDetailsV2Impl.class);
  }

  @Test
  @DisplayName("test create get v3 claimant details bean instance")
  void testCreateGetV3ClaimantDetails() {
    assertThat(cut.accountGetClaimantDetailsV3())
        .isExactlyInstanceOf(AccountGetClaimantDetailsV3Impl.class);
  }

  @Test
  @DisplayName("test create update email bean instance")
  void testCreateUpdateEmailBeanInstance() {
    assertThat(cut.accountUpdateEmail()).isInstanceOf(AccountUpdateEmail.class);
  }

  @Test
  @DisplayName("test create update nino bean instance")
  void testCreateUpdateNinoBeanInstance() {
    assertThat(cut.accountUpdateNino()).isInstanceOf(AccountUpdateNino.class);
  }

  @Test
  @DisplayName("test create update claimant details bean instance")
  void testCreateUpdateClaimantDetailsBeanInstance() {
    assertThat(cut.accountUpdateClaimantDetails()).isInstanceOf(AccountUpdateClaimantDetails.class);
  }
}