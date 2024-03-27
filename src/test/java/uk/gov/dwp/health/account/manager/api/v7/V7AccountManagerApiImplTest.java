package uk.gov.dwp.health.account.manager.api.v7;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.V4UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V7NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.V7AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV7Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV7Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV7Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class V7AccountManagerApiImplTest {

  @InjectMocks
  private V7AccountManagerApiImpl v7AccountManagerApi;

  @Mock
  private V7AccountManagerServices services;

  @Test
  void when_creating_account() {
    var accountCreate = mock(AccountCreateV7Impl.class);
    when(services.getAccountCreate()).thenReturn(accountCreate);

    var newAccountRequest = new V7NewAccountRequest();
    var accountReturn = new AccountReturn();
    ResponseEntity<AccountReturn> responseEntity =
        ResponseEntity.status(HttpStatus.CREATED).body(accountReturn);
    when(accountCreate.doCreateAccount(newAccountRequest)).thenReturn(responseEntity);

    ResponseEntity<AccountReturn> actualResponseEntity =
        v7AccountManagerApi.createAccount(newAccountRequest);

    assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(actualResponseEntity.getBody()).isEqualTo(accountReturn);
  }

  @Test
  @DisplayName("test create new account V7 account service invoked")
  void testCreateNewAccountV7AccountServiceInvoked() {
    var request = mock(V7NewAccountRequest.class);
    var service = mock(AccountCreateV7Impl.class);
    when(services.getAccountCreate()).thenReturn(service);
    v7AccountManagerApi.createAccount(request);
    var captor = ArgumentCaptor.forClass(V7NewAccountRequest.class);
    verify(service).doCreateAccount(captor.capture());
    assertThat(captor.getValue()).isEqualTo(request);
  }

  @Test
  @DisplayName("test query account by email V7 service invoked")
  void testQueryAccountByEmailV7ServiceInvoked() {
    var pathVariable = TestFixtures.EMAIL;
    var service = mock(AccountGetClaimantDetailsV7Impl.class);
    when(services.getClaimantDetailsV7()).thenReturn(service);
    v7AccountManagerApi.accountDetailsByEmail(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByEmail(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test query account by ID V7 service invoked")
  void testQueryAccountByIdV7ServiceInvoked() {
    var pathVariable = TestFixtures.REF;
    var service = mock(AccountGetClaimantDetailsV7Impl.class);
    when(services.getClaimantDetailsV7()).thenReturn(service);
    v7AccountManagerApi.accountDetailsByAccountNumber(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByRef(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test query account by NINO V7 service invoked")
  void testQueryAccountByNINOV7ServiceInvoked() {
    var pathVariable = TestFixtures.NINO;
    var service = mock(AccountGetClaimantDetailsV7Impl.class);
    when(services.getClaimantDetailsV7()).thenReturn(service);
    v7AccountManagerApi.accountDetailsByNino(pathVariable);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).getAccountDetailsByNino(captor.capture());
    assertThat(captor.getValue()).isEqualTo(pathVariable);
  }

  @Test
  @DisplayName("test update claimant details and V7 service invoked")
  void testUpdateClaimantDetailsAndV7ServiceInvoked() {
    var request = mock(V4UpdateClaimantDetailsRequest.class);
    var service = mock(AccountUpdateClaimantDetailsV7Impl.class);
    when(services.getUpdateClaimantDetails()).thenReturn(service);
    v7AccountManagerApi.updateClaimantDetails(request);
    var captor = ArgumentCaptor.forClass(V4UpdateClaimantDetailsRequest.class);
    verify(service).updateClaimantDetails(captor.capture());
    assertThat(captor.getValue()).isInstanceOf(V4UpdateClaimantDetailsRequest.class);
  }
}
