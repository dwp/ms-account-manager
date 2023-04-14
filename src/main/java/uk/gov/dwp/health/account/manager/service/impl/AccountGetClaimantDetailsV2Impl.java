package uk.gov.dwp.health.account.manager.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountGetClaimantDetails;
import uk.gov.dwp.health.account.manager.utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AccountGetClaimantDetailsV2Impl implements AccountGetClaimantDetails {

  @Getter private final ClaimantRepository repository;
  @Getter private final AccountDataMapper dataMapper;

  public AccountGetClaimantDetailsV2Impl(
      ClaimantRepository repository,
      AccountDataMapper dataMapper) {
    this.repository = repository;
    this.dataMapper = dataMapper;
  }

  @Override
  public ResponseEntity<List<AccountDetails>> getAccountDetailsByEmail(String email) {
    var result = new ArrayList<AccountDetails>();
    repository
        .findByEmailAddress(InputValidator.normaliseInputLower(email))
        .ifPresent(c -> result.add(getDataMapper().mapToAccountDetails(c)));
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<List<AccountDetails>> getAccountDetailsByNino(String nino) {
    var claimant =
        repository.findByNino(InputValidator.normaliseInputUpper(nino));
    log.info("Get account details by ID - an account found");
    return ResponseEntity.ok()
        .body(claimant.stream().map(dataMapper::mapToAccountDetails).collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<List<AccountDetails>> getAccountDetailsByRef(String ref) {
    var result = new ArrayList<AccountDetails>();
    repository.findById(ref).ifPresent(c -> result.add(getDataMapper().mapToAccountDetails(c)));
    return ResponseEntity.ok(result);
  }
}
