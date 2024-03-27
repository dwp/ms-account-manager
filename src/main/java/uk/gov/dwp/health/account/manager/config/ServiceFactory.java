package uk.gov.dwp.health.account.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.health.account.manager.openapi.model.AccountReturn;
import uk.gov.dwp.health.account.manager.openapi.model.IdRequest;
import uk.gov.dwp.health.account.manager.openapi.model.IdentificationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.PasswordSetResetRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidEmailPasswordRequest;
import uk.gov.dwp.health.account.manager.openapi.model.ValidTotpRequest;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.Account1FAuth;
import uk.gov.dwp.health.account.manager.service.Account2FAuth;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.AccountIdentification;
import uk.gov.dwp.health.account.manager.service.AccountUpdatePassword;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.service.SecureHashService;
import uk.gov.dwp.health.account.manager.service.TotpRequestService;
import uk.gov.dwp.health.account.manager.service.TotpVerifyService;
import uk.gov.dwp.health.account.manager.service.impl.Account1FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.Account2FAuthImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCheckCanApplyV5Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountCreateV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV2Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantDetailsV7Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountGetClaimantPhoneNumbersV3Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountIdentificationImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV4Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateClaimantDetailsV7Impl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateEmailImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateNinoImpl;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdatePasswordImpl;
import uk.gov.dwp.health.account.manager.service.impl.CheckCanApplyService;
import uk.gov.dwp.health.account.manager.service.impl.AccountUpdateTransferStatusImpl;


@Slf4j
@Configuration
public class ServiceFactory {

  private final ClaimantService<NewAccountRequest> claimantService;
  private final TotpVerifyService totpVerifyService;
  private final TotpRequestService totpRequestService;
  private final SecureHashService<String, String> secureHashService;
  private final ClaimantRepository claimantRepository;
  private final AccountDataMapper dataMapper;
  private final CheckCanApplyService checkCanApplyService;

  @Value("${account.manager.allow-failure:4}")
  private int maxAllowedFailure;

  public ServiceFactory(
      ClaimantService<NewAccountRequest> claimantService,
      TotpVerifyService totpVerifyService,
      TotpRequestService totpRequestService,
      SecureHashService<String, String> secureHashService,
      ClaimantRepository claimantRepository,
      @Qualifier("accountDataMapper") AccountDataMapper dataMapper,
      CheckCanApplyService checkCanApplyService) {
    this.claimantService = claimantService;
    this.totpVerifyService = totpVerifyService;
    this.totpRequestService = totpRequestService;
    this.secureHashService = secureHashService;
    this.claimantRepository = claimantRepository;
    this.dataMapper = dataMapper;
    this.checkCanApplyService = checkCanApplyService;
  }

  @Bean
  public AccountCreateImpl accountCreate() {
    log.info("Creating AccountCreate bean instance");
    return new AccountCreateImpl(claimantService);
  }

  @Bean
  public AccountCreateV3Impl accountCreateV3() {
    log.info("Creating AccountCreateV3 bean instance");
    return new AccountCreateV3Impl(claimantService);
  }

  @Bean
  public AccountCreateV4Impl accountCreateV4() {
    log.info("Creating AccountCreateV4 bean instance");
    return new AccountCreateV4Impl(claimantService);
  }

  @Bean
  public Account1FAuth<ValidEmailPasswordRequest, ResponseEntity<AccountReturn>> account1FAuth() {
    log.info("Creating Account1FAuth bean instance");
    return new Account1FAuthImpl(
        claimantService, totpRequestService, secureHashService, maxAllowedFailure);
  }

  @Bean
  public Account2FAuth<ValidTotpRequest, ResponseEntity<Void>> account2FAuth() {
    log.info("Creating Account2FAuth bean instance");
    return new Account2FAuthImpl(claimantService, totpRequestService, totpVerifyService);
  }

  @Bean
  public AccountUpdatePassword<PasswordSetResetRequest, ResponseEntity<Void>>
      accountUpdatePassword() {
    log.info("Creating AccountUpdatePassword bean instance");
    return new AccountUpdatePasswordImpl(claimantService, totpVerifyService);
  }

  @Bean
  public AccountIdentification<IdRequest, ResponseEntity<IdentificationResponse>>
      accountIdentification() {
    log.info("Creating AccountIdentification bean instance");
    return new AccountIdentificationImpl(claimantService, totpRequestService);
  }

  @Bean
  public AccountGetClaimantDetailsImpl accountGetClaimantDetailsV1() {
    log.info("Creating AccountGetClaimantDetails v1 bean instance");
    return new AccountGetClaimantDetailsImpl(claimantService, dataMapper);
  }

  @Bean
  public AccountGetClaimantDetailsV2Impl accountGetClaimantDetailsV2() {
    log.info("Creating AccountGetClaimantDetails v2 bean instance");
    return new AccountGetClaimantDetailsV2Impl(claimantRepository, dataMapper);
  }

  @Bean
  public AccountGetClaimantDetailsV3Impl accountGetClaimantDetailsV3() {
    log.info("Creating AccountGetClaimantDetails v3 bean instance");
    return new AccountGetClaimantDetailsV3Impl(claimantRepository);
  }

  @Bean
  public AccountGetClaimantDetailsV4Impl accountGetClaimantDetailsV4() {
    log.info("Creating AccountGetClaimantDetails v4 bean instance");
    return new AccountGetClaimantDetailsV4Impl(claimantRepository);
  }

  @Bean
  public AccountGetClaimantDetailsV7Impl accountGetClaimantDetailsV7() {
    log.info("Creating AccountGetClaimantDetails v7 bean instance");
    return new AccountGetClaimantDetailsV7Impl(claimantRepository);
  }

  @Bean
  public AccountCheckCanApplyV5Impl accountCheckCanApplyV5() {
    log.info("Creating AccountCheckCanApply v5 bean instance");
    return new AccountCheckCanApplyV5Impl(checkCanApplyService);
  }

  @Bean
  public AccountGetClaimantPhoneNumbersV3Impl accountGetClaimantPhoneNumbersV3() {
    log.info("Creating AccountGetClaimantPhoneNumbers v3 bean instance");
    return new AccountGetClaimantPhoneNumbersV3Impl(claimantRepository);
  }

  @Bean
  public AccountUpdateEmailImpl accountUpdateEmail() {
    log.info("Creating AccountUpdateEmail v1 bean instance");
    return new AccountUpdateEmailImpl(claimantService, dataMapper);
  }

  @Bean
  public AccountUpdateNinoImpl accountUpdateNino() {
    log.info("Creating AccountUpdateNino v3 bean instance");
    return new AccountUpdateNinoImpl(claimantService, dataMapper);
  }

  @Bean
  public AccountUpdateClaimantDetailsImpl accountUpdateClaimantDetails() {
    log.info("Creating accountUpdateClaimantDetails bean instance");
    return new AccountUpdateClaimantDetailsImpl(claimantService,
      dataMapper, accountUpdateEmail(), accountUpdateNino());
  }

  @Bean
  public AccountUpdateClaimantDetailsV4Impl accountUpdateClaimantDetailsV4() {
    log.info("Creating accountUpdateClaimantDetailsV4 bean instance");
    return new AccountUpdateClaimantDetailsV4Impl(claimantService,
      dataMapper, accountUpdateEmail(), accountUpdateNino());
  }

  @Bean
  public AccountUpdateClaimantDetailsV7Impl accountUpdateClaimantDetailsV7() {
    log.info("Creating accountUpdateClaimantDetailsV7 bean instance");
    return new AccountUpdateClaimantDetailsV7Impl(claimantService,
      dataMapper, accountUpdateEmail(), accountUpdateNino());
  }

  @Bean
  public AccountUpdateTransferStatusImpl accountUpdateTransferStatus() {
    log.info("Creating accountUpdateTransferStatus bean instance");
    return new AccountUpdateTransferStatusImpl(claimantService);
  }
}
