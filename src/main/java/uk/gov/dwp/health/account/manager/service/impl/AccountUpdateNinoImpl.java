package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateNinoRequest;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountUpdateNino;
import uk.gov.dwp.health.account.manager.service.AccountUpdateNinoAbstract;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

@Slf4j
public class AccountUpdateNinoImpl extends AccountUpdateNinoAbstract
    implements AccountUpdateNino<UpdateNinoRequest, ResponseEntity<AccountDetails>> {

  private final AccountDataMapper dataMapper;

  public AccountUpdateNinoImpl(ClaimantService claimantService, AccountDataMapper dataMapper) {
    super(claimantService);
    this.dataMapper = dataMapper;
  }

  @Override
  public ResponseEntity<AccountDetails> updateNino(UpdateNinoRequest request) {
    var claimant = updateNino(request.getNewNino(), request.getCurrentNino());
    return ResponseEntity.accepted()
        .body(dataMapper.mapToAccountDetails(claimant));
  }

  public void validateNino(UpdateNinoRequest request) {
    validateNino(request.getNewNino(), request.getCurrentNino());
  }
}
