package uk.gov.dwp.health.account.manager.api.v5;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.RegistrationsLimiterDto;
import uk.gov.dwp.health.account.manager.openapi.model.V5NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.RegistrationsLimiterGetter;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.service.V5AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.AccountCheckCanApplyV5Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV5Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateTransferStatusImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class V5AccountManagerApiImplTest {

  @InjectMocks private V5AccountManagerApiImpl v5AccountManagerApi;
  @Mock private V5AccountManagerServices v5AccountManagerServices;

  @Test
  void when_checking_whether_claimant_can_apply() {
    final AccountCheckCanApplyV5Impl service = mock(AccountCheckCanApplyV5Impl.class);
    when(v5AccountManagerServices.getAccountCheckCanApplyV5()).thenReturn(service);
    final String nino = "RN123123A";
    v5AccountManagerApi.v5CanApplyNinoGet("123", nino);
    final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(service).checkCanApply(captor.capture());
    assertThat(captor.getValue()).isEqualTo(nino);
  }

  @Test
  void when_updating_transfer_status() {
    var accountId = TestFixtures.REF;
    var service = mock(AccountUpdateTransferStatusImpl.class);
    when(v5AccountManagerServices.getAccountUpdateTransferStatus()).thenReturn(service);
    v5AccountManagerApi.updateTransferStatus(accountId);
    var captor = ArgumentCaptor.forClass(String.class);
    verify(service).updateTransferStatus(captor.capture());
    assertThat(captor.getValue()).isInstanceOf(String.class);
  }

  @Test
  void when_creating_account() {
    var accountCreateV5 = mock(AccountCreateV5Impl.class);
    when(v5AccountManagerServices.getAccountCreateV5()).thenReturn(accountCreateV5);

    var v5NewAccountRequest = new V5NewAccountRequest();
    var accountReturn = new AccountReturn();
    ResponseEntity<AccountReturn> responseEntity =
        ResponseEntity.status(HttpStatus.CREATED).body(accountReturn);
    when(accountCreateV5.doCreateAccount(v5NewAccountRequest)).thenReturn(responseEntity);

    ResponseEntity<AccountReturn> actualResponseEntity =
        v5AccountManagerApi.createAccount(v5NewAccountRequest);

    assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(actualResponseEntity.getBody()).isEqualTo(accountReturn);
  }

  @Test
  void when_getting_registrations_limiter() {
    var registrationsLimiterGetter = mock(RegistrationsLimiterGetter.class);
    when(v5AccountManagerServices.getRegistrationsLimiterGetter())
        .thenReturn(registrationsLimiterGetter);

    var registrationsLimiterDto = new RegistrationsLimiterDto();
    when(registrationsLimiterGetter.getRegistrationsLimiter()).thenReturn(registrationsLimiterDto);

    ResponseEntity<RegistrationsLimiterDto> responseEntity = v5AccountManagerApi.getLimiter();

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isEqualTo(registrationsLimiterDto);
  }
}
