package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.AccountCreate;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.regex.PostCodeValidator;

import java.time.LocalDate;

@Slf4j
public class AccountCreateImpl
    implements AccountCreate<NewAccountRequest, ResponseEntity<AccountReturn>> {

  private final ClaimantService claimantService;

  public AccountCreateImpl(final ClaimantService claimantService) {
    this.claimantService = claimantService;
  }

  @Override
  public ResponseEntity<AccountReturn> doCreateAccount(NewAccountRequest request) {
    if (!claimantService.findByNino(request.getNino()).isEmpty()) {
      log.info("Handle create new account failed - NINO already in use");
      throw new AccountExistException("NINO");
    }
    if (claimantService.findByEmail(request.getEmail()) != null) {
      log.info("Handle create new account failed - Email already in use");
      throw new AccountExistException("EMAIL");
    }
    if (request.getDob().isAfter(LocalDate.now())) {
      log.info("Handle create new account failed - DOB is a future date");
      throw new DataValidationException("Fail validation DOB is a future date");
    }
    if (!PostCodeValidator.validateInput(request.getPostcode())) {
      log.info("Handle create new account failed - Postcode invalid {}", request.getPostcode());
      throw new DataValidationException("Postcode is invalid");
    }
    final String objectId = claimantService.createAccount(request);
    AccountReturn ref = new AccountReturn();
    ref.setRef(objectId);
    log.info("Handle create new account successful retuning");
    return ResponseEntity.status(HttpStatus.CREATED).body(ref);
  }
}
