package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.FailedAttempt;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.exception.AccountSetupException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.service.Account1FAuth;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.SecureHashService;
import uk.gov.dwp.health.account.manager.service.TotpRequestService;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class Account1FAuthImpl
    implements Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> {

  private final ClaimantService claimantService;
  private final TotpRequestService totpRequestService;
  private final SecureHashService<String, String> secureHashService;
  private final int maxAllowedFailure;

  public Account1FAuthImpl(
      ClaimantService claimantService,
      TotpRequestService totpClientService,
      SecureHashService<String, String> secureHashService,
      int maxAllowedFailure) {
    this.claimantService = claimantService;
    this.totpRequestService = totpClientService;
    this.secureHashService = secureHashService;
    this.maxAllowedFailure = maxAllowedFailure;
  }

  @Override
  public ResponseEntity<AccountReturn> do1FAuthentication(ValidEmailPasswordRequest request) {
    final Optional<Claimant> optional = claimantService.findAccountBy(request.getEmail());
    if (optional.isEmpty()) {
      throw new AccountAuthFailException("Authentication Failed");
    }
    var citizen = optional.get();
    log.info("Handle 1st factor - an account found");
    verifyAccountLocked(citizen);
    verifyPassword(citizen, request.getPassword());
    log.info("Handle 1st factor - verification completed");
    totpRequestService.requestNewTotp(citizen, COMM.MOBILE);
    var ref = new AccountReturn();
    ref.setRef(citizen.getId());
    log.info("Handle 1st factor totp sent via mobile, op successful returning");
    return ResponseEntity.ok().body(ref);
  }

  private void verifyAccountLocked(Claimant claimant) {
    if (claimant.getAuth() != null) {
      if (claimant.getAuth().getStatus() == STAGE.LOCKED.current()) {
        log.debug("Verify account status - Locked");
        throw new AccountLockedException("Account locked");
      }
    } else {
      log.debug("Verify account status - inactive");
      throw new AccountSetupException("Account require activation");
    }
  }

  private void verifyPassword(Claimant claimant, String usrPswInput) {
    if (!claimant.getAuth().getPassword().equals(secureHashService.hash(usrPswInput))) {
      claimant
          .getAuth()
          .addFailure(FailedAttempt.builder().stage("1F").timestamp(LocalDateTime.now()).build());
      final int failureCounter = claimant.getAuth().getFailureCounter();
      if (failureCounter > maxAllowedFailure) {
        claimant.getAuth().setStatus(STAGE.LOCKED.current());
        claimantService.updateClaimant(claimant);
        log.info("Verify password exceed max allowed - account locked and updated");
        throw new AccountLockedException("Account locked");
      } else {
        claimantService.updateClaimant(claimant);
        log.info("Verify password failed - account updated");
        throw new AccountAuthFailException("Account authentication failed");
      }
    } else {
      claimant.getAuth().setFailureCounter(0);
      claimantService.updateClaimant(claimant);
      log.info("Verify password successful - counter reset successful");
    }
  }
}
