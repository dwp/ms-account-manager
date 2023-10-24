package uk.gov.dwp.health.account.manager.api.v6;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;
import uk.gov.dwp.health.account.manager.openapi.v6.api.V6Api;
import uk.gov.dwp.health.account.manager.service.V6AccountManagerServices;

import java.util.List;

import static uk.gov.dwp.health.account.manager.constant.Strings.CORRELATION_ID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class V6AccountManagerApiImpl implements V6Api {

  private final V6AccountManagerServices v6AccountManagerServices;

  @Override
  public ResponseEntity<List<CheckClaimResponse>> v6CanApplyNinoGet(
      final String dwpCorrelationId, final String nino, final Boolean checkPipApply) {
    MDC.put(CORRELATION_ID, dwpCorrelationId);
    final List<CheckClaimResponse> accountDetailsByNino =
        v6AccountManagerServices.getAccountCheckCanApplyV6().checkCanApply(nino, checkPipApply);
    return ResponseEntity.ok(accountDetailsByNino);
  }
}
