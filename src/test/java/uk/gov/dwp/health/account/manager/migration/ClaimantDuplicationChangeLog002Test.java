package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import io.changock.driver.api.lock.LockCheckException;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@DataMongoTest
class ClaimantDuplicationChangeLog002Test {

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private ClaimantRepository repository;

  private static ClaimantDuplicationChangeLog002 changeLog002;

  @BeforeAll
  void beforeAll() {
    repository.deleteAll();
    changeLog002 = new ClaimantDuplicationChangeLog002();
  }

  @Test
  void should_find_duplicate_claimant_and_remove_them() {
    createFixture();
    changeLog002.removeDuplicateClaimant(
        new MongockTemplate(mongoTemplate, lockGardInvoker()), repository);
    assertRemaining(2);
  }

  private void assertRemaining(int expected) {
    var actual = new AtomicInteger();
    repository.findAll().forEach(claim -> actual.incrementAndGet());
    assertThat(actual.get()).isEqualTo(expected);
  }

  private void createFixture() {
    var first = Auth.builder().status(STAGE.PENDING.current()).build();
    var second = Auth.builder().status(STAGE.PENDING.current()).build();
    var third = Auth.builder().status(STAGE.PENDING.current()).build();

    var claimant_1 =
        Claimant.builder()
            .emailAddress("test1@dwp.gov.uk")
            .auth(third)
            .build();
    var claimant_2 =
        Claimant.builder()
            .emailAddress("test@dwp.gov.uk")
            .auth(first)
            .build();
    var claimant_3 =
        Claimant.builder()
            .emailAddress("test@dwp.gov.uk")
            .auth(first)
            .build();

    repository.saveAll(List.of(claimant_1, claimant_2, claimant_3));
  }

  private LockGuardInvoker lockGardInvoker() {
    return new LockGuardInvokerImpl(
        new LockManager() {
          @Override
          public void acquireLockDefault() throws LockCheckException {}

          @Override
          public void ensureLockDefault() throws LockCheckException {}

          @Override
          public void releaseLockDefault() {}

          @Override
          public LockManager setLockMaxWaitMillis(long l) {
            return null;
          }

          @Override
          public int getLockMaxTries() {
            return 0;
          }

          @Override
          public LockManager setLockMaxTries(int i) {
            return null;
          }

          @Override
          public LockManager setLockAcquiredForMillis(long l) {
            return null;
          }

          @Override
          public String getOwner() {
            return null;
          }

          @Override
          public boolean isLockHeld() {
            return false;
          }

          @Override
          public void close() {}
        });
  }

  @AfterAll
  void afterAll() {
    repository.deleteAll();
  }

  @Test
  void should_convert_objectId_to_local_date_time() {
    var expected =
        LocalDateTime.parse(
            "2021-04-23T15:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var actual = changeLog002.objectId2LocalDateTime("6082e8251918ec5a77140833");
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void should_sort_claimant_based_on_timestamp() {
    var claimantFirst = Claimant.builder().id("6082e8251918ec5a77140833").build();
    var claimantSecond = Claimant.builder().id("61110cc5b9c9613a59ad78c5").build();
    var list = new ArrayList<Claimant>();
    list.add(claimantFirst);
    list.add(claimantSecond);
    changeLog002.sortByObjectTimeStamp(list);
    assertThat(claimantFirst).isEqualTo(list.get(1));
    assertThat(claimantSecond).isEqualTo(list.get(0));
  }
}
