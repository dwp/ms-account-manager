package uk.gov.dwp.health.account.manager.api.v3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V3NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.V3AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.Account1FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.Account2FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantPhoneNumbersV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountIdentificationImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdatePasswordImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class V3AccountManagerApiImplTest {

  @InjectMocks private V3AccountManagerApiImpl cut;
  @Mock private V3AccountManagerServices services;

  @Test
  @DisplayName("test create update password account manager service invoked")
  void testCreateUpdatePasswordAccountManagerServiceInvoked() {
    var request = mock(PasswordSetResetRequest.class);
    var service = mock(AccountUpdatePasswordImpl.class);
    when(services.getUpdatePassword()).thenReturn(service);
    cut.createUpdatePassword(request);
    var captor = ArgumentCaptor.forClass(PasswordSetResetRequest.class);
    verify(service).updatePassword(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test identify account account manager service invoked")
  void testIdentifyAccountAccountManagerServiceInvoked() {
    var request = mock(IdRequest.class);
    var service = mock(AccountIdentificationImpl.class);
    when(services.getAccountIdentification()).thenReturn(service);
    cut.identification(request);
    var captor = ArgumentCaptor.forClass(IdRequest.class);
    verify(service).doIdentification(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test valid password account manager service invoked")
  void testValidPasswordAccountManagerServiceInvoked() {
    var request = mock(ValidEmailPasswordRequest.class);
    var service = mock(Account1FAuthImpl.class);
    when(services.getAccount1FAuth()).thenReturn(service);
    cut.validFirstFactor(request);
    var captor = ArgumentCaptor.forClass(ValidEmailPasswordRequest.class);
    verify(service).do1FAuthentication(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test valid 2nd factor account manager service invoked")
  void testValid2NdFactorAccountManagerServiceInvoked() {
    var request = mock(ValidTotpRequest.class);
    var service = mock(Account2FAuthImpl.class);
    when(services.getAccount2FAuth()).thenReturn(service);
    cut.valid2fFactor(request);
    var captor = ArgumentCaptor.forClass(ValidTotpRequest.class);
    verify(service).do2FAuthentication(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test create new account v3 account service invoked")
  void testCreateNewAccountV3AccountServiceInvoked() {
    var request = mock(V3NewAccountRequest.class);
    var service = mock(AccountCreateV3Impl.class);
    when(services.getAccountCreate()).thenReturn(service);
    cut.createAccount(request);
    var captor = ArgumentCaptor.forClass(V3NewAccountRequest.class);
    verify(service).doCreateAccount(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test query account by email v3 service invoked")
  void testQueryAccountByEmailV3ServiceInvoked() {
    var pathVariable = TestFixtures.EMAIL;
    var service = mock(AccountGetClaimantDetailsV3Impl.class);
    when(services.getClaimantDetailsV3()).thenReturn(service);
    cut.accountDetailsByEmail(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByEmail(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test query account by ID v3 service invoked")
  void testQueryAccountByIdV3ServiceInvoked() {
    var pathVariable = TestFixtures.REF;
    var service = mock(AccountGetClaimantDetailsV3Impl.class);
    when(services.getClaimantDetailsV3()).thenReturn(service);
    cut.accountDetailsByAccountNumber(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByRef(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test query account by NINO v3 service invoked")
  void testQueryAccountByNINOV3ServiceInvoked() {
    var pathVariable = TestFixtures.NINO;
    var service = mock(AccountGetClaimantDetailsV3Impl.class);
    when(services.getClaimantDetailsV3()).thenReturn(service);
    cut.accountDetailsByNino(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByNino(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test update claimant details and v3 service invoked")
  void testUpdateClaimantDetailsAndV3ServiceInvoked() {
    var request = mock(UpdateClaimantDetailsRequest.class);
    var service = mock(AccountUpdateClaimantDetailsImpl.class);
    when(services.getUpdateClaimantDetails()).thenReturn(service);
    cut.updateClaimantDetails(request);
    var captor = ArgumentCaptor.forClass(UpdateClaimantDetailsRequest.class);
    verify(service).updateClaimantDetails(captor.capture());
    assertThat(captor.getValue()).isInstanceOf(UpdateClaimantDetailsRequest.class);
  }

  @Test
  @DisplayName("test get Account Mobile Phone Numbers By Id invoked")
  void testPhoneNumbersByClaimantId() {
    var request = "1,2,3";
    var service = mock(AccountGetClaimantPhoneNumbersV3Impl.class);
    when(services.getClaimantPhoneNumbersV3()).thenReturn(service);
    cut.mobilePhoneNumbersByClaimantId(request);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getMobilePhoneNumbersByClaimantId(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }
}
