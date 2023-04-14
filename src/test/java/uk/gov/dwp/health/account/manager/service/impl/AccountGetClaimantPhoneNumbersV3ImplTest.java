package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountMobilePhone;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountGetClaimantPhoneNumbersV3ImplTest {

  @InjectMocks private AccountGetClaimantPhoneNumbersV3Impl cut;
  @Mock private ClaimantRepository claimantRepository;

  @Test
  @DisplayName("get Account Phone Numbers By Id")
  void getAccountPhoneNumbersById() {
    when(claimantRepository.findAllById(any())).thenReturn(Arrays.asList(
        getClaimant("1"), getClaimant("2"), getClaimant("3")
    ));
    ResponseEntity<List<V3AccountMobilePhone>> phoneNumbers = cut.getMobilePhoneNumbersByClaimantId("1,2,3");
    verify(claimantRepository, times(1)).findAllById(any());
    assertEquals(3, phoneNumbers.getBody().size());
    final boolean[] found123 = new boolean[]{false, false, false};
    phoneNumbers.getBody().stream().forEach(s -> {
      if ("1".equals(s.getClaimantId())) {
        found123[0] = true;
        assertEquals("1", s.getClaimantId());
      }
      if ("2".equals(s.getClaimantId())) {
        found123[1] = true;
        assertEquals("2", s.getClaimantId());
      }
      if ("3".equals(s.getClaimantId())) {
        found123[2] = true;
        assertEquals("3", s.getClaimantId());
      }
    });
    assertTrue(found123[0]);
    assertTrue(found123[1]);
    assertTrue(found123[2]);
  }

  private static Claimant getClaimant(final String id) {
      return Claimant.builder()
        .mobileNumber(id)
        .id(id).build();
  }
}
