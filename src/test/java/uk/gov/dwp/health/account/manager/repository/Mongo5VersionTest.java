package uk.gov.dwp.health.account.manager.repository;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.account.manager.config.MongoConfig;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(MongoConfig.class)
public class Mongo5VersionTest {
  @Autowired private MongoTemplate mongoTemplate;

  @Test
  public void shouldFailOnOlderVersion() {
    // assert
    UncategorizedMongoDbException thrown = assertThrows(
            UncategorizedMongoDbException.class,
            () -> this.mongoTemplate.executeCommand("{ buildInfo: 1 }"),
            "Expected mongoTemplate.executeCommand() to throw UncategorizedMongoDbException, but it didn't"
    );

    assertTrue(thrown.getMessage().contains("Provided apiStrict:true, but the command buildInfo is not in API Version 1"));
  }

  @Test
  public void shouldPassOnNewerVersion() {
    // assert
    final Document document = this.mongoTemplate.executeCommand("{ ping: 1 }");
    final Double okValue = document.getDouble("ok");
    assertNotNull(okValue, "Mongo5 health check fails with a null");
    assertEquals(1.0D, okValue, "Mongo5 health check fails with a non-OK");
  }
}
