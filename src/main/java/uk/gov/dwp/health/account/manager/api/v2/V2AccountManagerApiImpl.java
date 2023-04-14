package uk.gov.dwp.health.account.manager.api.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.openapi.v2.api.V2Api;
import uk.gov.dwp.health.account.manager.service.V2AccountManagerServices;

import java.util.List;

@Slf4j
@Controller
public class V2AccountManagerApiImpl implements V2Api {

  private final V2AccountManagerServices v2AccountManagerServices;

  public V2AccountManagerApiImpl(V2AccountManagerServices v2AccountManagerServices) {
    this.v2AccountManagerServices = v2AccountManagerServices;
  }

  @Override
  public ResponseEntity<List<AccountDetails>> accountDetailsByAccountNumber(String accountId) {
    return v2AccountManagerServices.getClaimantDetailsV2().getAccountDetailsByRef(accountId);
  }

  @Override
  public ResponseEntity<List<AccountDetails>> accountDetailsByEmail(String email) {
    return v2AccountManagerServices.getClaimantDetailsV2().getAccountDetailsByEmail(email);
  }

  @Override
  public ResponseEntity<List<AccountDetails>> accountDetailsByNino(String nino) {
    return v2AccountManagerServices.getClaimantDetailsV2().getAccountDetailsByNino(nino);
  }

  @Override
  public ResponseEntity<AccountReturn> createAccount(NewAccountRequest request) {
    return v2AccountManagerServices.getAccountCreate().doCreateAccount(request);
  }

  @Override
  public ResponseEntity<Void> createUpdatePassword(PasswordSetResetRequest request) {
    return v2AccountManagerServices.getUpdatePassword().updatePassword(request);
  }

  @Override
  public ResponseEntity<IdentificationResponse> identification(IdRequest request) {
    return v2AccountManagerServices.getAccountIdentification().doIdentification(request);
  }

  @Override
  public ResponseEntity<AccountDetails> updateEmail(UpdateEmailRequest request) {
    return v2AccountManagerServices.getUpdateEmail().updateEmail(request);
  }

  @Override
  public ResponseEntity<Void> valid2fFactor(ValidTotpRequest request) {
    return v2AccountManagerServices.getAccount2FAuth().do2FAuthentication(request);
  }

  @Override
  public ResponseEntity<AccountReturn> validFirstFactor(ValidEmailPasswordRequest request) {
    return v2AccountManagerServices.getAccount1FAuth().do1FAuthentication(request);
  }
}
