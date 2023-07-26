package uk.gov.dwp.health.account.manager.api.query;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountDetailResponse;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.getAccountByIDUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

class GetAccountDetailsByIdIT extends ApiTest {
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
  void shouldReturn200StatusCodeAndAccountDetailsWhenRetrieveAccountById() {
    Response response = getRequest(getAccountByIDUrl(accountCreationResponse.getRef()));
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
  }
}
