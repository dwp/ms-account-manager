package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.account.manager.service.ClaimantService;
import uk.gov.dwp.health.account.manager.utils.InputValidator;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClaimantServiceImpl implements ClaimantService<NewAccountRequest> {

  private final ClaimantRepository repository;
  private final SecureSecureHashServiceImpl secureHashServiceImpl;
  private final WatcherConfigProperties watcherConfig;

  public ClaimantServiceImpl(
      ClaimantRepository repository,
      SecureSecureHashServiceImpl secureHashServiceImpl,
      WatcherConfigProperties watcherConfig) {
    this.repository = repository;
    this.secureHashServiceImpl = secureHashServiceImpl;
    this.watcherConfig = watcherConfig;
  }

  @Override
  public List<Claimant> findByNino(String nino) {
    return repository.findByNino(InputValidator.normaliseInputUpper(nino));
  }

  @Override
  public Claimant findByRef(final String ref) {
    return repository
        .findById(ref)
        .orElseThrow(
            () ->
                new AccountNotFoundException(
                    String.format("Account with ref %s does not exist", ref)));
  }

  @Override
  public Claimant findByEmail(String email) {
    return repository.findByEmailAddress(email).orElse(null);
  }

  @Override
  public Optional<Claimant> findAccountBy(
      final String email, final String nino, final LocalDate dob) {
    return repository.findByEmailAddressAndNinoAndDateOfBirth(
        email.strip().toLowerCase(),
        InputValidator.normaliseInputUpper(nino),
        dob);
  }

  @Override
  public Optional<Claimant> findAccountBy(final String email) {
    return repository.findByEmailAddress(InputValidator.normaliseInputLower(email));
  }

  @Override
  public String createAccount(final NewAccountRequest request) {
    if (findAccountBy(request.getEmail(), request.getNino(), request.getDob()).isPresent()) {
      log.warn("Account {} already exist", request.getEmail());
      throw new AccountExistException("Account creation fail, account already exist");
    } else {
      final LocalDate dob = request.getDob();
      final String nino = InputValidator.normaliseInputUpper(request.getNino());
      Claimant claimant =
          Claimant.builder()
              .emailAddress(request.getEmail().strip().toLowerCase())
              .enableCap(false)
              .surname(request.getSurname())
              .forename(request.getForename())
              .postcode(request.getPostcode())
              .nino(nino)
              .mobileNumber(request.getMobilePhone())
              .dateOfBirth(dob)
              .language(request.getLanguage().name())
              .build();
      claimant.setRegion(claimant.getPostcode().startsWith("BT") ? Region.NI : Region.GB);
      watcherConfig.setChangeStreamChannel(claimant, "account");
      claimant = repository.save(claimant);
      return claimant.getId();
    }
  }

  @Override
  public void setPassword(final String objectId, final String password) {
    Optional<Claimant> claimant = repository.findById(objectId);
    if (claimant.isPresent()) {
      Claimant account = claimant.get();
      if (account.getAuth() == null) {
        Auth auth =
            Auth.builder()
                .password(secureHashServiceImpl.hash(password))
                .status(STAGE.FIRST.current())
                .build();
        account.setAuth(auth);
      } else {
        Auth auth = account.getAuth();
        auth.setPassword(secureHashServiceImpl.hash(password));
        if (auth.getStatus() == STAGE.LOCKED.current()) {
          auth.setStatus(STAGE.SECONDPLUS.current());
          auth.setFailureCounter(0);
        }
      }
      updateClaimant(account);
    } else {
      log.warn("ALERT {} account not found but set password invoked", objectId);
    }
  }

  @Override
  public void updateClaimant(Claimant claimant) {
    watcherConfig.setChangeStreamChannel(claimant, "account");
    try {
      repository.save(claimant);
    } catch (DuplicateKeyException ex) {
      log.warn("A duplicate account creation attempted {}", ex.getMessage());
      throw new AccountExistException("Duplicate account creation attempted");
    }
  }
}
