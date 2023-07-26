package uk.gov.dwp.health.account.manager.service;

public interface AccountCheckCanApply<T> {

  T checkCanApply(String nino);

}
