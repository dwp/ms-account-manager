package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountMobilePhone;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountGetClaimantPhoneNumbers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AccountGetClaimantPhoneNumbersV3Impl extends AccountDataMapper
    implements AccountGetClaimantPhoneNumbers<ResponseEntity<List<V3AccountMobilePhone>>> {

  private final ClaimantRepository claimantRepository;

  public AccountGetClaimantPhoneNumbersV3Impl(
      final ClaimantRepository claimantRepository) {
    this.claimantRepository = claimantRepository;
  }

  @Override
  public ResponseEntity<List<V3AccountMobilePhone>> getMobilePhoneNumbersByClaimantId(
      String claimantIdCsv
  ) {
    final List<String> ids = Arrays.asList(claimantIdCsv.split(","));
    final var claimantIterable = claimantRepository.findAllById(ids);
    final List<V3AccountMobilePhone> result = new LinkedList<>();
    claimantIterable.forEach(c -> {
      final var mobilePhone = new V3AccountMobilePhone();
      mobilePhone.setMobilePhone(c.getMobileNumber());
      mobilePhone.setClaimantId(c.getId());
      result.add(mobilePhone);
    });
    return ResponseEntity.ok(result);
  }
}
