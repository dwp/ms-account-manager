package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v4.decorator.impl.MongockTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import uk.gov.dwp.health.account.manager.entity.Claimant;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestMethodOrder(value = OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ClaimantDuplicationChangeLog003Test {

  private static ClaimantDuplicationChangeLog003 claimantIndexChangeLog;
  @Mock
  private MongockTemplate mongockTemplate;

  @BeforeEach
  void beforeEach() {
    claimantIndexChangeLog = new ClaimantDuplicationChangeLog003();
  }

  @Test
  void should_drop_index_by_name_and_create_unique_new_index() {
    var indexOperation = mock(IndexOperations.class);
    var indexInfo = mock(IndexInfo.class);
    when(indexInfo.getName()).thenReturn("email_nino_idx");
    when(indexOperation.getIndexInfo()).thenReturn(List.of(indexInfo));
    when(mongockTemplate.indexOps(any(Class.class))).thenReturn(indexOperation);

    claimantIndexChangeLog.migrateCollectionIndex(mongockTemplate);

    var strArgCaptor = ArgumentCaptor.forClass(String.class);
    var classArgCaptor = ArgumentCaptor.forClass(Class.class);
    var idxDefCaptor = ArgumentCaptor.forClass(CompoundIndexDefinition.class);

    verify(mongockTemplate, times(3)).indexOps(classArgCaptor.capture());
    assertThat(classArgCaptor.getAllValues())
        .containsExactly(Claimant.class, Claimant.class, Claimant.class);
    verify(indexOperation).dropIndex(strArgCaptor.capture());
    assertThat(strArgCaptor.getValue()).isEqualTo("email_nino_idx");
    verify(indexOperation).ensureIndex(idxDefCaptor.capture());

    var actualIndexDef = idxDefCaptor.getValue();
    assertThat(actualIndexDef.getIndexOptions().get("unique")).isEqualTo(true);
    assertThat(actualIndexDef.getIndexKeys().get("email_address")).isNotNull();
    assertThat(actualIndexDef.getIndexKeys().get("nino_hash")).isNotNull();
  }
}
