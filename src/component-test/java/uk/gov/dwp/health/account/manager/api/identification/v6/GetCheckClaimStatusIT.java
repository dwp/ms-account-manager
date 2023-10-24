package uk.gov.dwp.health.account.manager.api.identification.v6;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.openapi.model.CheckClaimResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class GetCheckClaimStatusIT extends ApiTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  static Stream<Boolean> booleanProvider() {
    return Stream.of(true, false);
  }

  @ParameterizedTest
  @MethodSource("booleanProvider")
  void checkClaimStatusValid(boolean checkPipApply) throws IOException, InterruptedException {
    final String nino = "RN000054A";
    final HttpResponse<String> response = checkCanApply(nino, checkPipApply);
    final String body = response.body();
    assertNotNull(body);
    final TypeReference<List<CheckClaimResponse>> type = new TypeReference<>() {};
    final List<CheckClaimResponse> checkClaimResponses = objectMapper.readValue(body, type);
    assertNotNull(checkClaimResponses);
    assertEquals(1, checkClaimResponses.size());
    final CheckClaimResponse checkClaimResponse = checkClaimResponses.get(0);
    assertEquals("yes", checkClaimResponse.getResponseMessage());
    assertEquals(nino, checkClaimResponse.getNino());
  }

  @ParameterizedTest
  @MethodSource("booleanProvider")
  void checkClaimStatusInvalid(boolean checkPipApply) throws IOException, InterruptedException {
    checkClaimStatusErrorResponse(400, "RN000050A", checkPipApply);
  }

  @ParameterizedTest
  @MethodSource("booleanProvider")
  void checkClaimStatusClaimInProgress(boolean checkPipApply) throws IOException, InterruptedException {
    checkClaimStatusErrorResponse(412, "RN000051A", checkPipApply);
  }

  @ParameterizedTest
  @MethodSource("booleanProvider")
  void checkClaimStatusInvalidNino(boolean checkPipApply) throws IOException, InterruptedException {
    checkClaimStatusErrorResponse(417, "RN000052A", checkPipApply);
  }

  @ParameterizedTest
  @MethodSource("booleanProvider")
  void checkClaimStatusServerError(boolean checkPipApply) throws IOException, InterruptedException {
    checkClaimStatusErrorResponse(500, "RN000053A", checkPipApply);
  }

  private static void checkClaimStatusErrorResponse(final int expectedStatus, final String nino, final Boolean checkPipApply) throws IOException, InterruptedException {
    final HttpResponse<String> response = checkCanApply(nino, checkPipApply);
    assertEquals(expectedStatus, response.statusCode());
  }

  private static HttpResponse<String> checkCanApply(final String nino, Boolean checkPipApply) throws IOException, InterruptedException {
    final HttpClient client = HttpClient.newHttpClient();
    final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    final String uriString = baseURI + ":" + port + "/v6/can-apply/" + nino + "?check-pip-apply=" + checkPipApply;
    log.info("checkCanApply endpoint {}", uriString);
    return client.send(HttpRequest.newBuilder()
            .uri(URI.create(uriString))
            .GET()
            .header("x-dwp-correlation-id", "123")
            .build(), handler);
  }
}
