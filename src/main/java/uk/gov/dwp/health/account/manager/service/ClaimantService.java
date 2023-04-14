package uk.gov.dwp.health.account.manager.service;

import uk.gov.dwp.health.account.manager.entity.Claimant;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClaimantService<T> {

  List<Claimant> findByNino(String nino);

  Claimant findByRef(String ref);

  Claimant findByEmail(String email);

  Optional<Claimant> findAccountBy(String email, String nino, LocalDate dob);

  Optional<Claimant> findAccountBy(String email);

  String createAccount(T request);

  void setPassword(String objectId, String password);

  void updateClaimant(Claimant claimant);
}
