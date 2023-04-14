package uk.gov.dwp.health.account.manager.api.query;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountDetailResponse;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.getAccountByNinoUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

public class GetAccountDetailsByNinoIT extends ApiTest {
  private CreateAccountRequest createAccountRequest;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    createAccountRequest = CreateAccountRequest.builder().build();
    postRequest(postAccountUrl(), createAccountRequest);
  }

  @Test
  public void shouldReturn200StatusCodeAndAccountDetailsWhenRetrieveAccountByNino() {
    Response response = getRequest(getAccountByNinoUrl(createAccountRequest.getNino()));
    AccountDetailResponse accountDetailResponse = response.as(AccountDetailResponse[].class)[0];

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(accountDetailResponse.getEmail()).isEqualTo(createAccountRequest.getEmail());
    assertThat(accountDetailResponse.getNino()).isEqualTo(createAccountRequest.getNino());
    assertThat(accountDetailResponse.getDob()).isEqualTo(createAccountRequest.getDob());
    assertThat(accountDetailResponse.getSurname()).isEqualTo(createAccountRequest.getSurname());
    assertThat(accountDetailResponse.getForename()).isEqualTo(createAccountRequest.getForename());
    assertThat(accountDetailResponse.getPostcode()).isEqualTo(createAccountRequest.getPostcode());
    assertThat(accountDetailResponse.getMobilePhone()).isEqualTo(createAccountRequest.getMobilePhone());
    assertThat(accountDetailResponse.getLanguage()).isEqualTo("EN");
    assertThat(accountDetailResponse.getRegion()).isEqualTo("GB");
    assertThat(accountDetailResponse.getUserJourney()).isEqualTo(createAccountRequest.getUserJourney());
    assertThat(accountDetailResponse.getResearchContact()).isEqualTo("Yes");
  }

  @Test
  void shouldReturn400StatusCodeWhenRetrieveAccountByInvalidNino() {
    Response response = getRequest(getAccountByNinoUrl("not-a-valid-nino"));
    Error error = response.as(Error.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(error.getMessage()).isEqualTo("Validation failed on NINO format");
  }
}
