package uk.gov.dwp.health.account.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.health.account.manager.entity.Registration;

@Repository
public interface RegistrationRepository
    extends MongoRepository<Registration, String>, RegistrationRepositoryCustom {}
