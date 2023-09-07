package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.Message;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

@Slf4j
public class AccountUpdateTransferStatusImpl {

  private final ClaimantService claimantSvc;

  public AccountUpdateTransferStatusImpl(ClaimantService claimantSvc) {
    this.claimantSvc = claimantSvc;
  }

  public ResponseEntity<Message> updateTransferStatus(String accountId) {

    Claimant claimant = claimantSvc.findByRef(accountId);
    claimant.setTransferredToDwpApply(Boolean.TRUE);
    claimantSvc.updateClaimant(claimant);
    return ResponseEntity
            .accepted()
            .body(new Message().message("Transfer Status has successfully been updated."));
  }
}
