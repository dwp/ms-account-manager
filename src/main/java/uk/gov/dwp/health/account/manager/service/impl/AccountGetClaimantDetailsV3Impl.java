package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountDetails;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountGetClaimantDetails;
import uk.gov.dwp.health.account.manager.utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountGetClaimantDetailsV3Impl extends AccountDataMapper
    implements AccountGetClaimantDetails<ResponseEntity<List<V3AccountDetails>>> {

  private final ClaimantRepository repository;

  public AccountGetClaimantDetailsV3Impl(
      ClaimantRepository repository) {
    this.repository = repository;
  }

  @Override
  public ResponseEntity<List<V3AccountDetails>> getAccountDetailsByEmail(String email) {
    var results = new ArrayList<V3AccountDetails>();
    repository.findByEmailAddress(email).ifPresent(c -> results.add(mapToV3AccountDetails(c)));
    return ResponseEntity.ok(results);
  }

  @Override
  public ResponseEntity<List<V3AccountDetails>> getAccountDetailsByNino(String nino) {
    return ResponseEntity.ok(
        repository
            .findByNino(InputValidator.normaliseInputUpper(nino))
            .stream()
            .map(this::mapToV3AccountDetails)
            .collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<List<V3AccountDetails>> getAccountDetailsByRef(String ref) {
    var results = new ArrayList<V3AccountDetails>();
    repository.findById(ref).ifPresent(c -> results.add(mapToV3AccountDetails(c)));
    return ResponseEntity.ok(results);
  }

}
