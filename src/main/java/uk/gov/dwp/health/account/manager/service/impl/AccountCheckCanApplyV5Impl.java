package uk.gov.dwp.health.account.manager.service.impl;

import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;
import uk.gov.dwp.health.account.manager.service.AccountCheckCanApply;

import java.util.List;

public class AccountCheckCanApplyV5Impl implements AccountCheckCanApply<List<CheckClaimResponse>> {

  private final CheckCanApplyService service;

  public AccountCheckCanApplyV5Impl(final CheckCanApplyService service) {
    this.service = service;
  }

  @Override
  public List<CheckClaimResponse> checkCanApply(final String nino) {
    return service.checkCanApply(nino);
  }

}
