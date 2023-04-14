package uk.gov.dwp.health.account.manager.api.v1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.NinoDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.Account2FAuth;
import uk.gov.dwp.health.account.manager.service.AccountIdentification;
import uk.gov.dwp.health.account.manager.service.AccountUpdatePassword;
import uk.gov.dwp.health.account.manager.service.V1AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateEmailImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class V1AccountManagerApiImplTest {

  @InjectMocks private V1AccountManagerApiImpl underTest;
  @Mock private V1AccountManagerServices v1AccountManagerService;

  @Test
  void testCreateAccountHandledByAccountManagerService() {
    var createAccount = mock(AccountCreateImpl.class);
    when(v1AccountManagerService.getAccountCreate()).thenReturn(createAccount);
    var request = mock(NewAccountRequest.class);
    underTest.createAccount(request);
    verify(createAccount).doCreateAccount(request);
  }

  @Test
  void testSetOrResetPasswordHandledByAccountManagerService() {
    var updatePassword = mock(AccountUpdatePassword.class);
    when(v1AccountManagerService.getUpdatePassword()).thenReturn(updatePassword);
    var request = mock(PasswordSetResetRequest.class);
    underTest.createUpdatePassword(request);
    verify(updatePassword).updatePassword(request);
  }

  @Test
  void testAccountIdentificationHandledByAccountManagerService() {
    var accountIdentification = mock(AccountIdentification.class);
    when(v1AccountManagerService.getAccountIdentification()).thenReturn(accountIdentification);
    var request = mock(IdRequest.class);
    underTest.identification(request);
    verify(accountIdentification).doIdentification(request);
  }

  @Test
  void testAccountValid2fFactorHandledByAccountManagerService() {
    var account2FAuth = mock(Account2FAuth.class);
    when(v1AccountManagerService.getAccount2FAuth()).thenReturn(account2FAuth);
    var request = mock(ValidTotpRequest.class);
    underTest.valid2fFactor(request);
    verify(account2FAuth).do2FAuthentication(request);
  }

  @Test
  void testAccountDetailsHandledByAccountManagerService() {
    var accountDetails = mock(AccountGetClaimantDetailsImpl.class);
    when(v1AccountManagerService.getClaimantDetails()).thenReturn(accountDetails);
    var accountId = TestFixtures.REF;
    underTest.accountDetailsByAccountNumber(accountId);
    verify(accountDetails).getAccountDetailsByRef(accountId);
  }

  @Test
  @DisplayName("test query account details by nino")
  void testQueryAccountDetailsByNino() {
    var accountDetails = mock(AccountGetClaimantDetailsImpl.class);
    when(v1AccountManagerService.getClaimantDetails()).thenReturn(accountDetails);
    var request = new NinoDetailsRequest();
    request.setNino(TestFixtures.NINO);
    underTest.accountDetailsByNino(request);
    verify(accountDetails).getAccountDetailsByNino(TestFixtures.NINO);
  }

  @Test
  @DisplayName("test update account email")
  void testUpdateAccountEmail() {
    var updateEmail = mock(AccountUpdateEmailImpl.class);
    when(v1AccountManagerService.getUpdateEmail()).thenReturn(updateEmail);
    var request = mock(UpdateEmailRequest.class);
    underTest.updateEmail(request);
    verify(updateEmail).updateEmail(request);
  }
}
