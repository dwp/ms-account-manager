package uk.gov.dwp.health.account.manager.api.query;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;
import uk.gov.dwp.health.account.manager.dto.responses.AccountDetailResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.getAccountByEmailUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.patchClaimantTransferStatusUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

class GetAccountDetailsByEmailIT extends ApiTest {
  private CreateAccountRequest createAccountRequest;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    createAccountRequest = CreateAccountRequest.builder().build();
    postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);
  }

  @Test
  void shouldReturn200StatusCodeAndAccountDetailsWhenRetrieveAccountByEmail() {
    Response response = getRequest(getAccountByEmailUrl(createAccountRequest.getEmail()));
    AccountDetailResponse accountDetailResponse = response.as(AccountDetailResponse[].class)[0];

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(accountDetailResponse.getEmail()).isEqualTo(createAccountRequest.getEmail());
    assertThat(accountDetailResponse.getNino()).isEqualTo(createAccountRequest.getNino());
    assertThat(accountDetailResponse.getDob()).isEqualTo(createAccountRequest.getDob());
    assertThat(accountDetailResponse.getSurname()).isEqualTo(createAccountRequest.getSurname());
    assertThat(accountDetailResponse.getForename()).isEqualTo(createAccountRequest.getForename());
    assertThat(accountDetailResponse.getPostcode()).isEqualTo(createAccountRequest.getPostcode());
    assertThat(accountDetailResponse.getMobilePhone())
        .isEqualTo(createAccountRequest.getMobilePhone());
    assertThat(accountDetailResponse.getLanguage()).isEqualTo("EN");
    assertThat(accountDetailResponse.getRegion()).isEqualTo("GB");
    assertThat(accountDetailResponse.getUserJourney())
        .isEqualTo(createAccountRequest.getUserJourney());
    assertThat(accountDetailResponse.getResearchContact()).isEqualTo("Yes");
    assertThat(accountDetailResponse.getHasPassword()).isFalse();
    assertThat(accountDetailResponse.getTransferredToDwpApply()).isNull();
  }

  @Test
  void shouldReturn200StatusCodeAndAccountDetailsWhenRetrieveAccountByEmailAndTransferStatusIsProvided() {
    String email = createAccountRequest.getEmail();

    Response patchResponse = patchRequestWithHeader(patchClaimantTransferStatusUrl(), "x-email", email);

    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED.value());

    Response response = getRequest(getAccountByEmailUrl(createAccountRequest.getEmail()));
    AccountDetailResponse accountDetailResponse = response.as(AccountDetailResponse[].class)[0];

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(accountDetailResponse.getRef()).matches("^(?!\\s*$).+");
    assertThat(accountDetailResponse.getEmail()).isEqualTo(createAccountRequest.getEmail());
    assertThat(accountDetailResponse.getNino()).isEqualTo(createAccountRequest.getNino());
    assertThat(accountDetailResponse.getDob()).isEqualTo(createAccountRequest.getDob());
    assertThat(accountDetailResponse.getSurname()).isEqualTo(createAccountRequest.getSurname());
    assertThat(accountDetailResponse.getForename()).isEqualTo(createAccountRequest.getForename());
    assertThat(accountDetailResponse.getPostcode()).isEqualTo(createAccountRequest.getPostcode());
    assertThat(accountDetailResponse.getMobilePhone())
        .isEqualTo(createAccountRequest.getMobilePhone());
    assertThat(accountDetailResponse.getLanguage()).isEqualTo("EN");
    assertThat(accountDetailResponse.getRegion()).isEqualTo("GB");
    assertThat(accountDetailResponse.getUserJourney())
        .isEqualTo(createAccountRequest.getUserJourney());
    assertThat(accountDetailResponse.getResearchContact()).isEqualTo("Yes");
    assertThat(accountDetailResponse.getHasPassword()).isFalse();
    assertThat(accountDetailResponse.getTransferredToDwpApply()).isTrue();
  }

  @Test
  void shouldReturn400StatusCodeWhenRetrieveAccountWithInvalidEmail() {
    Response response = getRequest(getAccountByEmailUrl("not-a-valid-email"));
    Error error = response.as(Error.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(error.getMessage()).isEqualTo("Validation failed on email address format");
  }
}
