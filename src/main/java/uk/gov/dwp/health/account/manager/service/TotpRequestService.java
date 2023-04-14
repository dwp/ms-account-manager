package uk.gov.dwp.health.account.manager.service;

import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;

public interface TotpRequestService {
  void requestNewTotp(Claimant citizen, COMM contactMethod);
}
