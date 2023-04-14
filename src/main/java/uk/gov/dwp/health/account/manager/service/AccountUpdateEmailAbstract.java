package uk.gov.dwp.health.account.manager.service;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;

@Slf4j
public abstract class AccountUpdateEmailAbstract {

  private final ClaimantService claimantService;

  protected AccountUpdateEmailAbstract(ClaimantService claimantService) {
    this.claimantService = claimantService;
  }

  protected Claimant validateEmail(String newEmail, String currentEmail) {

    Objects.requireNonNull(newEmail, "New email must be a valid email");
    Objects.requireNonNull(currentEmail, "Current email must be a valid email");
    
    if (newEmail.equals(currentEmail)) { 
      throw new DataValidationException("New email must be different from current email");
    }

    var claimant = claimantService.findByEmail(currentEmail);
    if (claimant == null) {
      log.info("Given email did not resolve to an existing account");
      throw new UnauthorizedException("Account does not exist for update");
    }

    if (claimantService.findByEmail(newEmail) != null) {
      log.info("Handle update account email failed - new Email already in use");
      throw new AccountExistException("EMAIL");
    }

    return claimant;
  }

  protected Claimant updateEmail(String newEmail, String currentEmail) {
    var claimant = validateEmail(newEmail, currentEmail);
    claimant.setEmailAddress(newEmail);
    claimantService.updateClaimant(claimant);
    return claimant;
  }
}
