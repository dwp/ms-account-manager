package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.http.totp.Request;
import uk.gov.dwp.health.account.manager.http.totp.TotpVerifyRequest;
import uk.gov.dwp.health.account.manager.openapi.model.Totp;
import uk.gov.dwp.health.account.manager.service.RestClientService;
import uk.gov.dwp.health.account.manager.service.TotpVerifyService;

@Service
public class TotpVerifyServiceImpl implements TotpVerifyService {

  private final SecureSecureHashServiceImpl secureHashService;
  private final RestClientService<Request> totpClientService;

  public TotpVerifyServiceImpl(
      final SecureSecureHashServiceImpl secureHashService,
      final TotpClientServiceImpl totpClientService) {
    this.secureHashService = secureHashService;
    this.totpClientService = totpClientService;
  }

  @Override
  public boolean verify(final Claimant claimant, Totp... totps) {
    boolean outcome = true;
    for (Totp t : totps) {
      String secret = null;
      if (t.getSource() == Totp.SourceEnum.EMAIL) {
        secret =
            secureHashService.hash(
                String.format("%s%s", claimant.getEmailAddress(), claimant.getNino()));
      } else if (t.getSource() == Totp.SourceEnum.MOBILE) {
        secret =
            secureHashService.hash(
                String.format("%s%s", claimant.getMobileNumber(), claimant.getNino()));
      }
      outcome =
          totpClientService.postVerifyRequest(
              TotpVerifyRequest.builder()
                  .region(claimant.getRegion())
                  .secret(secret)
                  .totp(t.getCode())
                  .build());
      if (!outcome) {
        break;
      }
    }
    return outcome;
  }
}
