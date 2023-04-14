package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.AggregateIterable;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@ChangeLog(order = "002")
@Slf4j
public class ClaimantDuplicationChangeLog002 {

  @ChangeSet(order = "002", author = "PIP-apply ms-account-manager", id = "removeDuplicateClaimant")
  public void removeDuplicateClaimant(
      MongockTemplate mongoTemplate, ClaimantRepository repository) {
    log.info("Migration claimant collection change set 002");
    var aggregateIterable = findDuplicateClaimantByEmail(mongoTemplate);
    aggregateIterable
        .iterator()
        .forEachRemaining(
            doc -> {
              final var map = (Document) doc.get("_id");
              final var duplicates =
                  repository.findAllByEmailAddress((String) map.get("email_address"));
              sortByObjectTimeStamp(duplicates);
              final int numberOfDuplicates = duplicates.size();
              AtomicReference<Claimant> toKeep = new AtomicReference<>();
              IntStream.range(0, numberOfDuplicates)
                  .forEach(
                      idx -> {
                        if (duplicates.get(idx).getAuth() != null) {
                          toKeep.set(duplicates.get(idx));
                        }
                      });
              if (toKeep.get() != null) {
                duplicates.stream()
                    .filter(e -> !e.equals(toKeep.get()))
                    .forEach(repository::delete);
              } else {
                IntStream.range(1, numberOfDuplicates)
                    .forEach(idx -> repository.delete(duplicates.get(idx)));
              }
            });
    log.info("Migration 002 completed");
  }

  private AggregateIterable<Document> findDuplicateClaimantByEmail(MongockTemplate template) {
    return template
        .getDb()
        .getCollection("account")
        .aggregate(
            Arrays.asList(
                new Document(
                    "$group",
                    new Document(
                            "_id",
                            new Document("email_address", "$email_address")
                                .append("nino_hash", "$nino_hash"))
                        .append("count", new Document("$sum", 1))),
                new Document("$match", new Document("count", new Document("$gt", 1)))));
  }

  LocalDateTime objectId2LocalDateTime(final String objectId) {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(Long.parseLong(objectId.substring(0, 8), 16) * 1000),
        ZoneId.of("UTC"));
  }

  void sortByObjectTimeStamp(List<Claimant> claimantList) {
    claimantList.sort(
        (o1, o2) -> {
          final var t1 = objectId2LocalDateTime(o1.getId());
          final var t2 = objectId2LocalDateTime(o2.getId());
          var result = t1.compareTo(t2);
          result = ((-1) * result);
          if (0 == result) {
            result = t1.compareTo(t2);
          }
          return result;
        });
  }
}
