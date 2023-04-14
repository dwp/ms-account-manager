package uk.gov.dwp.health.account.manager.api.v2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.V2AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.Account2FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV2Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountIdentificationImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateEmailImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdatePasswordImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class V2AccountManagerApiImplTest {

  @InjectMocks private V2AccountManagerApiImpl cut;
  @Mock private V2AccountManagerServices services;
  @Captor private ArgumentCaptor<String> strCaptor;

  @Test
  @DisplayName(
      "test should return an account by account number account manager service invoked once")
  void testShouldReturnAnAccountByAccountNumberAccountManagerServiceInvokedOnce() {
    var accountId = "test_account_id";
    var service = mock(AccountGetClaimantDetailsV2Impl.class);
    when(services.getClaimantDetailsV2()).thenReturn(service);
    cut.accountDetailsByAccountNumber(accountId);
    verify(service).getAccountDetailsByRef(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(accountId);
  }

  @Test
  @DisplayName("test should return a list of account details by acc service invoked once")
  void testShouldReturnAListOfAccountDetailsByAccServiceInvokedOnce() {
    var nino = "RN000002C";
    var service = mock(AccountGetClaimantDetailsV2Impl.class);
    when(services.getClaimantDetailsV2()).thenReturn(service);
    when(service.getAccountDetailsByNino(anyString()))
        .thenReturn(ResponseEntity.ok().body(List.of(new AccountDetails())));
    var actual = cut.accountDetailsByNino(nino);
    verify(service).getAccountDetailsByNino(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(nino);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actual.getBody()).hasSize(1);
  }

  @Test
  @DisplayName("test should invoke set and reset password")
  void testSetOrResetPasswordHandledByAccountManagerService() {
    var request =
        mock(uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest.class);
    var service = mock(AccountUpdatePasswordImpl.class);
    when(services.getUpdatePassword()).thenReturn(service);
    cut.createUpdatePassword(request);
    verify(service).updatePassword(request);
  }

  @Test
  @DisplayName("test should invoke handle identification once")
  void testShouldInvokeHandleIdentificationOnce() {
    var request = mock(uk.gov.dwp.health.account.manager.openapi.model.IdRequest.class);
    var service = mock(AccountIdentificationImpl.class);
    when(services.getAccountIdentification()).thenReturn(service);
    cut.identification(request);
    verify(service).doIdentification(request);
  }

  @Test
  @DisplayName("test should invoke handle 2nd factor once")
  void testShouldInvokeHandle2ndFactorOnce() {
    var request = mock(uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest.class);
    var service = mock(Account2FAuthImpl.class);
    when(services.getAccount2FAuth()).thenReturn(service);
    cut.valid2fFactor(request);
    verify(service).do2FAuthentication(request);
  }

  @Test
  @DisplayName("test should invoke create new account once")
  void testShouldInvokeCreateNewAccountOnce() {
    var request = mock(NewAccountRequest.class);
    var service = mock(AccountCreateImpl.class);
    when(services.getAccountCreate()).thenReturn(service);
    cut.createAccount(request);
    verify(service).doCreateAccount(request);
  }

  @Test
  @DisplayName("test should invoke update account email once")
  void testShouldInvokeUpdateAccountEmailOnce() {
    var request = mock(uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest.class);
    var service = mock(AccountUpdateEmailImpl.class);
    when(services.getUpdateEmail()).thenReturn(service);
    cut.updateEmail(request);
    verify(service).updateEmail(request);
  }
}
