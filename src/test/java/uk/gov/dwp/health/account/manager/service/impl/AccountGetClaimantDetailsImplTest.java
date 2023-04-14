package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountGetClaimantDetailsImplTest {

  @InjectMocks private AccountGetClaimantDetailsImpl cut;
  @Mock private ClaimantService claimantService;
  @Mock private AccountDataMapper dataMapper;
  @Captor private ArgumentCaptor<String> strCaptor;

  @Test
  @DisplayName("test get claimant details by Email throws AccountNotFoundException")
  void testGetClaimantDetailsByEmailThrowsAccountNotFoundException() {
    var email = TestFixtures.EMAIL;
    when(claimantService.findAccountBy(anyString())).thenReturn(Optional.empty());

    var actual =
        assertThrows(AccountNotFoundException.class, () -> cut.getAccountDetailsByEmail(email));

    assertEquals("Account does not exist", actual.getMessage());
    verify(claimantService).findAccountBy(strCaptor.capture());
    assertEquals(email, strCaptor.getValue());
  }

  @Test
  @DisplayName("test get claimant details by email")
  void testGetClaimantDetailsByEmailReturnDetails() {
    var email = TestFixtures.EMAIL;
    var claimant = mock(Claimant.class);
    when(claimantService.findAccountBy(anyString())).thenReturn(Optional.of(claimant));
    cut.getAccountDetailsByEmail(email);
    verify(dataMapper).mapToAccountDetails(claimant);
  }

  @Test
  @DisplayName("test get claimant details by Nino returns empty")
  void testGetClaimantDetailsByNino() {
    var nino = TestFixtures.NINO;
    given(claimantService.findByNino(anyString())).willReturn(Collections.emptyList());
    cut.getAccountDetailsByNino(nino);
    verify(claimantService).findByNino(strCaptor.capture());
    assertEquals(nino, strCaptor.getValue());
    verify(dataMapper, never()).mapToAccountDetails(any(Claimant.class));
  }

  @Test
  @DisplayName("test get claimant details by account Id returns account details")
  void testGetClaimantDetailsByAccountIdReturnsEmpty() {
    var ref = TestFixtures.REF;
    var claimant = mock(Claimant.class);
    given(claimantService.findByRef(anyString())).willReturn(claimant);
    cut.getAccountDetailsByRef(ref);
    verify(claimantService).findByRef(strCaptor.capture());
    verify(dataMapper).mapToAccountDetails(claimant);
  }
}
