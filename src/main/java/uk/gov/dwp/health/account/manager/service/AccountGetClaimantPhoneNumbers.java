package uk.gov.dwp.health.account.manager.service;

public interface AccountGetClaimantPhoneNumbers<T> {

  T getMobilePhoneNumbersByClaimantId(String claimantIdCsv);

}
