package uk.gov.dwp.health.account.manager.api.patch;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;
import uk.gov.dwp.health.account.manager.openapi.model.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.patchClaimantTransferStatusUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

class PatchClaimantTransferStatusDetailsIT extends ApiTest {
  private AccountCreationResponse accountCreationResponse;

  @BeforeEach
  public void testSetup() {
    MongoClientConnection.emptyMongoCollection();
    CreateAccountRequest createAccountRequest = CreateAccountRequest.builder().build();
    accountCreationResponse =
            postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);
  }


  @Test
  void shouldReturn202StatusCodeForClaimantUpdateTransferStatusDetailsPatchUpdate() {
    String accountId = accountCreationResponse.getRef();

    Response response = patchRequest(patchClaimantTransferStatusUrl(accountId), accountId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
    assertThat(response.as(Message.class).getMessage()).isEqualTo("Transfer Status has successfully been updated.");
  }

  @Test
  void shouldReturn401StatusCodeForNoneExistentClaimantDetailsTransferStatusPatchUpdate() {
    String accountId = "invalid-ref";

    Response response = patchRequest(patchClaimantTransferStatusUrl(accountId), accountId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }
}