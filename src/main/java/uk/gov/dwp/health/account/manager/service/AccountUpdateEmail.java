package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface AccountUpdateEmail<T, R> {
  R updateEmail(T request);
}
