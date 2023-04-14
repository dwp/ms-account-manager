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
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountGetClaimantDetailsV4ImplTest {

  @InjectMocks private AccountGetClaimantDetailsV4Impl cut;
  @Mock private ClaimantRepository repository;
  @Captor private ArgumentCaptor<String> strArgCaptor;

  @Test
  @DisplayName("test get account by email details returns empty list")
  void testGetAccountByEmailDetailsReturnsEmptyList() {
    var email = TestFixtures.EMAIL;
    when(repository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
    var actual = cut.getAccountDetailsByEmail(email);
    verify(repository).findByEmailAddress(strArgCaptor.capture());
    assertEquals(email, strArgCaptor.getValue());
    assertEquals(HttpStatus.OK, actual.getStatusCode());
  }

  @Test
  @DisplayName("test get account details by nino returns empty list")
  void testGetAccountDetailsByNinoReturnsEmptyList() {
    var nino = TestFixtures.NINO;
    when(repository.findByNino(anyString())).thenReturn(Collections.emptyList());
    var actual = cut.getAccountDetailsByNino(nino);
    verify(repository).findByNino(strArgCaptor.capture());
    assertEquals(List.of(nino), strArgCaptor.getAllValues());
    assertEquals(HttpStatus.OK, actual.getStatusCode());
  }

  @Test
  @DisplayName("test get account details by account id")
  void testGetAccountDetailsByAccountId() {
    var ref = TestFixtures.REF;
    when(repository.findById(anyString())).thenReturn(Optional.empty());
    var actual = cut.getAccountDetailsByRef(ref);
    verify(repository).findById(strArgCaptor.capture());
    assertEquals(ref, strArgCaptor.getValue());
    assertEquals(HttpStatus.OK, actual.getStatusCode());
  }

}
