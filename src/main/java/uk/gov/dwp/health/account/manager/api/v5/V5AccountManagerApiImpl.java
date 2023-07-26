package uk.gov.dwp.health.account.manager.api.v5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;
import uk.gov.dwp.health.account.manager.openapi.v5.api.V5Api;
import uk.gov.dwp.health.account.manager.service.V5AccountManagerServices;

import java.util.List;

import static uk.gov.dwp.health.account.manager.constant.Strings.CORRELATION_ID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class V5AccountManagerApiImpl implements V5Api {

  private final V5AccountManagerServices services;

  @Override
  public ResponseEntity<List<CheckClaimResponse>> v5CanApplyNinoGet(
      final String xDwpCorrelationId, final String nino) {
    MDC.put(CORRELATION_ID, xDwpCorrelationId);
    final List<CheckClaimResponse> accountDetailsByNino =
        services.getAccountCheckCanApplyV5().checkCanApply(nino);
    return ResponseEntity.ok(accountDetailsByNino);
  }

}
