package uk.gov.dwp.health.account.manager.service;

public interface AccountGetClaimantDetails<T> {

  T getAccountDetailsByEmail(String email);

  T getAccountDetailsByNino(String nino);

  T getAccountDetailsByRef(String ref);

}
