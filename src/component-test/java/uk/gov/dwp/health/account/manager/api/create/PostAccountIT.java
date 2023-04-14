package uk.gov.dwp.health.account.manager.api.create;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

public class PostAccountIT extends ApiTest {

    @BeforeEach
    public void testSetup() {
        MongoClientConnection.emptyMongoCollection();
    }

    @Test
    public void shouldReturn201StatusCodeForTacticalAccountCreation() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder().build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);
        AccountCreationResponse accountCreationResponse = response.as(AccountCreationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(accountCreationResponse.getRef()).matches("^[a-zA-Z0-9]{24}$");
    }

    @Test
    public void shouldReturn201StatusCodeForStrategicAccountCreation() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userJourney("STRATEGIC")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);
        AccountCreationResponse accountCreationResponse = response.as(AccountCreationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(accountCreationResponse.getRef()).matches("^[a-zA-Z0-9]{24}$");
    }

    @Test
    public void shouldReturn201StatusCodeForNamesContainingAllowedSpecialChars() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .forename("Tester-Test'Test")
                .surname("Tester-Test'Test")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);
        AccountCreationResponse accountCreationResponse = response.as(AccountCreationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(accountCreationResponse.getRef()).matches("^[a-zA-Z0-9]{24}$");
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidPayload() {
        Response response = postRequest(postAccountUrl(), "}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidUserJourney() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userJourney("NOT_VALID_USER_JOURNEY")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidEmail() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .email("email.com")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidDob() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .dob("01-01-2000")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidMobile() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .mobilePhone("1113-655-566")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidNino() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .nino("ZZ12345Z")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidPostCode() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .postcode("ZA90 99K")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidForename() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .forename("!@£$%^&*()_+sfd")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidSurname() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .surname("!@£$%^&*()_+sfd")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400StatusCodeForInvalidLanguage() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .language("AA")
                .build();

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn409StatusCodeIfAccountExists() {
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder().build();
        postRequest(postAccountUrl(), createAccountRequest);

        Response response = postRequest(postAccountUrl(), createAccountRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
