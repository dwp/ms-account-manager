package uk.gov.dwp.health.account.manager.api.patch;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.patchClaimantTransferStatusUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.Message;

class PatchClaimantTransferStatusDetailsIT extends ApiTest {

  private CreateAccountRequest createAccountRequest;
  private final String EMAIL_HEADER = "x-email";

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    createAccountRequest = CreateAccountRequest.builder().build();
    postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);
  }


  @Test
  void shouldReturn202StatusCodeForClaimantUpdateTransferStatusDetailsPatchUpdate() {
    String email = createAccountRequest.getEmail();

    Response response = patchRequestWithHeader(patchClaimantTransferStatusUrl(), EMAIL_HEADER, email);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
    assertThat(response.as(Message.class).getMessage()).isEqualTo("Transfer Status has successfully been updated.");
  }

  @Test
  void shouldReturn401StatusCodeForNoneExistentClaimantDetailsTransferStatusPatchUpdate() {
    String email = "test@email.com";

    Response response = patchRequestWithHeader(patchClaimantTransferStatusUrl(), EMAIL_HEADER, email);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void shouldReturn400StatusCodeForBadlyFormedEmailAddress() {
    String email = "invalid-ref";

    Response response = patchRequestWithHeader(patchClaimantTransferStatusUrl(), EMAIL_HEADER, email);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void shouldReturn200StatusCodeForClaimantTransferStatusAlreadyTrue() {
    String email = createAccountRequest.getEmail();

    //Request to set status to true.
    patchRequestWithHeader(patchClaimantTransferStatusUrl(), EMAIL_HEADER, email);
    Response responseTrue = patchRequestWithHeader(patchClaimantTransferStatusUrl(), EMAIL_HEADER, email);

    assertThat(responseTrue.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(responseTrue.as(Message.class).getMessage())
            .isEqualTo("No changes required as account previously marked as transferred.");
  }
}