package uk.gov.dwp.health.account.manager.api.create.v7;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountV5Request;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.*;

class PostAccountIT extends ApiTest {

  private MongoTemplate mongoTemplate;

  @BeforeEach
  void beforeEach() {
    mongoTemplate = MongoClientConnection.getMongoTemplate();
    mongoTemplate.dropCollection("account");
  }

  @Test
  void when_new_account_request_then_create_account() {
    var createAccountRequest = CreateAccountV5Request.builder().build();

    var response = postRequest(postAccountV7Url(), createAccountRequest);

    verify201Response(response);
  }

  @Test
  void when_payload_not_valid() {
    Response response = postRequest(postAccountUrl(), "}");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void when_account_exists() {
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder().build();
    postRequest(postAccountUrl(), createAccountRequest);

    Response response = postRequest(postAccountUrl(), createAccountRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  private void verify201Response(Response response) {
    var accountCreationResponse = response.as(AccountCreationResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(accountCreationResponse.getRef()).matches("^[a-zA-Z0-9]{24}$");
  }

}
