package uk.gov.dwp.health.account.manager.api.v4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.V4AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V4NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V4UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.v4.api.V4Api;
import uk.gov.dwp.health.account.manager.service.V4AccountManagerServices;

import java.util.List;

@Slf4j
@Controller
public class V4AccountManagerApiImpl implements V4Api {

  private final V4AccountManagerServices services;

  public V4AccountManagerApiImpl(V4AccountManagerServices services) {
    this.services = services;
  }

  @Override
  public ResponseEntity<List<V4AccountDetails>> accountDetailsByAccountNumber(String accountId) {
    return services.getClaimantDetailsV4().getAccountDetailsByRef(accountId);
  }

  @Override
  public ResponseEntity<List<V4AccountDetails>> accountDetailsByEmail(String email) {
    return services.getClaimantDetailsV4().getAccountDetailsByEmail(email);
  }

  @Override
  public ResponseEntity<List<V4AccountDetails>> accountDetailsByNino(String nino) {
    return services.getClaimantDetailsV4().getAccountDetailsByNino(nino);
  }

  @Override
  public ResponseEntity<AccountReturn> createAccount(V4NewAccountRequest request) {
    return services.getAccountCreate().doCreateAccount(request);
  }

  @Override
  public ResponseEntity<V4AccountDetails> updateClaimantDetails(V4UpdateClaimantDetailsRequest req
  ) {
    return services.getUpdateClaimantDetails().updateClaimantDetails(req);
  }
}
