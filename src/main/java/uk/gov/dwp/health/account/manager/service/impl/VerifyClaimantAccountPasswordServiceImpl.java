package uk.gov.dwp.health.account.manager.service.impl;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.FailedAttempt;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.SecureHashService;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountPasswordService;

@Slf4j
public class VerifyClaimantAccountPasswordServiceImpl
    implements VerifyClaimantAccountPasswordService<Claimant, String> {

  final int maxAllowedFailure;
  private final ClaimantService claimantService;
  private final SecureHashService<String, String> secureHashService;

  public VerifyClaimantAccountPasswordServiceImpl(
      ClaimantService claimantService,
      SecureHashService<String, String> secureHashService,
      int maxAllowedFailure) {
    this.claimantService = claimantService;
    this.secureHashService = secureHashService;
    this.maxAllowedFailure = maxAllowedFailure;
  }

  @Override
  public void verifyClaimantAccountPassword(Claimant claimant, String usrPswInput) {
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
