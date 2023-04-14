package uk.gov.dwp.health.account.manager.service;

@FunctionalInterface
public interface AccountIdentification<T, R> {
  R doIdentification(T request);
}
