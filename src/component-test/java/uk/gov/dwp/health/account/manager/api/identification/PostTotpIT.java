package uk.gov.dwp.health.account.manager.api.identification;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.dto.requests.identification.IdentificationRequest;
import uk.gov.dwp.health.account.manager.dto.requests.identification.TotpRequest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postIdentifyUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postTotpUrl;

public class PostTotpIT extends ApiTest {
    private AccountCreationResponse accountCreationResponse;

    @BeforeEach
    public void testSetup() {
        MongoClientConnection.emptyMongoCollection();
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder().build();
        accountCreationResponse = postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);

        IdentificationRequest identifyDetails = IdentificationRequest.builder().build();
        postRequest(postIdentifyUrl(), identifyDetails);
    }

    @Test
    public void shouldReturn200StatusCodeForTOTPVerify() {
        TotpRequest totpRequest = TotpRequest.builder()
                .ref(accountCreationResponse.getRef())
                .build();

        Response response = postRequest(postTotpUrl(), totpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldReturn400StatusCodeForInvalidBody() {
        TotpRequest totpRequest = TotpRequest.builder()
                .ref(accountCreationResponse.getRef())
                .totp(TotpRequest.TotpBlock.builder().source("not a valid source").build())
                .build();

        Response response = postRequest(postTotpUrl(), totpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturn401StatusCodeForUnauthorized() {
        TotpRequest totpRequest = TotpRequest.builder()
                .build();

        Response response = postRequest(postTotpUrl(), totpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
