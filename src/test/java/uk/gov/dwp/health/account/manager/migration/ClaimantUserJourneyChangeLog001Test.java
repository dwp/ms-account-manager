package uk.gov.dwp.health.account.manager.migration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
class ClaimantUserJourneyChangeLog001Test {

  @Autowired private ClaimantRepository repository;
  private static ClaimantUserJourneyChangeLog001 claimantCollectionChangeLog;

  @BeforeAll
  void beforeAll() {
    claimantCollectionChangeLog = new ClaimantUserJourneyChangeLog001();
  }

  @Test
  void should_migrate_all_claimants_and_userJourney_flag_set() {
    ClaimantRepository spiedRepository = spy(repository);
    seedClaimantCollection(1000);
    // verify all claimants have not userJourney flag set
    verifyClaimantsFilterByUserJourneyNull(1000);

    claimantCollectionChangeLog.updateAddUserJourneyFieldAndSetDefaultValue(spiedRepository);

    // verify all claimants have userJourney flag set
    verifyClaimantsFilterByUserJourneyNull(0);
  }

  private void verifyClaimantsFilterByUserJourneyNull(int expected) {
    var all = repository.findAll();
    var actual = (int) all.stream().filter(c -> c.getUserJourney() == null).count();
    assertThat(actual).isEqualTo(expected);
  }

  private void seedClaimantCollection(int size) {
    var claimants = new HashSet<Claimant>();
    IntStream.range(0, size)
        .forEach(
            idx -> {
              var claimant = new Claimant();
              claimant.setSurname(UUID.randomUUID().toString());
              claimant.setForename(UUID.randomUUID().toString());
              claimant.setEmailAddress(UUID.randomUUID().toString());
              claimants.add(claimant);
            });
    repository.saveAll(claimants);
  }

  @AfterAll
  void afterAll() {
    repository.deleteAll();
  }
}
