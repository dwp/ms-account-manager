package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountUpdatePassword;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.TotpVerifyService;

@Slf4j
public class AccountUpdatePasswordImpl extends AccountDataMapper
    implements AccountUpdatePassword<PasswordSetResetRequest, ResponseEntity<Void>> {

  private final ClaimantService claimantService;
  private final TotpVerifyService totpVerifyService;

  public AccountUpdatePasswordImpl(
      ClaimantService claimantService, TotpVerifyService totpVerifyService) {
    this.claimantService = claimantService;
    this.totpVerifyService = totpVerifyService;
  }

  @Override
  public ResponseEntity<Void> updatePassword(PasswordSetResetRequest request) {
    final Claimant claimant = claimantService.findByRef(request.getRef());
    final boolean verified =
        totpVerifyService.verify(claimant, request.getTotp().toArray(new Totp[0]));
    if (verified) {
      log.info("Handle set/reset password - totp match expectation");
      claimantService.setPassword(request.getRef(), request.getPassword());
      if (claimant.getAuth() == null) {
        log.info("Handle create password successful");
        return ResponseEntity.status(HttpStatus.CREATED).build();
      } else {
        log.info("Handle reset password successful");
        return ResponseEntity.status(HttpStatus.OK).build();
      }
    } else {
      log.info("Handle set/reset password - totp not match expectation");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
