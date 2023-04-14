package uk.gov.dwp.health.account.manager.service;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.exception.UnauthorizedException;

import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class AccountUpdateNinoAbstract {

  private final ClaimantService claimantService;

  protected AccountUpdateNinoAbstract(ClaimantService claimantService) {
    this.claimantService = claimantService;
  }

  protected Claimant validateNino(String newNino, String currentNino) {
    Objects.requireNonNull(newNino, "New NINO must be a valid NINO");
    Objects.requireNonNull(currentNino, "Current NINO must be a valid NINO");

    if (newNino.equals(currentNino)) {
      throw new DataValidationException("New NINO must be different from current NINO");
    }

    final List<Claimant> claimants = claimantService.findByNino(currentNino);
    if (claimants.isEmpty()) {
      log.info("Given NINO did not resolve to an existing account");
      throw new UnauthorizedException("Account does not exist for update");
    }
    var claimant = claimants.get(0);

    if (!claimantService.findByNino(newNino).isEmpty()) {
      log.info("Handle update account NINO failed - new NINO already in use");
      throw new AccountExistException("NINO");
    }

    return claimant;
  }

  protected Claimant updateNino(String newNino, String currentNino) {
    var claimant = validateNino(newNino, currentNino);
    claimant.setNino(newNino);
    claimantService.updateClaimant(claimant);
    return claimant;
  }
}
