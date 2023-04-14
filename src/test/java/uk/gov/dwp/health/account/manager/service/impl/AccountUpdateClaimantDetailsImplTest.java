package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.http.HttpStatus;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateClaimantDetailsRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateEmailRequest;
import uk.gov.dwp.health.account.manager.openapi.model.UpdateNinoRequest;
import uk.gov.dwp.health.account.manager.service.AccountDataMapper;
import uk.gov.dwp.health.account.manager.service.ClaimantService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.health.account.manager.mock.claimant.getClaimant;

class AccountUpdateClaimantDetailsImplTest {

  private AccountUpdateClaimantDetailsImpl cut;
  private ClaimantService claimantService;
  private AccountDataMapper dataMapper;
  private AccountUpdateEmailImpl updateEmailImpl;
  private AccountUpdateNinoImpl updateNinoImpl;

  @BeforeEach
  void setup() {
    claimantService = mock(ClaimantService.class);
    dataMapper = mock(AccountDataMapper.class);
    updateEmailImpl = mock(AccountUpdateEmailImpl.class);
    updateNinoImpl = mock(AccountUpdateNinoImpl.class);
    cut = new AccountUpdateClaimantDetailsImpl(claimantService, dataMapper, updateEmailImpl, updateNinoImpl);
  }

  @Test
  @DisplayName("test update account user details")
  void testUpdateAccountClaimantDetails() {
    var strCaptor = ArgumentCaptor.forClass(String.class);
    var claimantCaptor = ArgumentCaptor.forClass(Claimant.class);
    var updateEmailRequestCaptor = ArgumentCaptor.forClass(UpdateEmailRequest.class);
    var updateNinoRequestCaptor = ArgumentCaptor.forClass(UpdateNinoRequest.class);
    var req = mock(UpdateClaimantDetailsRequest.class);

    var claimant = getClaimant();
    
    var currentEmail = TestFixtures.CURRENT_EMAIL;
    when(req.getCurrentEmail()).thenReturn(currentEmail);
    var newEmail = TestFixtures.NEW_EMAIL;
    when(req.getNewEmail()).thenReturn(newEmail);
    
    var currentNino = TestFixtures.CURRENT_NINO;
    when(req.getCurrentNino()).thenReturn(currentNino);
    var newNino = TestFixtures.NEW_NINO;
    when(req.getNewNino()).thenReturn(newNino);

    when(req.getRef()).thenReturn(TestFixtures.REF);

    var email = TestFixtures.EMAIL;
    when(req.getNewEmail()).thenReturn(email);
    var nino = TestFixtures.NINO;
    when(req.getNewNino()).thenReturn(nino);
    var forename = TestFixtures.FORENAME;
    when(req.getForename()).thenReturn(forename);
    var surname = TestFixtures.SURNAME;
    when(req.getSurname()).thenReturn(surname);
    var mobilePhone = TestFixtures.MOBILE;
    when(req.getMobilePhone()).thenReturn(mobilePhone);
    var postcode = TestFixtures.POSTCODE;
    when(req.getPostcode()).thenReturn(postcode);
    var dob = TestFixtures.DOB;
    when(req.getDob()).thenReturn(dob);

    when(claimantService.findByRef(anyString())).thenReturn(claimant);

    var actual = cut.updateClaimantDetails(req);

    var order = inOrder(updateEmailImpl, updateNinoImpl, claimantService, dataMapper);
    order.verify(updateEmailImpl).validateEmail(updateEmailRequestCaptor.capture());
    order.verify(updateNinoImpl).validateNino(updateNinoRequestCaptor.capture());
    
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    order.verify(claimantService).findByRef(strCaptor.capture());
    assertThat(strCaptor.getValue()).isEqualTo(TestFixtures.REF);

    order.verify(claimantService).updateClaimant(claimantCaptor.capture());

    assertThat(claimantCaptor.getValue().getEmailAddress()).isEqualTo(email);
    assertThat(claimantCaptor.getValue().getNino()).isEqualTo(nino);
    assertThat(claimantCaptor.getValue().getForename()).isEqualTo(forename);
    assertThat(claimantCaptor.getValue().getSurname()).isEqualTo(surname);
    assertThat(claimantCaptor.getValue().getMobileNumber()).isEqualTo(mobilePhone);
    assertThat(claimantCaptor.getValue().getPostcode()).isEqualTo(postcode);
    assertThat(claimantCaptor.getValue().getDateOfBirth()).isEqualTo(dob);

    order.verify(dataMapper).mapToV3AccountDetails(claimantCaptor.capture());
    
    assertThat(claimantCaptor.getValue().getEmailAddress()).isEqualTo(email);
    assertThat(claimantCaptor.getValue().getNino()).isEqualTo(nino);
    assertThat(claimantCaptor.getValue().getForename()).isEqualTo(forename);
    assertThat(claimantCaptor.getValue().getSurname()).isEqualTo(surname);
    assertThat(claimantCaptor.getValue().getMobileNumber()).isEqualTo(mobilePhone);
    assertThat(claimantCaptor.getValue().getPostcode()).isEqualTo(postcode);
    assertThat(claimantCaptor.getValue().getDateOfBirth()).isEqualTo(dob);
  }
}
