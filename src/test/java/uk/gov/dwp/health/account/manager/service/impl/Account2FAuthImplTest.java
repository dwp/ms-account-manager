package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.http.totp.TotpGenerateRequest;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;
import uk.gov.dwp.health.account.manager.openapi.model.Totp.SourceEnum;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static uk.gov.dwp.health.account.manager.openapi.model.Totp.SourceEnum.EMAIL;
import static uk.gov.dwp.health.account.manager.openapi.model.Totp.SourceEnum.MOBILE;

class Account2FAuthImplTest {

  @InjectMocks private Account2FAuthImpl cut;
  @Mock private ClaimantService claimantService;
  @Mock private TotpRequestServiceImpl totpRequestService;
  @Mock private TotpVerifyServiceImpl totpVerifyService;

  private static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(MOBILE, true, false, TestFixtures.MOBILE, "MOBILE"),
        Arguments.of(EMAIL, false, true, TestFixtures.EMAIL, "EMAIL"));
  }

  @BeforeEach
  void setup() {
    claimantService = mock(ClaimantServiceImpl.class);
    totpRequestService = mock(TotpRequestServiceImpl.class);
    totpVerifyService = mock(TotpVerifyServiceImpl.class);
    cut = new Account2FAuthImpl(claimantService, totpRequestService, totpVerifyService);
  }

  @Test
  @DisplayName("test 2f auth throws AccountAuthFailException")
  void test2FAuthThrowsAccountAuthFailException() {
    var strCaptor = ArgumentCaptor.forClass(String.class);
    var claimantArgCaptor = ArgumentCaptor.forClass(Claimant.class);
    var totpArgCaptor = ArgumentCaptor.forClass(Totp.class);
    var request = mock(ValidTotpRequest.class);
    given(request.getRef()).willReturn(TestFixtures.REF);
    var totp = mock(Totp.class);
    given(totp.getSource()).willReturn(MOBILE);
    given(totp.getCode()).willReturn(TestFixtures.TOTP);
    given(request.getTotp()).willReturn(totp);
    var claimant = mock(Claimant.class);
    given(claimantService.findByRef(anyString())).willReturn(claimant);
    given(totpVerifyService.verify(any(Claimant.class), any(Totp.class))).willReturn(false);

    assertThrows(
        AccountAuthFailException.class,
        () -> {
          cut.do2FAuthentication(request);
        });

    InOrder inOrder = inOrder(claimantService, totpVerifyService);
    inOrder.verify(claimantService, times(1)).findByRef(strCaptor.capture());
    inOrder.verify(totpVerifyService).verify(claimantArgCaptor.capture(), totpArgCaptor.capture());
    assertThat(totpArgCaptor.getValue()).isEqualToComparingFieldByField(totp);
    assertThat(claimantArgCaptor.getValue()).isEqualTo(claimant);
    assertEquals(TestFixtures.REF, strCaptor.getValue());
  }

  @ParameterizedTest
  @MethodSource(value = "testCases")
  @DisplayName("test 2f auth successful on email or sms")
  void test2FAuthSmsOrEmailSuccessfulOnEmailOrSms(
      SourceEnum type, boolean sms, boolean email, String source, String comm) {
    var strCaptor = ArgumentCaptor.forClass(String.class);
    var claimantArgCaptor = ArgumentCaptor.forClass(Claimant.class);
    var totpArgCaptor = ArgumentCaptor.forClass(Totp.class);
    var reqArgCaptor = ArgumentCaptor.forClass(TotpGenerateRequest.class);
    var commArgCaptor = ArgumentCaptor.forClass(COMM.class);

    var request = mock(ValidTotpRequest.class);
    given(request.getRef()).willReturn(TestFixtures.REF);
    given(request.getGenerateSmsTotp()).willReturn(sms);
    given(request.getGenerateEmailTotp()).willReturn(email);

    var totp = mock(Totp.class);
    given(totp.getSource()).willReturn(type);
    given(totp.getCode()).willReturn(TestFixtures.TOTP);
    given(request.getTotp()).willReturn(totp);
    var claimant = new Claimant();
    claimant.setEmailAddress(source);
    claimant.setMobileNumber(source);
    given(claimantService.findByRef(anyString())).willReturn(claimant);

    given(totpVerifyService.verify(any(Claimant.class), any(Totp.class))).willReturn(true);

    var actual = cut.do2FAuthentication(request);
    assertEquals(HttpStatus.OK, actual.getStatusCode());

    InOrder inOrder = inOrder(claimantService, totpVerifyService, totpRequestService);
    inOrder.verify(claimantService, times(1)).findByRef(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.REF);
    inOrder.verify(totpVerifyService).verify(claimantArgCaptor.capture(), totpArgCaptor.capture());
    assertThat(totpArgCaptor.getValue()).isEqualToComparingFieldByField(totp);
    assertThat(claimantArgCaptor.getValue()).isEqualTo(claimant);
    inOrder
        .verify(totpRequestService, times(1))
        .requestNewTotp(claimantArgCaptor.capture(), commArgCaptor.capture());
    assertThat(totpArgCaptor.getValue()).isEqualToComparingFieldByField(totp);
    assertThat(claimantArgCaptor.getValue()).isEqualTo(claimant);
  }
}
