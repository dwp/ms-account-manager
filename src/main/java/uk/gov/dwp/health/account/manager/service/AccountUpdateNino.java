package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface AccountUpdateNino<T, R> {
  R updateNino(T request);
}
