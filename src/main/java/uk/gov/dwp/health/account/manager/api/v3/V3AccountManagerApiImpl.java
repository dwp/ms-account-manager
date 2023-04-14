package uk.gov.dwp.health.account.manager.api.v3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.V3AccountMobilePhone;
import uk.gov.dwp.health.account.manager.openapi.model.V3NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.openapi.v3.api.V3Api;
import uk.gov.dwp.health.account.manager.service.V3AccountManagerServices;

import java.util.List;

@Slf4j
@Controller
public class V3AccountManagerApiImpl implements V3Api {

  private final V3AccountManagerServices services;

  public V3AccountManagerApiImpl(V3AccountManagerServices services) {
    this.services = services;
  }

  @Override
  public ResponseEntity<List<V3AccountMobilePhone>> mobilePhoneNumbersByClaimantId(
      String claimantIdCsv
  ) {
    return services.getClaimantPhoneNumbersV3().getMobilePhoneNumbersByClaimantId(claimantIdCsv);
  }

  @Override
  public ResponseEntity<List<V3AccountDetails>> accountDetailsByAccountNumber(String accountId) {
    return services.getClaimantDetailsV3().getAccountDetailsByRef(accountId);
  }

  @Override
  public ResponseEntity<List<V3AccountDetails>> accountDetailsByEmail(String email) {
    return services.getClaimantDetailsV3().getAccountDetailsByEmail(email);
  }

  @Override
  public ResponseEntity<List<V3AccountDetails>> accountDetailsByNino(String nino) {
    return services.getClaimantDetailsV3().getAccountDetailsByNino(nino);
  }

  @Override
  public ResponseEntity<AccountReturn> createAccount(V3NewAccountRequest request) {
    return services.getAccountCreate().doCreateAccount(request);
  }

  @Override
  public ResponseEntity<Void> createUpdatePassword(PasswordSetResetRequest request) {
    return services.getUpdatePassword().updatePassword(request);
  }

  @Override
  public ResponseEntity<IdentificationResponse> identification(IdRequest request) {
    return services.getAccountIdentification().doIdentification(request);
  }

  @Override
  public ResponseEntity<V3AccountDetails> updateClaimantDetails(UpdateClaimantDetailsRequest req) {
    return services.getUpdateClaimantDetails().updateClaimantDetails(req);
  }

  @Override
  public ResponseEntity<Void> valid2fFactor(ValidTotpRequest request) {
    return services.getAccount2FAuth().do2FAuthentication(request);
  }

  @Override
  public ResponseEntity<AccountReturn> validFirstFactor(ValidEmailPasswordRequest request) {
    return services.getAccount1FAuth().do1FAuthentication(request);
  }
}
