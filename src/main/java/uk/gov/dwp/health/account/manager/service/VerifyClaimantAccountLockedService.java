package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface VerifyClaimantAccountLockedService<T> {
  void verifyClaimantAccountLocked(T claimant);
}
