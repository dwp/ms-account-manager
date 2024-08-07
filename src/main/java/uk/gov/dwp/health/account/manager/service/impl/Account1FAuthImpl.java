package uk.gov.dwp.health.account.manager.service.impl;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.service.Account1FAuth;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.TotpRequestService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountLockedService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountPasswordService;

@Slf4j
public class Account1FAuthImpl
    implements Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> {

  private final ClaimantService claimantService;
  private final TotpRequestService totpRequestService;
  private final VerifyClaimantAccountLockedService<Claimant> verifyClaimantAccountLockedService;
  private final VerifyClaimantAccountPasswordService<Claimant, String>
      verifyClaimantAccountPasswordService;

  public Account1FAuthImpl(
      ClaimantService claimantService,
      TotpRequestService totpClientService,
      VerifyClaimantAccountLockedService<Claimant> verifyClaimantAccountLockedService,
      VerifyClaimantAccountPasswordService<Claimant, String> verifyClaimantAccountPasswordService) {
    this.claimantService = claimantService;
    this.totpRequestService = totpClientService;
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
    totpRequestService.requestNewTotp(citizen, COMM.MOBILE);
    var ref = new AccountReturn();
    ref.setRef(citizen.getId());
    log.info("Handle 1st factor totp sent via mobile, op successful returning");
    return ResponseEntity.ok().body(ref);
  }
}
