package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface Account1FAuth<T, R> {
  R do1FAuthentication(T request);
}
