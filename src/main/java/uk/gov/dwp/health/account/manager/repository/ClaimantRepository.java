package uk.gov.dwp.health.account.manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.health.account.manager.entity.Claimant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimantRepository extends MongoRepository<Claimant, String> {

  boolean existsByEmailAddressAndNinoAndDateOfBirth(
      final String email, final String nino, final LocalDate dob);

  Optional<Claimant> findByEmailAddressAndNinoAndDateOfBirth(
      final String email, final String nino, final LocalDate dob);

  Optional<Claimant> findByEmailAddress(final String email);

  List<Claimant> findByNino(final String nino);

  Page<Claimant> findAllByUserJourneyNull(Pageable pageable);

  List<Claimant> findAllByEmailAddress(final String email);
}
