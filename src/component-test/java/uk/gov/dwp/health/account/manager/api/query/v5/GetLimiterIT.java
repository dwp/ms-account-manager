package uk.gov.dwp.health.account.manager.api.query.v5;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import uk.gov.dwp.health.account.manager.api.ApiTest;
import uk.gov.dwp.health.account.manager.config.MongoClientConnection;
import uk.gov.dwp.health.account.manager.dto.requests.create.CreateAccountV5Request;
import uk.gov.dwp.health.account.manager.dto.responses.LimiterResponse;
import uk.gov.dwp.health.account.manager.entity.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.getLimiterUrl;
import static uk.gov.dwp.health.account.manager.utils.UrlBuilderUtil.postAccountV5Url;

class GetLimiterIT extends ApiTest {

  private MongoTemplate mongoTemplate;

  @BeforeEach
  void beforeEach() {
    mongoTemplate = MongoClientConnection.getMongoTemplate();
    mongoTemplate.dropCollection("registration");
    mongoTemplate.dropCollection("account");
  }

  @Test
  void when_getting_limiter() {
    var registration = Registration.builder().count(1).build();
    mongoTemplate.save(registration);

    var response = getRequest(getLimiterUrl());
    var limiterResponse = response.as(LimiterResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(limiterResponse.isLimitReached()).isFalse();
  }

  @Test
  void when_limit_reached_and_schedule_triggered() {
    verifyLimitReached();

    await().atMost(1, TimeUnit.MINUTES).until(() -> isCount(0));

    verifyNewRegistration();
  }

  private void verifyLimitReached() {
    var registration = Registration.builder().count(11).build();
    mongoTemplate.save(registration);

    var response = getRequest(getLimiterUrl());
    var limiterResponse = response.as(LimiterResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(limiterResponse.isLimitReached()).isTrue();
  }

  private void verifyNewRegistration() {
    var createAccountRequest = CreateAccountV5Request.builder().build();
    var response = postRequest(postAccountV5Url(), createAccountRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(isCount(1)).isTrue();
  }

  private boolean isCount(int expectedCount) {
    MongoCollection<Document> collection = mongoTemplate.getCollection("registration");

    Document query = new Document("count", expectedCount);
    List<Document> results = new ArrayList<>();

    collection.find(query).into(results);

    return results.size() == 1;
  }
}
