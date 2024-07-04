package uk.gov.dwp.health.account.manager.service.impl;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.service.Account1FAuth;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountLockedService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountPasswordService;

@Slf4j
public class Account1FAuthImplV2
    implements Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> {
  private final ClaimantService claimantService;
  private final VerifyClaimantAccountLockedService<Claimant> verifyClaimantAccountLockedService;
  private final VerifyClaimantAccountPasswordService<Claimant, String>
      verifyClaimantAccountPasswordService;

  public Account1FAuthImplV2(
      ClaimantService claimantService,
      VerifyClaimantAccountLockedService<Claimant> verifyClaimantAccountLockedService,
      VerifyClaimantAccountPasswordService<Claimant, String> verifyClaimantAccountPasswordService) {
    this.claimantService = claimantService;
    this.verifyClaimantAccountLockedService = verifyClaimantAccountLockedService;
    this.verifyClaimantAccountPasswordService = verifyClaimantAccountPasswordService;
  }

  @Override
  public ResponseEntity<AccountReturn> do1FAuthentication(ValidEmailPasswordRequest request) {
    final Optional<Claimant> optional = claimantService.findAccountBy(request.getEmail());
    if (optional.isEmpty()) {
      throw new AccountAuthFailException("Authentication Failed");
    }
    var citizen = optional.get();
    log.info("Handle 1st factor - an account found");
    verifyClaimantAccountLockedService.verifyClaimantAccountLocked(citizen);
    verifyClaimantAccountPasswordService.verifyClaimantAccountPassword(
        citizen, request.getPassword());
    log.info("Handle 1st factor - verification completed");
    var ref = new AccountReturn();
    ref.setRef(citizen.getId());
    return ResponseEntity.ok().body(ref);
  }
}
