package uk.gov.dwp.health.account.manager.api.identification;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.requests.identification.IdentificationRequest;
import uk.gov.dwp.health.account.manager.dto.requests.identification.LoginRequest;
import uk.gov.dwp.health.account.manager.dto.requests.password.CreatePasswordRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;
import uk.gov.dwp.health.account.manager.dto.responses.LoginResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postIdentifyUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postLoginUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postPasswordUrl;

public class PostLoginIT extends ApiTest {
  private CreateAccountRequest createAccountRequest;
  private CreatePasswordRequest createPasswordRequest;
  private AccountCreationResponse accountCreationResponse;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    createAccountRequest = CreateAccountRequest.builder().build();
    accountCreationResponse =
        postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);

    IdentificationRequest identificationRequest =
        IdentificationRequest.builder()
            .dob(createAccountRequest.getDob())
            .nino(createAccountRequest.getNino())
            .email(createAccountRequest.getEmail())
            .build();
    postRequest(postIdentifyUrl(), identificationRequest);

    createPasswordRequest =
        CreatePasswordRequest.builder().ref(accountCreationResponse.getRef()).build();
    postRequest(postPasswordUrl(), createPasswordRequest);
  }

  @Test
  public void shouldReturn200ForSuccessfulLogin() {
    LoginRequest loginRequest =
        LoginRequest.builder()
                .email(createAccountRequest.getEmail())
                .password(createPasswordRequest.getPassword())
                .build();

    Response response = postRequest(postLoginUrl(), loginRequest);
    LoginResponse loginResponse = response.as(LoginResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(loginResponse.getRef()).isEqualTo(accountCreationResponse.getRef());
  }

  @Test
  void shouldReturn400ForInvalidRequestBody() {
    LoginRequest loginRequest = LoginRequest.builder().email("not-a-valid-email").build();

    Response response = postRequest(postLoginUrl(), loginRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void shouldReturn401ForUnauthorised() {
    LoginRequest loginRequest = LoginRequest.builder().password("incorrect password").build();

    Response response = postRequest(postLoginUrl(), loginRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void shouldReturn403ForAccountLocked() {
    LoginRequest loginRequest =
        LoginRequest.builder()
            .email(createAccountRequest.getEmail())
            .password("incorrect password")
            .build();

    for (int i = 0; i < 4; i++) {
      postRequest(postLoginUrl(), loginRequest);
    }

    Response response = postRequest(postLoginUrl(), loginRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
  }
}
