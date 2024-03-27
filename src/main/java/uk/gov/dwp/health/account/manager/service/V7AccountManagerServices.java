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
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV7Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV7Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantPhoneNumbersV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV7Impl;

@Service
@AllArgsConstructor
@Getter
public class V7AccountManagerServices {

  private AccountCreateV7Impl accountCreate;
  private AccountGetClaimantDetailsV7Impl claimantDetailsV7;
  private AccountGetClaimantPhoneNumbersV3Impl claimantPhoneNumbersV3;
  private AccountUpdatePassword<PasswordSetResetRequest, ResponseEntity<Void>> updatePassword;
  private AccountIdentification<IdRequest,
      ResponseEntity<IdentificationResponse>> accountIdentification;
  private Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> account1FAuth;
  private Account2FAuth<ValidTotpRequest, ResponseEntity<Void>> account2FAuth;
  private AccountUpdateClaimantDetailsV7Impl updateClaimantDetails;
}
