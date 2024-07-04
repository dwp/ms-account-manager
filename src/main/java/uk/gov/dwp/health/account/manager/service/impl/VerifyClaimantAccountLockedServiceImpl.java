package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountLockedException;
import uk.gov.dwp.health.account.manager.exception.AccountSetupException;
import uk.gov.dwp.health.account.manager.service.VerifyClaimantAccountLockedService;

@Slf4j
public class VerifyClaimantAccountLockedServiceImpl
    implements VerifyClaimantAccountLockedService<Claimant> {
  @Override
  public void verifyClaimantAccountLocked(Claimant claimant) {
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
}
