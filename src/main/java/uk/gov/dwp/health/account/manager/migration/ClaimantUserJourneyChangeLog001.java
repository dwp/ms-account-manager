package uk.gov.dwp.health.account.manager.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.UserJourney;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;

import java.util.ArrayList;

@ChangeLog(order = "001")
@Slf4j
public final class ClaimantUserJourneyChangeLog001 {

  @ChangeSet(
      order = "001",
      author = "PIP-apply ms-account-manager",
      id = "updateAddUserJourneyFieldSetDefaultValue")
  public void updateAddUserJourneyFieldAndSetDefaultValue(
      @Autowired ClaimantRepository repository) {
    int totalCount = 0;
    log.info("Execute migration change set 001");
    Pageable pageable = PageRequest.of(0, 500);
    var page = repository.findAllByUserJourneyNull(pageable);
    while (!page.isEmpty()) {
      pageable = page.getPageable();
      var updated = new ArrayList<Claimant>();
      page.forEach(
          claimant -> {
            claimant.setUserJourney(UserJourney.TACTICAL);
            updated.add(claimant);
          });
      repository.saveAll(updated);
      totalCount += updated.size();
      page = repository.findAllByUserJourneyNull(pageable);
    }
    log.info("Complete migration change set 001 successfully total {}", totalCount);
  }
}
