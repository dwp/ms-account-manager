package uk.gov.dwp.health.account.manager.config;

import com.mongodb.MongoClientSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

  private MongoConfig mongoConfig;

  @BeforeEach
  public void beforeEach() {
    this.mongoConfig = new MongoConfig();
  }

  @Test
  @DisplayName("Ensure that the mongo client builds correctly")
  void buildMongoClientTest() {
    mongoConfig.mongoDBDefaultSettings();
  }

  @Test
  @DisplayName("Ensure that the mongo client does not return server api for non stable")
  void buildMongoClientVersionApiFalse(){
    ReflectionTestUtils.setField(this.mongoConfig, "isMongoStableApiEnabled", false);
    Assertions.assertNull(build().getServerApi());
  }

  @Test
  @DisplayName("Ensure that the mongo client returns strict server api for stable")
  void buildMongoClientVersionApiTest(){
    ReflectionTestUtils.setField(this.mongoConfig, "isMongoStableApiEnabled", true);
    Assertions.assertTrue(build().getServerApi().getStrict().get());
  }

  private MongoClientSettings build() {
    final MongoClientSettingsBuilderCustomizer clientSettingsBuilderCustomizer = mongoConfig.mongoDBDefaultSettings();
    final MongoClientSettings.Builder builder = MongoClientSettings.builder();
    clientSettingsBuilderCustomizer.customize(builder);
    return builder.build();
  }
}

