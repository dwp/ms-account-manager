package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MigrationIndexHelper {

  void findAndDropIndexByName(Class clazz, String idxToDelete, MongockTemplate mongockTemplate) {
    mongockTemplate.indexOps(clazz).getIndexInfo().stream()
        .filter(idx -> idx.getName().equals(idxToDelete))
        .findFirst()
        .ifPresentOrElse(
          found -> {
              final var indexName = found.getName();
              log.info("Index [{}] found", indexName);
              try {
                mongockTemplate.indexOps(clazz).dropIndex(indexName);
              } catch (Exception ex) {
                log.error("Index operation failed on drop command {}", ex.getMessage());
              }
              log.info("Index [{}] dropped successfully", indexName);
            },
          () -> log.warn("Index not found with name [{}]", idxToDelete));
  }
}
