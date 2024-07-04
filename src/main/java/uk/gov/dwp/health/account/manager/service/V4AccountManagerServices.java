package uk.gov.dwp.health.account.manager.service;

import jakarta.annotation.Resource;
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
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantPhoneNumbersV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV4Impl;

@Service
@AllArgsConstructor
@Getter
public class V4AccountManagerServices {
  private AccountCreateV4Impl accountCreate;
  private AccountGetClaimantDetailsV4Impl claimantDetailsV4;
  private AccountGetClaimantPhoneNumbersV3Impl claimantPhoneNumbersV3;
  private AccountUpdatePassword<PasswordSetResetRequest, ResponseEntity<Void>> updatePassword;
  private AccountIdentification<IdRequest,
      ResponseEntity<IdentificationResponse>> accountIdentification;
  @Resource(name = "account1FAuthV2")
  private Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> account1FAuth;
  private Account2FAuth<ValidTotpRequest, ResponseEntity<Void>> account2FAuth;
  private AccountUpdateClaimantDetailsV4Impl updateClaimantDetails;
}
