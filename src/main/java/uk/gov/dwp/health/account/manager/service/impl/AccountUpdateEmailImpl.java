package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountUpdateEmail;
import uk.gov.dwp.health.account.manager.service.AccountUpdateEmailAbstract;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

@Slf4j
public class AccountUpdateEmailImpl extends AccountUpdateEmailAbstract
    implements AccountUpdateEmail<UpdateEmailRequest, ResponseEntity<AccountDetails>> {

  private final AccountDataMapper dataMapper;

  public AccountUpdateEmailImpl(ClaimantService claimantService, AccountDataMapper dataMapper) {
    super(claimantService);
    this.dataMapper = dataMapper;
  }

  @Override
  public ResponseEntity<AccountDetails> updateEmail(UpdateEmailRequest request) {
    var claimant = updateEmail(request.getNewEmail(), request.getCurrentEmail());
    return ResponseEntity.accepted()
        .body(dataMapper.mapToAccountDetails(claimant));
  }

  public void validateEmail(UpdateEmailRequest request) {
    validateEmail(request.getNewEmail(), request.getCurrentEmail());
  }
}
