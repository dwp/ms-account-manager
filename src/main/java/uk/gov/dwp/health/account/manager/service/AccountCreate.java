package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface AccountCreate<T, R> {
  R doCreateAccount(T request);
}
