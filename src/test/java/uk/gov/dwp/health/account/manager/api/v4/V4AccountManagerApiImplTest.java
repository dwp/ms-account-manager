package uk.gov.dwp.health.account.manager.api.v4;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.openapi.model.V4NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V4UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.service.V4AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV4Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class V4AccountManagerApiImplTest {

  @InjectMocks private V4AccountManagerApiImpl cut;
  @Mock private V4AccountManagerServices services;

  @Test
  @DisplayName("test create new account v4 account service invoked")
  void testCreateNewAccountV4AccountServiceInvoked() {
    var request = mock(V4NewAccountRequest.class);
    var service = mock(AccountCreateV4Impl.class);
    when(services.getAccountCreate()).thenReturn(service);
    cut.createAccount(request);
    var captor = ArgumentCaptor.forClass(V4NewAccountRequest.class);
    verify(service).doCreateAccount(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test query account by email V4 service invoked")
  void testQueryAccountByEmailV4ServiceInvoked() {
    var pathVariable = TestFixtures.EMAIL;
    var service = mock(AccountGetClaimantDetailsV4Impl.class);
    when(services.getClaimantDetailsV4()).thenReturn(service);
    cut.accountDetailsByEmail(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByEmail(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test query account by ID V4 service invoked")
  void testQueryAccountByIdV4ServiceInvoked() {
    var pathVariable = TestFixtures.REF;
    var service = mock(AccountGetClaimantDetailsV4Impl.class);
    when(services.getClaimantDetailsV4()).thenReturn(service);
    cut.accountDetailsByAccountNumber(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByRef(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test query account by NINO V4 service invoked")
  void testQueryAccountByNINOV4ServiceInvoked() {
    var pathVariable = TestFixtures.NINO;
    var service = mock(AccountGetClaimantDetailsV4Impl.class);
    when(services.getClaimantDetailsV4()).thenReturn(service);
    cut.accountDetailsByNino(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByNino(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test update claimant details and V4 service invoked")
  void testUpdateClaimantDetailsAndV4ServiceInvoked() {
    var request = mock(V4UpdateClaimantDetailsRequest.class);
    var service = mock(AccountUpdateClaimantDetailsV4Impl.class);
    when(services.getUpdateClaimantDetails()).thenReturn(service);
    cut.updateClaimantDetails(request);
    var captor = ArgumentCaptor.forClass(V4UpdateClaimantDetailsRequest.class);
    verify(service).updateClaimantDetails(captor.capture());
    assertThat(captor.getValue()).isInstanceOf(V4UpdateClaimantDetailsRequest.class);
  }

}
