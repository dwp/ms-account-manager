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
import uk.gov.dwp.health.account.manager.openapi.model.UpdateNinoRequest;
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

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AccountUpdateNinoImplTest {

  @InjectMocks private AccountUpdateNinoImpl cut;
  @Mock private ClaimantServiceImpl claimantService;
  @Mock private AccountDataMapper dataMapper;
  @Captor private ArgumentCaptor<String> strCaptor;
  @Captor private ArgumentCaptor<Claimant> claimantArgCaptor;

  @Test
  @DisplayName("test update nino throws NullPointerException when the new NINO is null")
  void testUpdateNinoThrowsNullPointerExceptionWhenNewNinoIsNull() {
    assertThatThrownBy(() -> cut.updateNino(GetUpdateNinoRequest(null, TestFixtures.CURRENT_NINO)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("New NINO must be a valid NINO");
  }

  @Test
  @DisplayName("test update nino throws NullPointerException when the current NINO is null")
  void testUpdateNinoThrowsNullPointerExceptionWhenCurrentNinoIsNull() {
    assertThatThrownBy(() -> cut.updateNino(GetUpdateNinoRequest(TestFixtures.NEW_NINO, null)))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Current NINO must be a valid NINO");
  }

  @Test
  @DisplayName("test update nino throws UnauthorizedException when claimant not found")
  void testUpdateNinoThrowsUnauthorizedExceptionWhenAccountNotFound() {
    when(claimantService.findByNino(TestFixtures.CURRENT_NINO)).thenReturn(new ArrayList<Claimant>());
    assertThatThrownBy(() -> cut.updateNino(GetUpdateNinoRequest(TestFixtures.NEW_NINO, TestFixtures.CURRENT_NINO)))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Account does not exist for update");
    verify(claimantService, times(1)).findByNino(strCaptor.capture());
    assertEquals(TestFixtures.CURRENT_NINO, strCaptor.getValue());
  }

  @Test
  @DisplayName("test update nino throws DataValidationException when new NINO and current NINO are the same")
  void testUpdateNinoDThrowsDataValidationExceptionWhenNewNinoAndCurrentNinoAreEqual() {
    assertThatThrownBy(() -> cut.updateNino(GetUpdateNinoRequest(TestFixtures.CURRENT_NINO, TestFixtures.CURRENT_NINO)))
        .isInstanceOf(DataValidationException.class)
        .hasMessage("New NINO must be different from current NINO");
  }

  @Test
  @DisplayName("test update nino throws accountAlreadyExistException when new nino returns record")
  void testUpdateNinoThrowsAccountAlreadyExistException() {
    when(claimantService.findByNino(TestFixtures.CURRENT_NINO)).thenReturn(getClaimants());
    when(claimantService.findByNino(TestFixtures.NEW_NINO)).thenReturn(getClaimants());
    assertThatThrownBy(() -> cut.updateNino(GetUpdateNinoRequest(TestFixtures.NEW_NINO, TestFixtures.CURRENT_NINO)))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("NINO");
    verify(claimantService, times(2)).findByNino(strCaptor.capture());
    assertEquals(TestFixtures.NEW_NINO, strCaptor.getValue());
    verifyNoMoreInteractions(claimantService);
  }

  @Test
  @DisplayName("test update nino returns full account details with new nino")
  void testUpdateNinoReturnsFullAccountDetailsWithNewNino() {
    var currentNino = TestFixtures.CURRENT_NINO;
    var newNino = TestFixtures.NEW_NINO;
    var existingClaimant = mock(Claimant.class);
    when(existingClaimant.getNino()).thenReturn(newNino);
    when(claimantService.findByNino(currentNino)).thenReturn(getClaimants(existingClaimant));
    cut.updateNino(GetUpdateNinoRequest(newNino, currentNino));
    InOrder order = Mockito.inOrder(claimantService, dataMapper);
    order.verify(claimantService, times(2)).findByNino(strCaptor.capture());
    assertThat(strCaptor.getAllValues()).containsSequence(currentNino, newNino);
    order.verify(claimantService).updateClaimant(claimantArgCaptor.capture());
    assertEquals(newNino, claimantArgCaptor.getValue().getNino());
    verify(dataMapper).mapToAccountDetails(any(Claimant.class));
  }

  @Test
  @DisplayName("test validate nino that it does not update the claimant")
  void testUpdateNinoDoesNotUpdateClaimantWhenNewNinoIsNull() {
    var currentNino = TestFixtures.CURRENT_NINO;
    var newNino = TestFixtures.NEW_NINO;
    when(claimantService.findByNino(currentNino)).thenReturn(getClaimants());
    cut.validateNino(GetUpdateNinoRequest(newNino, currentNino));
    verify(claimantService, times(0)).updateClaimant(claimantArgCaptor.capture());
  }

  private UpdateNinoRequest GetUpdateNinoRequest(String newNino, String currentNino) {
    var request = new UpdateNinoRequest();
    request.newNino(newNino);
    request.currentNino(currentNino);
    return request;
  }

  private List<Claimant> getClaimants() {
    return getClaimants(mock(Claimant.class));
  }
  
  private ArrayList<Claimant> getClaimants(Claimant claimant) {
    var claimants = new ArrayList<Claimant>();
    claimants.add(claimant);
    return claimants;
  }
}
