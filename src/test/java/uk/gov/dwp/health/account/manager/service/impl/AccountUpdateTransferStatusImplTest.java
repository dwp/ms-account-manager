package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.health.account.manager.mock.claimant.getClaimant;

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
    var accountId = TestFixtures.REF;

    var claimant = getClaimant();

    when(claimantService.findByRef(accountId)).thenReturn(claimant);

    var actual = accountUpdateTransferStatus.updateTransferStatus(accountId);

    var order = inOrder(claimantService);

    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    order.verify(claimantService).findByRef(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.REF);

    order.verify(claimantService).updateClaimant(claimantCaptor.capture());
    assertThat(claimantCaptor.getValue().getTransferredToDwpApply()).isTrue();
  }
}
