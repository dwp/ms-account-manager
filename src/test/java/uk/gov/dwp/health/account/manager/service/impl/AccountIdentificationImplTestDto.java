package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountIdentificationImplTestDto {

  @InjectMocks private AccountIdentificationImpl cut;
  @Mock private ClaimantServiceImpl claimantService;
  @Mock private TotpRequestServiceImpl totpRequestService;
  @Captor private ArgumentCaptor<String> strCaptor;
  @Captor private ArgumentCaptor<LocalDate> dateArgCaptor;

  private static Stream<Arguments> firstAndSecondPlusStatus() {
    return Stream.of(
        Arguments.of(STAGE.FIRST.current(), true, "ACTIVE", 1),
        Arguments.of(STAGE.SECONDPLUS.current(), true, "ACTIVE", 1),
        Arguments.of(STAGE.FIRST.current(), false, "ACTIVE", 0),
        Arguments.of(STAGE.SECONDPLUS.current(), false, "ACTIVE", 0));
  }

  private static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(STAGE.LOCKED.current(), "LOCKED"),
        Arguments.of(STAGE.PENDING.current(), "PENDING"));
  }

  @Test
  @DisplayName("test identification throws AccountNotFoundException")
  void testIdentificationThrowsAccountNotFoundException() {
    var request = mock(IdRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getNino()).willReturn(TestFixtures.NINO);
    given(request.getDob()).willReturn(TestFixtures.DOB);

    given(claimantService.findAccountBy(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.empty());
    assertThrows(AccountNotFoundException.class, () -> cut.doIdentification(request));
    verifyNoInteractions(totpRequestService);
  }

  @Test
  @DisplayName("test identification account not active")
  void testIdentificationAccountNotActive() {
    var request = mock(IdRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getNino()).willReturn(TestFixtures.NINO);
    given(request.getDob()).willReturn(TestFixtures.DOB);

    var claimant = mock(Claimant.class);
    when(claimant.getId()).thenReturn("1234");
    given(claimant.getAuth()).willReturn(null);

    given(claimantService.findAccountBy(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.of(claimant));
    var actual = cut.doIdentification(request);

    verify(claimantService)
        .findAccountBy(strCaptor.capture(), strCaptor.capture(), dateArgCaptor.capture());
    assertThat(strCaptor.getAllValues()).containsSequence(TestFixtures.EMAIL, TestFixtures.NINO);
    assertThat(dateArgCaptor.getValue()).isEqualTo(TestFixtures.DOB);
    assertThat(actual.getBody().getRef()).isEqualTo("1234");
    assertThat(actual.getBody().getStatus().name()).isEqualTo("PENDING");
  }

  @Test
  @DisplayName("test identification account locked")
  void testIdentificationAccountLocked() {
    var request = mock(IdRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getNino()).willReturn(TestFixtures.NINO);
    given(request.getDob()).willReturn(TestFixtures.DOB);

    var auth = mock(Auth.class);
    when(auth.getStatus()).thenReturn(STAGE.LOCKED.current());
    var claimant = mock(Claimant.class);
    given(claimant.getAuth()).willReturn(auth);
    given(claimantService.findAccountBy(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.of(claimant));

    var actual = cut.doIdentification(request);
    verify(claimantService)
        .findAccountBy(strCaptor.capture(), strCaptor.capture(), dateArgCaptor.capture());
    assertThat(strCaptor.getAllValues()).containsSequence(TestFixtures.EMAIL, TestFixtures.NINO);
    assertThat(dateArgCaptor.getValue()).isEqualTo(TestFixtures.DOB);
    assertThat(actual.getBody().getStatus().name()).isEqualTo("LOCKED");
  }

  @ParameterizedTest
  @MethodSource(value = "testCases")
  @DisplayName("test identification send totp by sms and email")
  void testIdentificationSendTotpBySmsAndEmail(int status, String expected) {
    var request = mock(IdRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getNino()).willReturn(TestFixtures.NINO);
    given(request.getDob()).willReturn(TestFixtures.DOB);
    given(request.getGenerateEmailTotp()).willReturn(true);
    given(request.getGenerateSmsTotp()).willReturn(true);

    var claimant = mock(Claimant.class);

    var auth = mock(Auth.class);
    when(auth.getStatus()).thenReturn(status);
    when(claimant.getAuth()).thenReturn(auth);
    given(claimantService.findAccountBy(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.of(claimant));

    var actual = cut.doIdentification(request);

    InOrder order = inOrder(claimantService, totpRequestService);
    order
        .verify(claimantService)
        .findAccountBy(strCaptor.capture(), strCaptor.capture(), dateArgCaptor.capture());
    assertThat(strCaptor.getAllValues()).containsSequence(TestFixtures.EMAIL, TestFixtures.NINO);
    assertThat(dateArgCaptor.getValue()).isEqualTo(TestFixtures.DOB);
    order.verify(totpRequestService, times(2)).requestNewTotp(any(Claimant.class), any(COMM.class));
  }

  @ParameterizedTest
  @MethodSource(value = "firstAndSecondPlusStatus")
  void testAccountIdentificationAccountActiveRequested200(
      int status, boolean email, String expectedStatus, int invocationCount) {
    var request = mock(IdRequest.class);
    given(request.getEmail()).willReturn(TestFixtures.EMAIL);
    given(request.getNino()).willReturn(TestFixtures.NINO);
    given(request.getDob()).willReturn(TestFixtures.DOB);
    given(request.getGenerateEmailTotp()).willReturn(email);
    given(request.getGenerateSmsTotp()).willReturn(false);
    var claimant = mock(Claimant.class);

    var auth = mock(Auth.class);
    when(auth.getStatus()).thenReturn(status);
    when(claimant.getAuth()).thenReturn(auth);
    given(claimantService.findAccountBy(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.of(claimant));

    var actual = cut.doIdentification(request);
    InOrder order = inOrder(claimantService, totpRequestService);
    order
        .verify(claimantService)
        .findAccountBy(strCaptor.capture(), strCaptor.capture(), dateArgCaptor.capture());
    assertThat(strCaptor.getAllValues()).containsSequence(TestFixtures.EMAIL, TestFixtures.NINO);
    assertThat(dateArgCaptor.getValue()).isEqualTo(TestFixtures.DOB);
    verify(totpRequestService, times(invocationCount))
        .requestNewTotp(any(Claimant.class), any(COMM.class));
    assertThat(actual.getBody().getStatus().name()).isEqualTo(expectedStatus);
  }
}
