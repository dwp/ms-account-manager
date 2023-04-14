package uk.gov.dwp.health.account.manager.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountGetClaimantDetails;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class AccountGetClaimantDetailsImpl implements AccountGetClaimantDetails {

  @Getter private final ClaimantService claimantService;
  @Getter private final AccountDataMapper dataMapper;

  public AccountGetClaimantDetailsImpl(
      ClaimantService claimantService, AccountDataMapper dataMapper) {
    this.claimantService = claimantService;
    this.dataMapper = dataMapper;
  }

  @Override
  public ResponseEntity<AccountDetails> getAccountDetailsByEmail(String email) {
    AtomicReference<AccountDetails> accountDetails = new AtomicReference<>();
    final Optional<Claimant> optional = claimantService.findAccountBy(email);
    optional.ifPresentOrElse(
            c -> accountDetails.set(this.dataMapper.mapToAccountDetails(c)),
            () -> {
              throw new AccountNotFoundException("Account does not exist");
            });
    return ResponseEntity.ok().body(accountDetails.get());
  }

  @Override
  public ResponseEntity<List<AccountDetails>> getAccountDetailsByNino(String nino) {
    final List<Claimant> claimant = claimantService.findByNino(nino);
    log.info("Get account details by ID - an account found");
    return ResponseEntity.ok()
        .body(claimant.stream().map(dataMapper::mapToAccountDetails).collect(Collectors.toList()));
  }

  @Override
  public ResponseEntity<AccountDetails> getAccountDetailsByRef(String ref) {
    var claimant = claimantService.findByRef(ref);
    log.info("Get account details by ID - an account found");
    return ResponseEntity.status(HttpStatus.OK).body(this.dataMapper.mapToAccountDetails(claimant));
  }
}
