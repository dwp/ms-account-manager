package uk.gov.dwp.health.account.manager.api.v7;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.V4AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V4NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V4UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V7AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V7NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.v7.api.V7Api;
import uk.gov.dwp.health.account.manager.service.V7AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV7Impl;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class V7AccountManagerApiImpl implements V7Api {

  private final V7AccountManagerServices services;

  public ResponseEntity<List<V7AccountDetails>> accountDetailsByAccountNumber(String accountId) {
    return services.getClaimantDetailsV7().getAccountDetailsByRef(accountId);
  }

  public ResponseEntity<AccountReturn> createAccount(V7NewAccountRequest v7NewAccountRequest) {
    return services.getAccountCreate().doCreateAccount(v7NewAccountRequest);
  }

  @Override
  public ResponseEntity<List<V7AccountDetails>> accountDetailsByEmail(String email) {
    return services.getClaimantDetailsV7().getAccountDetailsByEmail(email);
  }

  @Override
  public ResponseEntity<List<V7AccountDetails>> accountDetailsByNino(String nino) {
    return services.getClaimantDetailsV7().getAccountDetailsByNino(nino);
  }

  @Override
  public ResponseEntity<V7AccountDetails> updateClaimantDetails(
      V4UpdateClaimantDetailsRequest v4UpdateClaimantDetailsRequest
  ) {
    final AccountUpdateClaimantDetailsV7Impl details = services.getUpdateClaimantDetails();
    return details.updateClaimantDetails(v4UpdateClaimantDetailsRequest);
  }

}
