package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.http.ResponseEntity;

import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateNinoRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountDetails;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountUpdateClaimantDetails;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

public class AccountUpdateClaimantDetailsImpl implements AccountUpdateClaimantDetails
    <UpdateClaimantDetailsRequest, ResponseEntity<V3AccountDetails>> {

  private final ClaimantService claimantSvc;
  private final AccountDataMapper dataMapper;
  private final AccountUpdateEmailImpl updateEmailImpl;
  private final AccountUpdateNinoImpl updateNinoImpl;

  public AccountUpdateClaimantDetailsImpl(ClaimantService claimantSvc,
      AccountDataMapper dataMapper,
      AccountUpdateEmailImpl updateEmailImpl,
      AccountUpdateNinoImpl updateNinoImpl) {
    this.claimantSvc = claimantSvc;
    this.dataMapper = dataMapper;
    this.updateEmailImpl = updateEmailImpl;
    this.updateNinoImpl = updateNinoImpl;
  }

  @Override
  public ResponseEntity<V3AccountDetails> 
      updateClaimantDetails(UpdateClaimantDetailsRequest request) {    
    
    var currentEmail = request.getCurrentEmail();
    var newEmail = request.getNewEmail();
    if (!currentEmail.equals(newEmail)) {
      var updateEmailRequest = new UpdateEmailRequest();
      updateEmailRequest.setCurrentEmail(currentEmail);
      updateEmailRequest.setNewEmail(newEmail);
      updateEmailImpl.validateEmail(updateEmailRequest);
    }

    var currentNino = request.getCurrentNino();
    var newNino = request.getNewNino();
    if (!currentNino.equals(newNino)) {
      var updateNinoRequest = new UpdateNinoRequest();
      updateNinoRequest.setCurrentNino(currentNino);
      updateNinoRequest.setNewNino(newNino);
      updateNinoImpl.validateNino(updateNinoRequest);
    }

    Claimant claimant = claimantSvc.findByRef(request.getRef());

    claimant.setEmailAddress(newEmail);
    claimant.setNino(newNino);
    claimant.setForename(request.getForename());
    claimant.setSurname(request.getSurname());
    claimant.setDateOfBirth(request.getDob());
    claimant.setMobileNumber(request.getMobilePhone());
    claimant.setPostcode(request.getPostcode());

    claimantSvc.updateClaimant(claimant);
    return ResponseEntity.accepted().body(dataMapper.mapToV3AccountDetails(claimant));
  }
}