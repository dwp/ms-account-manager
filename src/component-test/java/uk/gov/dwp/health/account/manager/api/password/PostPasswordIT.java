package uk.gov.dwp.health.account.manager.api.password;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.dto.requests.identification.IdentificationRequest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.requests.password.CreatePasswordRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postIdentifyUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postPasswordUrl;

public class PostPasswordIT extends ApiTest {
  private AccountCreationResponse accountCreationResponse;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder().build();
    accountCreationResponse = postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);

    IdentificationRequest identification = IdentificationRequest.builder().build();
    postRequest(postIdentifyUrl(), identification);
  }

  @Test
  void shouldReturn200StatusCodeForPasswordReset() {
    CreatePasswordRequest createPasswordRequest = CreatePasswordRequest.builder()
            .ref(accountCreationResponse.getRef())
            .password("UpdatedPassword123")
            .build();
    postRequest(postPasswordUrl(), createPasswordRequest);

    Response response = postRequest(postPasswordUrl(), createPasswordRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
  }

  @Test
  void shouldReturn201StatusCodeForCreatePassword() {
    CreatePasswordRequest createPasswordRequest = CreatePasswordRequest.builder()
            .ref(accountCreationResponse.getRef())
            .build();

    Response response = postRequest(postPasswordUrl(), createPasswordRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
  }

  @Test
  void shouldReturn400StatusCodeForBadRequest() {
    CreatePasswordRequest createPasswordRequest = CreatePasswordRequest.builder()
            .ref(accountCreationResponse.getRef())
            .password(null)
            .build();

    Response response = postRequest(postPasswordUrl(), createPasswordRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void shouldReturn401StatusCodeForUnauthorised() {
    CreatePasswordRequest createPasswordRequest = CreatePasswordRequest.builder()
            .ref(accountCreationResponse.getRef())
            .totp(List.of(CreatePasswordRequest.TotpBlock.builder().code("000000").build()))
            .build();

    Response response = postRequest(postPasswordUrl(), createPasswordRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void shouldReturn503StatusCodeForBadTOTP() {
    CreatePasswordRequest createPasswordRequest = CreatePasswordRequest.builder()
            .ref(accountCreationResponse.getRef())
            .totp(List.of(CreatePasswordRequest.TotpBlock.builder().code("000001").build()))
            .build();

    Response response = postRequest(postPasswordUrl(), createPasswordRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
  }
}
