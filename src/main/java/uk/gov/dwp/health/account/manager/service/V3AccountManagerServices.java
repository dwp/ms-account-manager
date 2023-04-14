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
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantPhoneNumbersV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsImpl;

@Service
@AllArgsConstructor
@Getter
public class V3AccountManagerServices {

  private AccountCreateV3Impl accountCreate;
  private AccountGetClaimantDetailsV3Impl claimantDetailsV3;
  private AccountGetClaimantPhoneNumbersV3Impl claimantPhoneNumbersV3;
  private AccountUpdatePassword<PasswordSetResetRequest, ResponseEntity<Void>> updatePassword;
  private AccountIdentification<IdRequest,
      ResponseEntity<IdentificationResponse>> accountIdentification;
  private Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> account1FAuth;
  private Account2FAuth<ValidTotpRequest, ResponseEntity<Void>> account2FAuth;
  private AccountUpdateClaimantDetailsImpl updateClaimantDetails;
}
