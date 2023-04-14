package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.service.AccountIdentification;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.TotpRequestService;

import java.util.Optional;

@Slf4j
public class AccountIdentificationImpl
    implements AccountIdentification<IdRequest, ResponseEntity<IdentificationResponse>> {

  private final ClaimantService claimantService;
  private final TotpRequestService totpRequestService;

  public AccountIdentificationImpl(
      ClaimantService claimantService, TotpRequestService totpRequestService) {
    this.claimantService = claimantService;
    this.totpRequestService = totpRequestService;
  }

  @Override
  public ResponseEntity<IdentificationResponse> doIdentification(IdRequest request) {
    final Optional<Claimant> optional = claimantService
        .findAccountBy(request.getEmail(), request.getNino(), request.getDob());
    if (optional.isEmpty()) {
      throw new AccountNotFoundException("User account not found");
    }
    var citizen = optional.get();
    log.info("Handle user identification - an account found");
    var ref = new IdentificationResponse();
    ref.setRef(citizen.getId());
    Optional.ofNullable(citizen.getAuth())
        .ifPresentOrElse(
            auth -> {
              switch (STAGE.fromInt(auth.getStatus())) {
                case FIRST:
                case SECONDPLUS:
                  ref.setStatus(IdentificationResponse.StatusEnum.ACTIVE);
                  break;
                case LOCKED:
                  ref.setStatus(IdentificationResponse.StatusEnum.LOCKED);
                  break;
                default:
                  ref.setStatus(IdentificationResponse.StatusEnum.PENDING);
              }
            },
            () -> ref.setStatus(IdentificationResponse.StatusEnum.PENDING));
    log.info("Handle user identification - requesting totp");
    if (request.getGenerateEmailTotp()) {
      totpRequestService.requestNewTotp(citizen, COMM.EMAIL);
      log.info("Handle user identification -  totp sent via email");
    }
    if (request.getGenerateSmsTotp()) {
      totpRequestService.requestNewTotp(citizen, COMM.MOBILE);
      log.info("Handle user identification -  totp sent via mobile");
    }
    log.info("Handle user identification successful returning");
    return ResponseEntity.ok().body(ref);
  }
}
