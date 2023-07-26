package uk.gov.dwp.health.account.manager.api.patch;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.requests.patch.UpdateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;
import uk.gov.dwp.health.account.manager.dto.responses.AccountDetailResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.patchClaimantDetailsUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

class PatchClaimantDetailsIT extends ApiTest {
  private CreateAccountRequest createAccountRequest;
  private AccountCreationResponse accountCreationResponse;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    createAccountRequest = CreateAccountRequest.builder().build();
    accountCreationResponse =
        postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);
  }

  @Test
  void shouldReturn202StatusCodeForClaimantDetailsPatchUpdate() {
    UpdateAccountRequest updateAccountRequest =
        UpdateAccountRequest.builder()
            .currentEmail(createAccountRequest.getEmail())
            .currentNino(createAccountRequest.getNino())
            .ref(accountCreationResponse.getRef())
            .build();

    Response response = patchRequest(patchClaimantDetailsUrl(), updateAccountRequest);
    AccountDetailResponse accountDetailResponse = response.as(AccountDetailResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
    assertThat(accountDetailResponse.getEmail()).isEqualTo(updateAccountRequest.getNewEmail());
    assertThat(accountDetailResponse.getNino()).isEqualTo((updateAccountRequest.getNewNino()));
    assertThat(accountDetailResponse.getDob()).isEqualTo(updateAccountRequest.getDob());
    assertThat(accountDetailResponse.getSurname()).isEqualTo(updateAccountRequest.getSurname());
    assertThat(accountDetailResponse.getForename()).isEqualTo(updateAccountRequest.getForename());
    assertThat(accountDetailResponse.getPostcode()).isEqualTo(updateAccountRequest.getPostcode());
    assertThat(accountDetailResponse.getMobilePhone())
        .isEqualTo(updateAccountRequest.getMobilePhone());
    assertThat(accountDetailResponse.getLanguage()).isEqualTo("EN");
    assertThat(accountDetailResponse.getRegion()).isEqualTo("GB");
    assertThat(accountDetailResponse.getUserJourney())
        .isEqualTo(createAccountRequest.getUserJourney());
    assertThat(accountDetailResponse.getResearchContact()).isEqualTo("No");
    assertThat(accountDetailResponse.getHasPassword()).isFalse();
  }

  @Test
  void shouldReturn400StatusCodeForInvalidClaimantDetailsPatchUpdate() {
    UpdateAccountRequest updateAccountRequest =
        UpdateAccountRequest.builder()
            .ref(accountCreationResponse.getRef())
            .newEmail("abc")
            .build();

    Response response = patchRequest(patchClaimantDetailsUrl(), updateAccountRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.as(Error.class).getMessage()).contains("Validation failed for argument");
  }

  @Test
  void shouldReturn401StatusCodeForNoneExistentClaimantDetailsPatchUpdate() {
    UpdateAccountRequest updateAccountRequest =
        UpdateAccountRequest.builder().ref("b0a0d4fb-e6c8-419e-8cb9-af45914bd1a6").build();

    Response response = patchRequest(patchClaimantDetailsUrl(), updateAccountRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void shouldReturn409StatusCodeForExistingEmailPatchUpdate() {
    CreateAccountRequest createSecondAccountRequest =
        CreateAccountRequest.builder().email("abc@dwp.gov.uk").nino("RN000003C").build();
    AccountCreationResponse accountCreationResponse =
        postRequest(postAccountUrl(), createSecondAccountRequest).as(AccountCreationResponse.class);
    String secondAccountRefId = accountCreationResponse.getRef();

    UpdateAccountRequest updateAccountRequest =
        UpdateAccountRequest.builder()
            .ref(secondAccountRefId)
            .currentNino("RN000003C")
            .currentEmail("abc@dwp.gov.uk")
            .newEmail(createAccountRequest.getEmail())
            .build();
    Response response = patchRequest(patchClaimantDetailsUrl(), updateAccountRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  @Test
  void shouldReturn409StatusCodeForExistingNinoPatchUpdate() {
    CreateAccountRequest createSecondAccountRequest =
        CreateAccountRequest.builder().email("abc@dwp.gov.uk").nino("RN000003C").build();
    AccountCreationResponse accountCreationResponse =
        postRequest(postAccountUrl(), createSecondAccountRequest).as(AccountCreationResponse.class);
    String secondAccountRefId = accountCreationResponse.getRef();

    UpdateAccountRequest updateAccountRequest =
        UpdateAccountRequest.builder()
            .ref(secondAccountRefId)
            .currentNino("RN000003C")
            .currentEmail("abc@dwp.gov.uk")
            .newNino(createAccountRequest.getNino())
            .build();
    Response response = patchRequest(patchClaimantDetailsUrl(), updateAccountRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
  }
}
