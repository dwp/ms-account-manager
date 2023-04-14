package uk.gov.dwp.health.account.manager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateEmailImpl;

@Getter
@Service
@AllArgsConstructor
public class V1AccountManagerServices {

  private AccountCreateImpl accountCreate;
  private AccountGetClaimantDetailsImpl claimantDetails;
  private AccountUpdatePassword<PasswordSetResetRequest, ResponseEntity<Void>> updatePassword;
  private AccountIdentification<IdRequest, ResponseEntity<IdentificationResponse>>
      accountIdentification;
  private AccountUpdateEmailImpl updateEmail;
  private Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> account1FAuth;
  private Account2FAuth<ValidTotpRequest, ResponseEntity<Void>> account2FAuth;
}
