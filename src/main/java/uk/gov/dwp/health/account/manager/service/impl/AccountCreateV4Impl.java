package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.entity.ResearchContact;
import uk.gov.dwp.health.account.manager.entity.UserJourney;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.V4NewAccountRequest;
import uk.gov.dwp.health.account.manager.service.AccountCreate;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.utils.InputValidator;
import uk.gov.dwp.regex.PostCodeValidator;

import java.time.LocalDate;

@Slf4j
public class AccountCreateV4Impl
    implements AccountCreate<V4NewAccountRequest, ResponseEntity<AccountReturn>> {

  private final ClaimantService claimantService;

  public AccountCreateV4Impl(
      ClaimantService claimantService) {
    this.claimantService = claimantService;
  }

  @Override
  public ResponseEntity<AccountReturn> doCreateAccount(V4NewAccountRequest request) {
    var dob = request.getDob();
    isDobValid(dob);
    var postcode = request.getPostcode();
    isPostcodeValid(postcode);
    var email = InputValidator.normaliseInputLower(request.getEmail());
    isEmailInUse(email);
    var nino = InputValidator.normaliseInputUpper(request.getNino());
    isNinoInUse(nino);

    var claimant =
        Claimant.builder()
            .emailAddress(email)
            .enableCap(false)
            .surname(request.getSurname())
            .forename(request.getForename())
            .postcode(request.getPostcode())
            .nino(nino)
            .mobileNumber(request.getMobilePhone())
            .dateOfBirth(dob)
            .researchContact(ResearchContact.valueOf(request.getResearchContact()
                .toString()))
            .language(request.getLanguage().name())
            .userJourney(UserJourney.valueOf(request.getUserJourney().toString()))
            .build();
    claimant.setRegion(claimant.getPostcode().startsWith("BT") ? Region.NI : Region.GB);
    claimantService.updateClaimant(claimant);
    var saved = claimantService.findByEmail(email);
    var accountReturn = new AccountReturn();
    accountReturn.setRef(saved.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(accountReturn);
  }

  private void isDobValid(LocalDate dob) {
    if (dob.isAfter(LocalDate.now())) {
      log.info("Handle create new account failed - DOB is a future date");
      throw new DataValidationException("Fail validation DOB is a future date");
    }
  }

  private void isPostcodeValid(String postcode) {
    if (!PostCodeValidator.validateInput(postcode)) {
      log.info("Handle create new account failed - Postcode invalid {}", postcode);
      throw new DataValidationException("Postcode is invalid");
    }
  }

  private void isNinoInUse(String nino) {
    if (!claimantService.findByNino(nino).isEmpty()) {
      log.info("Handle create new account failed - NINO already in use");
      throw new AccountExistException("NINO");
    }
  }

  private void isEmailInUse(String email) {
    if (claimantService.findByEmail(email) != null) {
      log.info("Handle create new account failed - Email already in use");
      throw new AccountExistException("EMAIL");
    }
  }
}
