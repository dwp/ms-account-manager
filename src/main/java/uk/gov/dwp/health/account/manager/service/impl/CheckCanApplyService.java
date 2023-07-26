package uk.gov.dwp.health.account.manager.service.impl;

import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;

import java.util.List;

public interface CheckCanApplyService {

  List<CheckClaimResponse> checkCanApply(final String nino);

}
