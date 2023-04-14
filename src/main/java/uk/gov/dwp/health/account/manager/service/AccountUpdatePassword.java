package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface AccountUpdatePassword<T, R> {
  R updatePassword(T request);
}
