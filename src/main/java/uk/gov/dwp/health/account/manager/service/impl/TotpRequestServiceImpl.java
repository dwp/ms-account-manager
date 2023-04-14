package uk.gov.dwp.health.account.manager.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.http.totp.TotpGenerateRequest;
import uk.gov.dwp.health.account.manager.service.SecureHashService;
import uk.gov.dwp.health.account.manager.service.TotpRequestService;

@Service
public class TotpRequestServiceImpl implements TotpRequestService {

  private final TotpClientServiceImpl totpClientService;
  private final SecureHashService<String, String> secureHashService;

  public TotpRequestServiceImpl(
      TotpClientServiceImpl client, SecureHashService<String, String> secureHashService) {
    this.totpClientService = client;
    this.secureHashService = secureHashService;
  }

  @Override
  public void requestNewTotp(Claimant citizen, COMM contactMethod) {
    totpClientService.postGenerateRequest(
        TotpGenerateRequest.builder()
            .region(citizen.getRegion())
            .comm(contactMethod.method())
            .contact(
                COMM.EMAIL == contactMethod ? citizen.getEmailAddress() : citizen.getMobileNumber())
            .secret(
                COMM.EMAIL == contactMethod
                    ? secureHashService.hash(
                        String.format("%s%s", citizen.getEmailAddress(), citizen.getNino()))
                    : secureHashService.hash(
                        String.format("%s%s", citizen.getMobileNumber(), citizen.getNino())))
            .build());
  }
}
