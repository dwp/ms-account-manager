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
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.openapi.model.V7NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.account.manager.openapi.model.V7NewAccountRequest.LanguageEnum.EN;
import static uk.gov.dwp.health.account.manager.openapi.model.V7NewAccountRequest.UserJourneyEnum.STRATEGIC;
import static uk.gov.dwp.health.account.manager.openapi.model.V7NewAccountRequest.UserJourneyEnum.TACTICAL;

@ExtendWith(MockitoExtension.class)
class AccountCreateV7ImplTest {

  @InjectMocks
  private AccountCreateV7Impl accountCreateV6Impl;
  @Mock
  private ClaimantService claimantService;
  @Captor
  private ArgumentCaptor<String> strCaptor;
  @Captor private ArgumentCaptor<Claimant> claimantCaptor;

  @Test
  @DisplayName("test throws accountExistException on nino")
  void testThrowsAccountExistExceptionOnNino() {
    var request = mock(V7NewAccountRequest.class);
    when(request.getDob()).thenReturn(TestFixtures.DOB);
    when(request.getPostcode()).thenReturn(TestFixtures.POSTCODE);
    when(request.getEmail()).thenReturn(TestFixtures.EMAIL);
    when(claimantService.findByEmail(anyString())).thenReturn(null);
    when(request.getNino()).thenReturn(TestFixtures.NINO);
    when(claimantService.findByNino(anyString())).thenReturn(List.of(new Claimant()));

    assertThatThrownBy(() -> accountCreateV6Impl.doCreateAccount(request))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("NINO");

    verify(claimantService).findByEmail(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.EMAIL);
    verify(claimantService).findByNino(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.NINO);
  }

  @Test
  @DisplayName("test throws dataValidationException on invalid postcode")
  void testThrowsDataValidationExceptionOnInvalidPostcode() {
    var request = mock(V7NewAccountRequest.class);
    when(request.getDob()).thenReturn(TestFixtures.DOB);
    when(request.getPostcode()).thenReturn("invalid-postcode");

    assertThatThrownBy(() -> accountCreateV6Impl.doCreateAccount(request))
        .isInstanceOf(DataValidationException.class)
        .hasMessage("Postcode is invalid");
  }

  @Test
  @DisplayName("test throws dataValidationException on future dob")
  void testThrowsDataValidationExceptionOnFutureDob() {
    var request = mock(V7NewAccountRequest.class);
    when(request.getDob()).thenReturn(LocalDate.now().plusDays(1));
    assertThatThrownBy(() -> accountCreateV6Impl.doCreateAccount(request))
        .isInstanceOf(DataValidationException.class)
        .hasMessage("Fail validation DOB is a future date");
  }

  @Test
  @DisplayName("test throws accountExistException")
  void testThrowsAccountExistException() {
    var request = mock(V7NewAccountRequest.class);
    when(request.getDob()).thenReturn(TestFixtures.DOB);
    when(request.getPostcode()).thenReturn(TestFixtures.POSTCODE);
    when(request.getEmail()).thenReturn(TestFixtures.EMAIL);
    when(claimantService.findByEmail(anyString())).thenReturn(new Claimant());

    assertThatThrownBy(() -> accountCreateV6Impl.doCreateAccount(request))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("EMAIL");
    verify(claimantService).findByEmail(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.EMAIL);
  }

  @Test
  @DisplayName("test tactical returns 201 and new account ref")
  void testTacticalReturns201AndNewAccountRef() {
    var request = new V7NewAccountRequest();
    request.setPostcode(TestFixtures.POSTCODE);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setEmail(TestFixtures.EMAIL);
    request.setNino(TestFixtures.NINO);
    request.setDob(TestFixtures.DOB);
    request.setSurname(TestFixtures.SURNAME);
    request.setForename(TestFixtures.FORENAME);
    request.setUserJourney(TACTICAL);
    request.setLanguage(EN);
    var savedClaimant = new Claimant();
    savedClaimant.setId(TestFixtures.REF);
    when(claimantService.findByEmail(anyString())).thenReturn(null).thenReturn(savedClaimant);
    when(claimantService.findByNino(anyString())).thenReturn(Collections.emptyList());

    var actual = accountCreateV6Impl.doCreateAccount(request);

    verify(claimantService).updateClaimant(claimantCaptor.capture());
    assertAll(
        "assert claimant details",
        () -> {
          var claimant = claimantCaptor.getValue();
          assertThat(claimant.getEmailAddress()).isEqualTo(TestFixtures.EMAIL);
          assertThat(claimant.getNino()).isEqualTo(TestFixtures.NINO);
          assertThat(claimant.getPostcode()).isEqualTo(TestFixtures.POSTCODE);
          assertThat(claimant.getForename()).isEqualTo(TestFixtures.FORENAME);
          assertThat(claimant.getSurname()).isEqualTo(TestFixtures.SURNAME);
          assertThat(claimant.getMobileNumber()).isEqualTo(TestFixtures.MOBILE);
          assertThat(claimant.getRegion().name()).isEqualTo("GB");
          assertThat(claimant.getLanguage()).isEqualTo(TestFixtures.LANGUAGE);
          assertThat(claimant.getUserJourney().name()).isEqualTo("TACTICAL");
        });
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var ref = Objects.requireNonNull(actual.getBody()).getRef();
    assertThat(ref).isEqualTo(TestFixtures.REF);

  }

  @Test
  @DisplayName("test strategic returns 201 and new account ref")
  void testStrategicReturns201AndNewAccountRef() {
    var request = new V7NewAccountRequest();
    request.setPostcode(TestFixtures.POSTCODE);
    request.setMobilePhone(TestFixtures.MOBILE);
    request.setEmail(TestFixtures.EMAIL);
    request.setNino(TestFixtures.NINO);
    request.setDob(TestFixtures.DOB);
    request.setSurname(TestFixtures.SURNAME);
    request.setForename(TestFixtures.FORENAME);
    request.setUserJourney(STRATEGIC);
    request.setLanguage(EN);
    var savedClaimant = new Claimant();
    savedClaimant.setId(TestFixtures.REF);
    when(claimantService.findByEmail(anyString())).thenReturn(null).thenReturn(savedClaimant);
    when(claimantService.findByNino(anyString())).thenReturn(Collections.emptyList());

    var actual = accountCreateV6Impl.doCreateAccount(request);

    verify(claimantService).updateClaimant(claimantCaptor.capture());
    assertAll(
        "assert claimant details",
        () -> {
          var claimant = claimantCaptor.getValue();
          assertThat(claimant.getEmailAddress()).isEqualTo(TestFixtures.EMAIL);
          assertThat(claimant.getNino()).isEqualTo(TestFixtures.NINO);
          assertThat(claimant.getPostcode()).isEqualTo(TestFixtures.POSTCODE);
          assertThat(claimant.getForename()).isEqualTo(TestFixtures.FORENAME);
          assertThat(claimant.getSurname()).isEqualTo(TestFixtures.SURNAME);
          assertThat(claimant.getMobileNumber()).isEqualTo(TestFixtures.MOBILE);
          assertThat(claimant.getRegion().name()).isEqualTo("GB");
          assertThat(claimant.getLanguage()).isEqualTo(TestFixtures.LANGUAGE);
          assertThat(claimant.getUserJourney().name()).isEqualTo("STRATEGIC");
        });
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var ref = Objects.requireNonNull(actual.getBody()).getRef();
    assertThat(ref).isEqualTo(TestFixtures.REF);

  }
}
