package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface Account2FAuth<T, R> {

  R do2FAuthentication(T request);
}
