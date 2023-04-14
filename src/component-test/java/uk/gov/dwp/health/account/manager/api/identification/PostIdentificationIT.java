package uk.gov.dwp.health.account.manager.api.identification;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.dto.requests.identification.IdentificationRequest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postIdentifyUrl;

public class PostIdentificationIT extends ApiTest {
  private CreateAccountRequest createAccountRequest;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    createAccountRequest = CreateAccountRequest.builder().build();
    postRequest(postAccountUrl(), createAccountRequest);
  }

  @Test
  public void shouldReturn200StatusCodeForAccountIdentify() {
    IdentificationRequest identificationRequest = IdentificationRequest.builder()
            .dob(createAccountRequest.getDob())
            .nino(createAccountRequest.getNino())
            .email(createAccountRequest.getEmail())
            .build();

    Response response = postRequest(postIdentifyUrl(), identificationRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
  }

  @Test
  void shouldReturn400StatusCodeForAccountIdentifyWhenInvalidPayload() {
    IdentificationRequest identificationRequest = IdentificationRequest.builder().dob("not-a-valid-dob").build();

    Response response = postRequest(postIdentifyUrl(), identificationRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void shouldReturn401StatusCodeForAccountIdentifyWhenAccountDoesNotExist() {
    IdentificationRequest identificationRequest = IdentificationRequest.builder()
            .nino("AA370773A")
            .build();

    Response response = postRequest(postIdentifyUrl(), identificationRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }
}
