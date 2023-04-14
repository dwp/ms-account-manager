package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.ExternalServiceException;
import uk.gov.dwp.health.account.manager.http.totp.TotpVerifyRequest;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TotpVerifyServiceImplTest {

  @InjectMocks private TotpVerifyServiceImpl underTest;
  @Mock private SecureSecureHashServiceImpl secureHashService;
  @Mock private TotpClientServiceImpl totpClientService;
  @Captor private ArgumentCaptor<TotpVerifyRequest> verifyReqArgCaptor;

  @Test
  void testVerifyTotpcodesValid() {
    var totpMobile = mock(Totp.class);
    given(totpMobile.getSource()).willReturn(Totp.SourceEnum.MOBILE);
    given(totpMobile.getCode()).willReturn("123456");

    var claimant = mock(Claimant.class);
    given(claimant.getNino()).willReturn(TestFixtures.NINO);
    given(claimant.getMobileNumber()).willReturn(TestFixtures.MOBILE);

    given(secureHashService.hash(anyString())).willReturn("this_is_hashed_secret");
    given(totpClientService.postVerifyRequest(any(TotpVerifyRequest.class))).willReturn(true);

    boolean actual = underTest.verify(claimant, totpMobile);
    assertThat(actual).isTrue();
    verify(secureHashService).hash(TestFixtures.MOBILE + TestFixtures.NINO);
    verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder().totp("123456").secret("this_is_hashed_secret").build(),
            "totp",
            "secret");
  }

  @Test
  void testVerifyTOTPCodesInvalid() {
    var totpEmail = mock(Totp.class);
    given(totpEmail.getSource()).willReturn(Totp.SourceEnum.EMAIL);
    given(totpEmail.getCode()).willReturn("654321");

    var claimant = mock(Claimant.class);
    given(claimant.getEmailAddress()).willReturn(TestFixtures.EMAIL);
    given(claimant.getNino()).willReturn(TestFixtures.NINO);

    given(secureHashService.hash(anyString())).willReturn("this_is_hashed_email_secret");
    given(totpClientService.postVerifyRequest(any(TotpVerifyRequest.class))).willReturn(false);

    boolean actual = underTest.verify(claimant, totpEmail);
    assertThat(actual).isFalse();
    verify(secureHashService).hash(TestFixtures.EMAIL + TestFixtures.NINO);
    verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder()
                .totp("654321")
                .secret("this_is_hashed_email_secret")
                .build(),
            "totp",
            "secret");
  }

  @Test
  void testVerifyMoreThanOneTotpCode() {
    var totpEmail = mock(Totp.class);
    given(totpEmail.getSource()).willReturn(Totp.SourceEnum.EMAIL);
    given(totpEmail.getCode()).willReturn("654321");

    var totpMobile = mock(Totp.class);
    given(totpMobile.getSource()).willReturn(Totp.SourceEnum.MOBILE);
    given(totpMobile.getCode()).willReturn("123456");

    var claimant = mock(Claimant.class);
    given(claimant.getEmailAddress()).willReturn(TestFixtures.EMAIL);
    given(claimant.getMobileNumber()).willReturn(TestFixtures.MOBILE);
    given(claimant.getNino()).willReturn(TestFixtures.NINO, TestFixtures.NINO);

    given(secureHashService.hash(anyString()))
        .willReturn("this_is_hashed_email_secret", "this_is_hashed_secret");
    given(totpClientService.postVerifyRequest(any(TotpVerifyRequest.class))).willReturn(true, true);

    boolean actual = underTest.verify(claimant, totpEmail, totpMobile);
    assertThat(actual).isTrue();

    InOrder inOrder = inOrder(secureHashService, totpClientService);

    inOrder.verify(secureHashService).hash(TestFixtures.EMAIL + TestFixtures.NINO);
    inOrder.verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder()
                .totp("654321")
                .secret("this_is_hashed_email_secret")
                .build(),
            "totp",
            "secret");

    inOrder.verify(secureHashService).hash(TestFixtures.MOBILE + TestFixtures.NINO);
    inOrder.verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder().totp("123456").secret("this_is_hashed_secret").build(),
            "totp",
            "secret");
  }

  @Test
  void testVerifyMoreThanOneTotpCodeFaliure2ndCode() {
    var totpEmail = mock(Totp.class);
    given(totpEmail.getSource()).willReturn(Totp.SourceEnum.EMAIL);
    given(totpEmail.getCode()).willReturn("654321");

    var totpMobile = mock(Totp.class);
    given(totpMobile.getSource()).willReturn(Totp.SourceEnum.MOBILE);
    given(totpMobile.getCode()).willReturn("123456");

    var claimant = mock(Claimant.class);
    given(claimant.getEmailAddress()).willReturn(TestFixtures.EMAIL);
    given(claimant.getMobileNumber()).willReturn(TestFixtures.MOBILE);
    given(claimant.getNino()).willReturn(TestFixtures.NINO, TestFixtures.NINO);

    given(secureHashService.hash(anyString()))
        .willReturn("this_is_hashed_email_secret", "this_is_hashed_secret");
    given(totpClientService.postVerifyRequest(any(TotpVerifyRequest.class)))
        .willReturn(true, false);

    boolean actual = underTest.verify(claimant, totpEmail, totpMobile);
    assertThat(actual).isFalse();

    InOrder inOrder = inOrder(secureHashService, totpClientService);

    inOrder.verify(secureHashService).hash(TestFixtures.EMAIL + TestFixtures.NINO);
    inOrder.verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder()
                .totp("654321")
                .secret("this_is_hashed_email_secret")
                .build(),
            "totp",
            "secret");

    inOrder.verify(secureHashService).hash(TestFixtures.MOBILE + TestFixtures.NINO);
    inOrder.verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder().totp("123456").secret("this_is_hashed_secret").build(),
            "totp",
            "secret");
  }

  @Test
  void testVerifyMoreThanOneTotpCodeFailue1stcode() {
    var totpEmail = mock(Totp.class);
    given(totpEmail.getSource()).willReturn(Totp.SourceEnum.EMAIL);
    given(totpEmail.getCode()).willReturn("654321");

    var totpMobile = mock(Totp.class);

    var claimant = mock(Claimant.class);
    given(claimant.getEmailAddress()).willReturn(TestFixtures.EMAIL);
    given(claimant.getNino()).willReturn(TestFixtures.NINO, TestFixtures.NINO);

    given(secureHashService.hash(anyString()))
        .willReturn("this_is_hashed_email_secret", "this_is_hashed_secret");
    given(totpClientService.postVerifyRequest(any(TotpVerifyRequest.class)))
        .willReturn(false, true);

    boolean actual = underTest.verify(claimant, totpEmail, totpMobile);
    assertThat(actual).isFalse();

    InOrder inOrder = inOrder(secureHashService, totpClientService);

    inOrder.verify(secureHashService).hash(TestFixtures.EMAIL + TestFixtures.NINO);
    inOrder.verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder()
                .totp("654321")
                .secret("this_is_hashed_email_secret")
                .build(),
            "totp",
            "secret");

    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void testVerifyThrowsException() {
    var totpEmail = mock(Totp.class);
    given(totpEmail.getSource()).willReturn(Totp.SourceEnum.EMAIL);
    given(totpEmail.getCode()).willReturn("654321");

    var claimant = mock(Claimant.class);
    given(claimant.getEmailAddress()).willReturn(TestFixtures.EMAIL);
    given(claimant.getNino()).willReturn(TestFixtures.NINO, TestFixtures.NINO);

    given(secureHashService.hash(anyString()))
        .willReturn("this_is_hashed_email_secret", "this_is_hashed_secret");
    given(totpClientService.postVerifyRequest(any(TotpVerifyRequest.class)))
        .willThrow(ExternalServiceException.class);

    assertThrows(ExternalServiceException.class, () -> underTest.verify(claimant, totpEmail));

    InOrder inOrder = inOrder(secureHashService, totpClientService);

    inOrder.verify(secureHashService).hash(TestFixtures.EMAIL + TestFixtures.NINO);
    inOrder.verify(totpClientService).postVerifyRequest(verifyReqArgCaptor.capture());
    assertThat(verifyReqArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            TotpVerifyRequest.builder()
                .totp("654321")
                .secret("this_is_hashed_email_secret")
                .build(),
            "totp",
            "secret");
  }
}
