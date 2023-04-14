package uk.gov.dwp.health.account.manager.api.query;

import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.dto.responses.MobileNumbersResponse;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountRequest;
import uk.gov.dwp.health.account.manager.dto.responses.AccountCreationResponse;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.getMobileNumbersByClaimantIdUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountUrl;

public class GetMobileNumbersResponseByClaimantIdsIT extends ApiTest {
    CreateAccountRequest createAccountRequest;
    private AccountCreationResponse accountCreationResponse;

    @BeforeEach
    public void testSetup() {
        MongoClientConnection.emptyMongoCollection();
        createAccountRequest = CreateAccountRequest.builder().build();
        accountCreationResponse = postRequest(postAccountUrl(), createAccountRequest).as(AccountCreationResponse.class);
    }

    @Test
    public void shouldReturn200StatusCodeAndListOfMobileNumbers() {
        Response response = getRequest(getMobileNumbersByClaimantIdUrl(accountCreationResponse.getRef()));
        MobileNumbersResponse[] mobileNumbersResponses = response.as(MobileNumbersResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(mobileNumbersResponses).isNotEmpty();
        assertThat(mobileNumbersResponses[0].getMobilePhone()).isEqualTo(createAccountRequest.getMobilePhone());
        assertThat(mobileNumbersResponses[0].getClaimantId()).isEqualTo(accountCreationResponse.getRef());
    }

    @Test
    public void shouldReturn200StatusCodeAndListOfMobileNumbersMultipleClaimantIds() {
        ArrayList<String> accounts = createAccounts(100);
        String csListOfAccounts = StringUtils.join(accounts.toArray(), ",");

        Response response = getRequest(getMobileNumbersByClaimantIdUrl(csListOfAccounts));
        MobileNumbersResponse[] mobileNumbersResponses = response.as(MobileNumbersResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(mobileNumbersResponses).isNotEmpty();
        assertThat(mobileNumbersResponses.length).isEqualTo(100);
    }

    @Test
    public void shouldReturn200AndEmptyArrayIfClaimantIdsDoNotExist() {
        Response response = getRequest(getMobileNumbersByClaimantIdUrl("63861d815f95f75dc768d020"));
        MobileNumbersResponse[] mobileNumbersResponses = response.as(MobileNumbersResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(Arrays.toString(mobileNumbersResponses)).isEqualTo("[]");
    }

    private ArrayList<String> createAccounts(int numberOfAccounts) {
        ArrayList<String> claimantIds = new ArrayList<>();

        for (int i = 0; i < numberOfAccounts; i++) {
            CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                    .nino("AC" + String.format("%06d", i) + "C")
                    .email("email" + i + "@dwp.gov.uk")
                    .mobilePhone("07" + String.format("%09d", i))
                    .build();
            final Response response = postRequest(postAccountUrl(), createAccountRequest);
            AccountCreationResponse accountCreationResponse = response.as(AccountCreationResponse.class);
            claimantIds.add(accountCreationResponse.getRef());
        }

        return claimantIds;
    }
}
