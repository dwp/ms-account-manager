package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface AccountUpdateClaimantDetails<T, R> {
  R updateClaimantDetails(T request);
}