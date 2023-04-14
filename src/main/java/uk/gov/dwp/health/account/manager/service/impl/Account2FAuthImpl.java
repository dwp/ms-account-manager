package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountAuthFailException;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.Account2FAuth;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.TotpRequestService;
import uk.gov.dwp.health.account.manager.service.TotpVerifyService;

@Slf4j
public class Account2FAuthImpl extends AccountDataMapper
    implements Account2FAuth<ValidTotpRequest, ResponseEntity<Void>> {

  private final ClaimantService claimantService;
  private final TotpRequestService totpRequestService;
  private final TotpVerifyService totpVerifyService;

  public Account2FAuthImpl(
      ClaimantService claimantService,
      TotpRequestService totpRequestService,
      TotpVerifyService totpVerifyService) {
    this.claimantService = claimantService;
    this.totpRequestService = totpRequestService;
    this.totpVerifyService = totpVerifyService;
  }

  @Override
  public ResponseEntity<Void> do2FAuthentication(ValidTotpRequest request) {
    Claimant claimant = claimantService.findByRef(request.getRef());
    log.info("Handle 2nd factor - an account found");
    if (totpVerifyService.verify(claimant, request.getTotp())) {
      log.info("Handle 2nd factor - totp verified");
      if (request.getGenerateEmailTotp()) {
        totpRequestService.requestNewTotp(claimant, COMM.EMAIL);
        log.info("Handle 2nd factor - totp sent via email");
      }
      if (request.getGenerateSmsTotp()) {
        totpRequestService.requestNewTotp(claimant, COMM.MOBILE);
        log.info("Handle 2nd factor - totp sent via mobile");
      }
      log.info("Handle 2nd factor successful and returning");
      return ResponseEntity.status(HttpStatus.OK).build();
    } else {
      log.info("Handle 2nd factor verify totp not matching");
      throw new AccountAuthFailException("Totp Authentication Failed");
    }
  }
}
