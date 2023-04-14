package uk.gov.dwp.health.account.manager.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import static uk.gov.dwp.health.account.manager.utils.EnvironmentUtil.getEnv;

public class MongoClientConnection {
    public static MongoTemplate getMongoTemplate() {
        ConnectionString connectionString =
                new ConnectionString(
                        "mongodb://"
                                + getEnv("MONGODB_HOST", "localhost")
                                + ":"
                                +  getEnv("MONGODB_PORT", "27017")
                                + "/pip-apply-acc-mgr");

        MongoClientSettings mongoClientSettings =
                MongoClientSettings.builder().applyConnectionString(connectionString).build();

        MongoClient mongoClient = MongoClients.create(mongoClientSettings);

        return new MongoTemplate(mongoClient, "pip-apply-acc-mgr");
    }

    public static void emptyMongoCollection() {
        MongoCollection<Document> mongoCollection = getMongoTemplate().getCollection("account");
        mongoCollection.drop();
    }
}
