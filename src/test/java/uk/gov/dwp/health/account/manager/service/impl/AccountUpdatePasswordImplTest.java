package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountUpdatePasswordImplTest {

  @InjectMocks private AccountUpdatePasswordImpl cut;
  @Mock private ClaimantServiceImpl claimantService;
  @Mock private TotpVerifyServiceImpl totpVerifyService;
  @Captor private ArgumentCaptor<String> strCaptor;
  @Captor private ArgumentCaptor<Totp> totpArgCaptor;
  @Captor private ArgumentCaptor<Claimant> claimantArgCaptor;

  @Test
  @DisplayName("Test update password return unauthorized")
  void testUpdatePasswordReturnUnauthorized() {
    var request = new PasswordSetResetRequest();
    request.setRef(TestFixtures.REF);
    given(claimantService.findByRef(anyString())).willReturn(mock(Claimant.class));
    given(totpVerifyService.verify(any(Claimant.class), any(Totp.class))).willReturn(false);
    var actual = cut.updatePassword(request);

    verify(claimantService).findByRef(strCaptor.capture());
    assertEquals(TestFixtures.REF, strCaptor.getValue());
    assertEquals(HttpStatus.UNAUTHORIZED, actual.getStatusCode());
  }

  @Test
  @DisplayName("test create new password returns created status")
  void testCreateNewPasswordReturnsCreatedStatus() {
    var request = new PasswordSetResetRequest();
    var totp = new Totp();
    request.setRef(TestFixtures.REF);
    request.setPassword("password");
    request.setTotp(Collections.singletonList(totp));
    var claimant = new Claimant();

    when(claimantService.findByRef(anyString())).thenReturn(claimant);
    when(totpVerifyService.verify(any(Claimant.class), any(Totp.class))).thenReturn(true);

    var actual = cut.updatePassword(request);

    verify(totpVerifyService).verify(claimantArgCaptor.capture(), totpArgCaptor.capture());
    verify(claimantService).setPassword(strCaptor.capture(), strCaptor.capture());
    assertEquals(List.of(TestFixtures.REF, "password"), strCaptor.getAllValues());
    assertEquals(HttpStatus.CREATED, actual.getStatusCode());
  }

  @Test
  @DisplayName("test create new password returns OK status")
  void testCreateNewPasswordReturnsOkStatus() {
    var request = new PasswordSetResetRequest();
    var totp = new Totp();
    request.setRef(TestFixtures.REF);
    request.setPassword("password");
    request.setTotp(Collections.singletonList(totp));
    var claimant = new Claimant();
    claimant.setAuth(new Auth());

    when(claimantService.findByRef(anyString())).thenReturn(claimant);
    when(totpVerifyService.verify(any(Claimant.class), any(Totp.class))).thenReturn(true);

    var actual = cut.updatePassword(request);

    verify(totpVerifyService).verify(claimantArgCaptor.capture(), totpArgCaptor.capture());
    verify(claimantService).setPassword(strCaptor.capture(), strCaptor.capture());
    assertEquals(List.of(TestFixtures.REF, "password"), strCaptor.getAllValues());
    assertEquals(HttpStatus.OK, actual.getStatusCode());
  }
}
