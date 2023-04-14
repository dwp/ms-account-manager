package uk.gov.dwp.health.account.manager.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.gov.dwp.health.account.manager.openapi.api.V1Api;
import uk.gov.dwp.health.account.manager.openapi.model.AccountDetails;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.NinoDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.V1AccountManagerServices;

import java.util.List;

@Slf4j
@Controller
public class V1AccountManagerApiImpl implements V1Api {

  private final V1AccountManagerServices v1Services;

  public V1AccountManagerApiImpl(V1AccountManagerServices v1Services) {
    this.v1Services = v1Services;
  }

  @Override
  public ResponseEntity<AccountDetails> accountDetailsByAccountNumber(String ref) {
    return v1Services.getClaimantDetails().getAccountDetailsByRef(ref);
  }

  @Override
  public ResponseEntity<List<AccountDetails>> accountDetailsByNino(
      NinoDetailsRequest ninoDetailsRequest) {
    return v1Services.getClaimantDetails().getAccountDetailsByNino(ninoDetailsRequest.getNino());
  }

  @Override
  public ResponseEntity<AccountReturn> createAccount(NewAccountRequest request) {
    return v1Services.getAccountCreate().doCreateAccount(request);
  }

  @Override
  public ResponseEntity<Void> createUpdatePassword(PasswordSetResetRequest request) {
    return v1Services.getUpdatePassword().updatePassword(request);
  }

  @Override
  public ResponseEntity<IdentificationResponse> identification(IdRequest request) {
    return v1Services.getAccountIdentification().doIdentification(request);
  }

  @Override
  public ResponseEntity<AccountDetails> updateEmail(UpdateEmailRequest updateEmailRequest) {
    return v1Services.getUpdateEmail().updateEmail(updateEmailRequest);
  }

  @Override
  public ResponseEntity<Void> valid2fFactor(ValidTotpRequest body) {
    return v1Services.getAccount2FAuth().do2FAuthentication(body);
  }

  @Override
  public ResponseEntity<AccountReturn> validFirstFactor(ValidEmailPasswordRequest body) {
    return v1Services.getAccount1FAuth().do1FAuthentication(body);
  }
}
