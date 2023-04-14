package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountCreateImplTest {

  @InjectMocks private AccountCreateImpl cut;
  @Mock private ClaimantService claimantService;
  @Captor private ArgumentCaptor<String> strCaptor;

  @Test
  @DisplayName("test create account throws accountExistException against nino")
  void testCreateAccountThrowsAccountExistExceptionAgainstNino() {
    var request = mock(NewAccountRequest.class);
    when(request.getNino()).thenReturn(TestFixtures.NINO);
    when(claimantService.findByNino(anyString()))
        .thenReturn(Collections.singletonList(Claimant.builder().build()));

    assertThatThrownBy(() -> cut.doCreateAccount(request))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("NINO");

    verify(claimantService).findByNino(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.NINO);
  }

  @Test
  @DisplayName("test create account throws accountExistException against email")
  void testCreateAccountThrowsAccountExistExceptionAgainstEmail() {
    var request = mock(NewAccountRequest.class);
    when(request.getNino()).thenReturn(TestFixtures.NINO);
    when(request.getEmail()).thenReturn(TestFixtures.EMAIL);

    when(claimantService.findByNino(anyString())).thenReturn(Collections.emptyList());
    when(claimantService.findByEmail(anyString())).thenReturn(new Claimant());

    assertThatThrownBy(() -> cut.doCreateAccount(request))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("EMAIL");

    InOrder order = Mockito.inOrder(claimantService);
    order.verify(claimantService).findByNino(strCaptor.capture());
    order.verify(claimantService).findByEmail(strCaptor.capture());
    assertThat(strCaptor.getAllValues()).isEqualTo(List.of(TestFixtures.NINO, TestFixtures.EMAIL));
  }

  @Test
  @DisplayName("test create account throws dataValidationException with future DOB")
  void testCreateAccountThrowsDataValidationExceptionWithFutureDob() {
    var request = mock(NewAccountRequest.class);
    when(request.getDob()).thenReturn(LocalDate.now().plusDays(1));
    assertThatThrownBy(() -> cut.doCreateAccount(request))
        .isInstanceOf(DataValidationException.class)
        .hasMessage("Fail validation DOB is a future date");
  }

  @Test
  @DisplayName("test create account throws dataValidationException with invalid postcode")
  void testCreateAccountThrowsDataValidationExceptionWithInvalidPostcode() {
    var request = mock(NewAccountRequest.class);
    when(request.getDob()).thenReturn(LocalDate.now().minusDays(365));
    when(request.getPostcode()).thenReturn("invalid-postcode");
    assertThatThrownBy(() -> cut.doCreateAccount(request))
        .isInstanceOf(DataValidationException.class)
        .hasMessage("Postcode is invalid");
  }

  @Test
  @DisplayName("test create account return AccountReturn with Created 201")
  void testCreateAccountReturnAccountReturnWithCreated201() {
    var request = mock(NewAccountRequest.class);
    when(request.getDob()).thenReturn(LocalDate.now().minusDays(365));
    when(request.getPostcode()).thenReturn("LS1 7XX");
    given(claimantService.createAccount(request)).willReturn("12345");
    ResponseEntity<AccountReturn> actual = cut.doCreateAccount(request);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(Objects.requireNonNull(actual.getBody()).getRef()).isEqualTo("12345");
  }
}
