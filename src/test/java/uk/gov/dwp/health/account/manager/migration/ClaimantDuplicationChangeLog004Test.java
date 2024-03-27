package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v4.decorator.impl.MongockTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import uk.gov.dwp.health.account.manager.entity.Claimant;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(value = OrderAnnotation.class)
class ClaimantDuplicationChangeLog004Test {

  private static ClaimantDuplicationChangeLog004 claimantHashIndexChangeLog;
  private MongockTemplate mongockTemplate;

  @BeforeEach
  void beforeEach() {
    mongockTemplate = mock(MongockTemplate.class);
    claimantHashIndexChangeLog = new ClaimantDuplicationChangeLog004();
  }

  @Test
  void should_drop_index_by_name_and_create_unique_new_index() {
    var indexOperation = mock(IndexOperations.class);
    var indexInfo = mock(IndexInfo.class);
    when(indexInfo.getName()).thenReturn("email_address_nino_hash_idx");
    when(indexOperation.getIndexInfo()).thenReturn(List.of(indexInfo));
    when(mongockTemplate.indexOps(any(Class.class))).thenReturn(indexOperation);

    claimantHashIndexChangeLog.migrateCollectionIndex(mongockTemplate);

    var strArgCaptor = ArgumentCaptor.forClass(String.class);
    var classArgCaptor = ArgumentCaptor.forClass(Class.class);
    var idxDefCaptor = ArgumentCaptor.forClass(CompoundIndexDefinition.class);

    verify(mongockTemplate, times(3)).indexOps(classArgCaptor.capture());
    assertThat(classArgCaptor.getAllValues())
        .containsExactly(Claimant.class, Claimant.class, Claimant.class);
    verify(indexOperation).dropIndex(strArgCaptor.capture());
    assertThat(strArgCaptor.getValue()).isEqualTo("email_address_nino_hash_idx");
    verify(indexOperation).ensureIndex(idxDefCaptor.capture());

    var actualIndexDef = idxDefCaptor.getValue();
    assertThat(actualIndexDef.getIndexOptions().get("unique")).isEqualTo(true);
    assertThat(actualIndexDef.getIndexKeys().get("email_address")).isNotNull();
    assertThat(actualIndexDef.getIndexKeys().get("nino")).isNotNull();
  }
}
