package uk.gov.dwp.health.account.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.entity.UserJourney;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.DataValidationException;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.V5NewAccountRequest;
import uk.gov.dwp.health.account.manager.repository.RegistrationRepository;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.utils.InputValidator;
import uk.gov.dwp.regex.PostCodeValidator;

import java.time.LocalDate;

import static uk.gov.dwp.health.account.manager.openapi.model.V5NewAccountRequest.UserJourneyEnum.STRATEGIC;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountCreateV5Impl {

  private final ClaimantService claimantService;
  private final RegistrationRepository registrationRepository;

  public ResponseEntity<AccountReturn> doCreateAccount(V5NewAccountRequest v5NewAccountRequest) {
    log.info("About to process a v5 account creation");

    var dob = v5NewAccountRequest.getDob();
    isDobValid(dob);
    var postcode = v5NewAccountRequest.getPostcode();
    isPostcodeValid(postcode);
    var formattedEmail = InputValidator.normaliseInputLower(v5NewAccountRequest.getEmail());
    isEmailInUse(formattedEmail);
    var nino = InputValidator.normaliseInputUpper(v5NewAccountRequest.getNino());
    isNinoInUse(nino);

    var accountReturn = createAccount(v5NewAccountRequest, formattedEmail);
    incrementRegistrationCount(v5NewAccountRequest);

    log.info("Processed a v5 account creation");

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

  private AccountReturn createAccount(
      V5NewAccountRequest v5NewAccountRequest, String formattedEmail) {
    log.info("About to create a v5 account");
    Claimant claimant = toModel(v5NewAccountRequest);
    claimantService.updateClaimant(claimant);
    var savedClaimant = claimantService.findByEmail(formattedEmail);
    var accountReturn = new AccountReturn();
    accountReturn.setRef(savedClaimant.getId());
    log.info("Created a v5 account with claimant id of {}", savedClaimant.getId());
    return accountReturn;
  }

  private Claimant toModel(V5NewAccountRequest request) {
    var claimant =
        Claimant.builder()
            .emailAddress(request.getEmail())
            .enableCap(false)
            .surname(request.getSurname())
            .forename(request.getForename())
            .postcode(request.getPostcode())
            .nino(request.getNino())
            .mobileNumber(request.getMobilePhone())
            .dateOfBirth(request.getDob())
            .language(request.getLanguage().name())
            .userJourney(UserJourney.valueOf(request.getUserJourney().toString()))
            .build();
    claimant.setRegion(claimant.getPostcode().startsWith("BT") ? Region.NI : Region.GB);
    return claimant;
  }

  private void incrementRegistrationCount(V5NewAccountRequest v5NewAccountRequest) {
    log.info("About to increment registration count");

    if (v5NewAccountRequest.getUserJourney().equals(STRATEGIC)) {
      registrationRepository.incrementRegistrationCount();
    }

    log.info("Incremented registration count");
  }
}
