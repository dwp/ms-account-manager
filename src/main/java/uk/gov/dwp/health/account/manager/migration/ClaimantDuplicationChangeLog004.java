package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v4.decorator.impl.MongockTemplate;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import uk.gov.dwp.health.account.manager.entity.Claimant;

@ChangeLog(order = "004")
@Slf4j
public class ClaimantDuplicationChangeLog004 extends MigrationIndexHelper {

  private static final String TARGET_INDEX = "email_address_nino_hash_idx";

  @Generated
  @ChangeSet(order = "004", id = "migrateCollectionHashIndex",
          author = "PIP-Apply ms-account-manager")
  public void migrateCollectionIndex(MongockTemplate mongockTemplate) {
    log.info("Begin migration change set 004");
    findAndDropIndexByName(Claimant.class, TARGET_INDEX, mongockTemplate);
    createIndexOfEmailAddressAndNino(mongockTemplate);
    log.info("Complete migration change set 004 successfully");
  }

  void createIndexOfEmailAddressAndNino(MongockTemplate mongockTemplate) {
    log.info("Create new index");
    mongockTemplate
        .indexOps(Claimant.class)
        .ensureIndex(
            new CompoundIndexDefinition(
                    new Document().append("email_address", 1).append("nino", 1))
                .unique()
                .named("email_address_nino_idx"));
    log.info("New index created successfully");
  }
}
