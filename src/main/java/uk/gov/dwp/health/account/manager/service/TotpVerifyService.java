package uk.gov.dwp.health.account.manager.service;

import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;

@FunctionalInterface
public interface TotpVerifyService {
  boolean verify(Claimant claimant, Totp... totps);
}
