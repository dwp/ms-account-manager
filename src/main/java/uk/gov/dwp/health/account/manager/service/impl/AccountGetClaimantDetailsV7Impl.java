package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.V7AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V7AccountDetails;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountGetClaimantDetails;
import uk.gov.dwp.health.account.manager.utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountGetClaimantDetailsV7Impl extends AccountDataMapper
    implements AccountGetClaimantDetails<ResponseEntity<List<V7AccountDetails>>> {

  private final ClaimantRepository repository;

  public AccountGetClaimantDetailsV7Impl(
      ClaimantRepository repository) {
    this.repository = repository;
  }

  @Override
  public ResponseEntity<List<V7AccountDetails>> getAccountDetailsByEmail(String email) {
    var results = new ArrayList<V7AccountDetails>();
    repository.findByEmailAddress(email).ifPresent(c -> results.add(mapToV7AccountDetails(c)));
    return ResponseEntity.ok(results);
  }

  @Override
  public ResponseEntity<List<V7AccountDetails>> getAccountDetailsByNino(String nino) {
    return ResponseEntity.ok(
        repository
            .findByNino(InputValidator.normaliseInputUpper(nino))
            .stream()
            .map(this::mapToV7AccountDetails)
            .collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<List<V7AccountDetails>> getAccountDetailsByRef(String ref) {
    var results = new ArrayList<V7AccountDetails>();
    repository.findById(ref).ifPresent(c -> results.add(mapToV7AccountDetails(c)));
    return ResponseEntity.ok(results);
  }

}
