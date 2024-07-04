package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface VerifyClaimantAccountPasswordService<T, R> {
  void verifyClaimantAccountPassword(T claimant, R password);
}
