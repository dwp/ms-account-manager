package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.constant.COMM;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.http.totp.TotpGenerateRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotpRequestServiceImplTest {

  @InjectMocks private TotpRequestServiceImpl cut;
  @Mock private SecureSecureHashServiceImpl secureHashService;
  @Mock private TotpClientServiceImpl totpClientService;
  @Captor private ArgumentCaptor<String> strArgCaptor;
  @Captor private ArgumentCaptor<TotpGenerateRequest> totpReqArgCaptor;

  private static Stream<Arguments> testCases() {
    var citizenEmail = new Claimant();
    citizenEmail.setEmailAddress(TestFixtures.EMAIL);
    citizenEmail.setRegion(Region.GB);
    citizenEmail.setNino(TestFixtures.NINO);
    var commEmail = COMM.EMAIL;

    var citizenSms = new Claimant();
    citizenSms.setEmailAddress(TestFixtures.MOBILE);
    citizenSms.setRegion(Region.GB);
    citizenSms.setNino(TestFixtures.NINO);
    var commSms = COMM.MOBILE;

    return Stream.of(Arguments.of(citizenEmail, commEmail), Arguments.of(citizenSms, commSms));
  }

  @ParameterizedTest
  @MethodSource("testCases")
  @DisplayName("test request new totp by email or sms")
  void testRequestNewTotpByEmailOrSms(Claimant citizen, COMM method) {
    when(secureHashService.hash(anyString())).thenReturn("hashed-string-value");
    cut.requestNewTotp(citizen, method);
    verify(totpClientService).postGenerateRequest(totpReqArgCaptor.capture());
    var capturedRequest = totpReqArgCaptor.getValue();
    verify(secureHashService).hash(strArgCaptor.capture());
    var capturedString = strArgCaptor.getValue();
    assertAll(
        "assert captured values",
        () -> {
          assertEquals(Region.GB, capturedRequest.getRegion());
          assertEquals(method.name(), capturedRequest.getComm());
          assertEquals(
              method == COMM.EMAIL ? citizen.getEmailAddress() : citizen.getMobileNumber(),
              capturedRequest.getContact());
          assertEquals("hashed-string-value", capturedRequest.getSecret());
          assertEquals(
              method == COMM.EMAIL
                  ? citizen.getEmailAddress() + TestFixtures.NINO
                  : citizen.getMobileNumber() + TestFixtures.NINO,
              capturedString);
        });
  }
}
