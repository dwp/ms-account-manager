package uk.gov.dwp.health.account.manager.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static uk.gov.dwp.health.account.manager.mock.claimant.getClaimant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

class AccountUpdateTransferStatusImplTest {

  private AccountUpdateTransferStatusImpl accountUpdateTransferStatus;
  private ClaimantService claimantService;

  @BeforeEach
  void setup() {
    claimantService = mock(ClaimantService.class);
    accountUpdateTransferStatus = new AccountUpdateTransferStatusImpl(claimantService);
  }

  @Test
  @DisplayName("test update account transfer status")
  void testUpdateAccountTransferStatus() {
    var strCaptor = ArgumentCaptor.forClass(String.class);
    var claimantCaptor = ArgumentCaptor.forClass(Claimant.class);
    var email = TestFixtures.EMAIL;

    var claimant = getClaimant();

    when(claimantService.findByEmail(email)).thenReturn(claimant);

    var actual = accountUpdateTransferStatus.updateTransferStatus(email);

    var order = inOrder(claimantService);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    order.verify(claimantService).findByEmail(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.EMAIL);

    order.verify(claimantService).updateClaimant(claimantCaptor.capture());
    assertThat(claimantCaptor.getValue().getTransferredToDwpApply()).isTrue();
  }

  @Test
  @DisplayName("test update account transfer status when already transferred")
  void testUpdateAccountTransferStatusWhenAlreadyTransferred() {
    var email = TestFixtures.EMAIL;

    var claimant = getClaimant();
    claimant.setTransferredToDwpApply(true);

    when(claimantService.findByEmail(email)).thenReturn(claimant);

    var actual = accountUpdateTransferStatus.updateTransferStatus(email);
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
