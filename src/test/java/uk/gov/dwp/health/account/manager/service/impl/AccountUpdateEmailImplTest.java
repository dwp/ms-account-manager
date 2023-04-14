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
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountUpdateEmailImplTest {

  @InjectMocks private AccountUpdateEmailImpl cut;
  @Mock private ClaimantServiceImpl claimantService;
  @Mock private AccountDataMapper dataMapper;
  @Captor private ArgumentCaptor<String> strCaptor;
  @Captor private ArgumentCaptor<Claimant> claimantArgCaptor;

  @Test
  @DisplayName("test update email throws NullPointerException when the new email is null")
  void testUpdateEmailThrowsNullPointerExceptionWhenNewEmailIsNull() {
    assertThatThrownBy(() -> cut.updateEmail(GetUpdateEmailRequest(null, TestFixtures.CURRENT_EMAIL)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("New email must be a valid email");
  }

  @Test
  @DisplayName("test update email throws NullPointerException when the current email is null")
  void testUpdateEmailThrowsNullPointerExceptionWhenCurrentEmailIsNull() {
    assertThatThrownBy(() -> cut.updateEmail(GetUpdateEmailRequest(TestFixtures.NEW_EMAIL, null)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Current email must be a valid email");
  }

  @Test
  @DisplayName("test update email throws UnauthorizedException when claimant not found")
  void testUpdateEmailThrowsUnauthorizedExceptionWhenAccountNotFound() {
    when(claimantService.findByEmail(TestFixtures.CURRENT_EMAIL)).thenReturn(null);
    assertThatThrownBy(() -> cut.updateEmail(GetUpdateEmailRequest(TestFixtures.NEW_EMAIL, TestFixtures.CURRENT_EMAIL)))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Account does not exist for update");
    verify(claimantService, times(1)).findByEmail(strCaptor.capture());
    assertEquals(TestFixtures.CURRENT_EMAIL, strCaptor.getValue());
  }

  @Test
  @DisplayName("test update email throws DataValidationException when new email and current email are the same")
  void testUpdateEmailDThrowsDataValidationExceptionWhenNewEmailAndCurrentEmailAreEqual() {
    assertThatThrownBy(() -> cut.updateEmail(GetUpdateEmailRequest(TestFixtures.CURRENT_EMAIL, TestFixtures.CURRENT_EMAIL)))
        .isInstanceOf(DataValidationException.class)
        .hasMessage("New email must be different from current email");
  }

  @Test
  @DisplayName("test update email throws accountAlreadyExistException when new email returns record")
  void testUpdateEmailThrowsAccountAlreadyExistException() {
    when(claimantService.findByEmail(TestFixtures.CURRENT_EMAIL)).thenReturn(new Claimant());
    when(claimantService.findByEmail(TestFixtures.NEW_EMAIL)).thenReturn(new Claimant());
    assertThatThrownBy(() -> cut.updateEmail(GetUpdateEmailRequest(TestFixtures.NEW_EMAIL, TestFixtures.CURRENT_EMAIL)))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("EMAIL");
    verify(claimantService, times(2)).findByEmail(strCaptor.capture());
    assertEquals(TestFixtures.NEW_EMAIL, strCaptor.getValue());
    verifyNoMoreInteractions(claimantService);
  }

  @Test
  @DisplayName("test update email returns full account details with new email")
  void testUpdateEmailReturnsFullAccountDetailsWithNewEmail() {
    var currentEmail = TestFixtures.CURRENT_EMAIL;
    var newEmail = TestFixtures.NEW_EMAIL;
    var existingClaimant = mock(Claimant.class);
    when(existingClaimant.getEmailAddress()).thenReturn(newEmail);
    when(claimantService.findByEmail(currentEmail)).thenReturn(existingClaimant);
    cut.updateEmail(GetUpdateEmailRequest(newEmail, currentEmail));
    InOrder order = Mockito.inOrder(claimantService, dataMapper);
    order.verify(claimantService, times(2)).findByEmail(strCaptor.capture());
    assertThat(strCaptor.getAllValues()).containsSequence(currentEmail, newEmail);
    order.verify(claimantService).updateClaimant(claimantArgCaptor.capture());
    assertEquals(newEmail, claimantArgCaptor.getValue().getEmailAddress());
    verify(dataMapper).mapToAccountDetails(any(Claimant.class));
  }

  @Test
  @DisplayName("test validate email that it does not update the claimant")
  void testUpdateEmailDoesNotUpdateClaimantWhenNewEmailIsNull() {
    var currentEmail = TestFixtures.CURRENT_EMAIL;
    var newEmail = TestFixtures.NEW_EMAIL;
    when(claimantService.findByEmail(currentEmail)).thenReturn(new Claimant());
    cut.validateEmail(GetUpdateEmailRequest(newEmail, currentEmail));
    verify(claimantService, times(0)).updateClaimant(claimantArgCaptor.capture());
  }

  private UpdateEmailRequest GetUpdateEmailRequest(String newEmail, String currentEmail) {
    var request = new UpdateEmailRequest();
    request.newEmail(newEmail);
    request.currentEmail(currentEmail);
    return request;
  }
}
