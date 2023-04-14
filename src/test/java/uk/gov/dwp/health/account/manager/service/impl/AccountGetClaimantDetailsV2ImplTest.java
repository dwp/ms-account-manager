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
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountGetClaimantDetailsV2ImplTest {

  @InjectMocks private AccountGetClaimantDetailsV2Impl cut;
  @Mock private ClaimantRepository repository;
  @Mock private AccountDataMapper dataMapper;
  @Captor private ArgumentCaptor<String> strArgCaptor;

  @Test
  @DisplayName("test get claimant details by email returns non empty list")
  void testGetClaimantDetailsByEmailReturnsNonEmptyList() {
    var id = UUID.randomUUID().toString();
    var claimant = mock(Claimant.class);
    when(repository.findByEmailAddress(anyString())).thenReturn(Optional.of(claimant));
    cut.getAccountDetailsByEmail(id);
    verify(dataMapper).mapToAccountDetails(claimant);
  }

  @Test
  @DisplayName("test get claimant details by email return empty list")
  void testGetClaimantDetailsByEmailReturnEmptyList() {
    var email = TestFixtures.EMAIL;
    when(repository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
    var actual = cut.getAccountDetailsByEmail(email);
    assertAll(
        "assert and verify all",
        () -> {
          verify(repository).findByEmailAddress(strArgCaptor.capture());
          assertThat(strArgCaptor.getValue()).isEqualTo(email);
          assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(actual.getBody()).isEmpty();
        });
  }

  @Test
  @DisplayName("test get claimant details by accountId returns non empty list")
  void testGetClaimantDetailsByAccountIdReturnsNonEmptyList() {
    var id = UUID.randomUUID().toString();
    var claimant = mock(Claimant.class);
    when(repository.findById(anyString())).thenReturn(Optional.of(claimant));
    cut.getAccountDetailsByRef(id);
    verify(dataMapper).mapToAccountDetails(claimant);
  }

  @Test
  @DisplayName("test get claimant details by accountId returns empty list")
  void testGetClaimantDetailsByAccountIdReturnsEmptyList() {
    var id = UUID.randomUUID().toString();
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    var actual = cut.getAccountDetailsByRef(id);
    assertAll(
        "assert and verify all",
        () -> {
          verify(repository).findById(strArgCaptor.capture());
          assertThat(strArgCaptor.getValue()).isEqualTo(id);
          assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(actual.getBody()).isEmpty();
          verifyNoInteractions(dataMapper);
        });
  }

  @Test
  @DisplayName("test get claimant details by nino")
  void testGetClaimantDetailsByNino() {
    var nino = TestFixtures.NINO;
    when(repository.findByNino(anyString())).thenReturn(Collections.emptyList());
    var actual = cut.getAccountDetailsByNino(nino);
    assertAll(
        "assert and verify all",
        () -> {
          verify(repository).findByNino(strArgCaptor.capture());
          assertThat(strArgCaptor.getValue()).isEqualTo(nino);
          assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(actual.getBody()).isEmpty();
          verify(dataMapper, never()).mapToAccountDetails(any(Claimant.class));
        });
    assertThat(strArgCaptor.getValue()).isEqualTo(nino);
    verifyNoInteractions(dataMapper);
  }
}
