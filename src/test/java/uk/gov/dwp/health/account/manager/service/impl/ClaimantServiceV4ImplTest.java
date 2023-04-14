package uk.gov.dwp.health.account.manager.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import support.TestFixtures;
import uk.gov.dwp.health.account.manager.constant.STAGE;
import uk.gov.dwp.health.account.manager.entity.Auth;
import uk.gov.dwp.health.account.manager.entity.Claimant;
import uk.gov.dwp.health.account.manager.entity.FailedAttempt;
import uk.gov.dwp.health.account.manager.entity.Region;
import uk.gov.dwp.health.account.manager.exception.AccountExistException;
import uk.gov.dwp.health.account.manager.exception.AccountNotFoundException;
import uk.gov.dwp.health.account.manager.openapi.model.NewAccountRequest;
import uk.gov.dwp.health.account.manager.openapi.model.V4NewAccountRequest;
import uk.gov.dwp.health.account.manager.repository.ClaimantRepository;
import uk.gov.dwp.health.mongo.changestream.config.properties.Channel;
import uk.gov.dwp.health.mongo.changestream.config.properties.WatcherConfigProperties;
import uk.gov.dwp.health.mongo.changestream.extension.MongoChangeStreamIdentifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimantServiceV4ImplTest {

  @InjectMocks private ClaimantServiceV4Impl underTest;
  @Mock private ClaimantRepository repository;
  @Mock private SecureSecureHashServiceImpl secureHashService;
  @Mock private WatcherConfigProperties watcherConfig;
  @Captor private ArgumentCaptor<Claimant> claimantArgCaptor;

  @Test
  void testNormalizeInputWhenFindingAccountDetails() {
    given(repository.findByEmailAddressAndNinoAndDateOfBirth(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.empty());
    Optional<Claimant> actual =
        underTest.findAccountBy("citizen@dwp.gov.uk", "rn000002b", LocalDate.of(1993, 8, 10));
    assertThat(actual).isEmpty();
    verify(repository)
        .findByEmailAddressAndNinoAndDateOfBirth(
            "citizen@dwp.gov.uk", "RN000002B", LocalDate.of(1993, 8, 10));
  }

  @Test
  void testCreateAccountWithGivenDetails() {
    var savedClaimant =
        Claimant.builder()
            .id("123456")
            .emailAddress("citizen@dwp.gov.uk")
            .mobileNumber("0777777778")
            .surname("last name")
            .forename("first name")
            .postcode("LS1 1XX")
            .nino("RN000002B")
            .dateOfBirth(LocalDate.of(1990, 9, 10))
            .language("EN")
            .region(Region.GB)
            .build();
    given(repository.findByEmailAddressAndNinoAndDateOfBirth(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.empty());

    when(repository.save(any(Claimant.class))).thenReturn(savedClaimant);
    doNothing().when(watcherConfig).setChangeStreamChannel(any(MongoChangeStreamIdentifier.class), anyString());

    var request = new V4NewAccountRequest();
    request.setEmail(" citizen@dwp.gov.uk ");
    request.setNino("rn 00 00 02 b");
    request.setDob(LocalDate.of(1990, 9, 10));
    request.setSurname("last name");
    request.setForename("first name");
    request.setMobilePhone("0777777778");
    request.setPostcode("LS1 1XX");
    request.setLanguage(V4NewAccountRequest.LanguageEnum.EN);
    request.setResearchContact(V4NewAccountRequest.ResearchContactEnum.YES);

    var actual = underTest.createAccount(request);
    assertThat(actual).isEqualTo(savedClaimant.getId());
    verify(repository)
        .findByEmailAddressAndNinoAndDateOfBirth(
            "citizen@dwp.gov.uk", "RN000002B", LocalDate.of(1990, 9, 10));
    verify(repository).save(claimantArgCaptor.capture());
    assertThat(claimantArgCaptor.getValue())
        .isEqualToComparingOnlyGivenFields(
            savedClaimant,
            "emailAddress",
            "surname",
            "forename",
            "postcode",
            "nino",
            "mobileNumber",
            "dateOfBirth",
            "language",
            "region");
  }

  @Test
  void testCreateAccountFailOnAccountExist() {
    var request = new V4NewAccountRequest();
    request.setEmail(" citizen@dwp.gov.uk ");
    request.setNino("rn 00 00 02 b");
    request.setDob(LocalDate.of(1990, 9, 10));
    request.setSurname("last name");
    request.setForename("first name");
    request.setMobilePhone("0777777778");
    request.setLanguage(V4NewAccountRequest.LanguageEnum.EN);

    given(repository.findByEmailAddressAndNinoAndDateOfBirth(anyString(), anyString(), any(LocalDate.class)))
        .willReturn(Optional.of(Claimant.builder().build()));
    assertThrows(AccountExistException.class, () -> underTest.createAccount(request));
    verify(repository, never()).save(any(Claimant.class));
    verify(repository)
        .findByEmailAddressAndNinoAndDateOfBirth(
            "citizen@dwp.gov.uk", "RN000002B", LocalDate.of(1990, 9, 10));
  }

  @Test
  void testSetPasswordWhenAccountExistFor1stTime() {
    given(repository.findById(anyString())).willReturn(Optional.of(Claimant.builder().build()));
    given(secureHashService.hash(anyString())).willReturn("hashed_password");

    underTest.setPassword("123456", "password_in_clear");

    verify(repository).save(claimantArgCaptor.capture());
    var actual = claimantArgCaptor.getValue().getAuth();

    assertThat(actual.getStatus()).isEqualTo(STAGE.FIRST.current());
    assertThat(actual.getPassword()).isEqualTo("hashed_password");
    assertThat(actual.getFailedAttempts()).isNull();
    verify(repository).findById("123456");
    verify(secureHashService).hash("password_in_clear");
  }

  @Test
  void testResetPasswordWhenAccountLockedFailAttemptReset() {
    var auth = Auth.builder().status(STAGE.LOCKED.current()).password("old_hashed_psw").build();
    auth.addFailure(new FailedAttempt());
    auth.addFailure(new FailedAttempt());
    auth.addFailure(new FailedAttempt());
    var spiedAuth = spy(auth);
    given(repository.findById(anyString()))
        .willReturn(Optional.of(Claimant.builder().auth(spiedAuth).build()));
    given(secureHashService.hash(anyString())).willReturn("hashed_password");
    underTest.setPassword("123456", "password_in_clear");
    verify(repository).save(claimantArgCaptor.capture());
    Auth actual = claimantArgCaptor.getValue().getAuth();
    assertThat(actual.getStatus()).isEqualTo(STAGE.SECONDPLUS.current());
    assertThat(actual.getPassword()).isEqualTo("hashed_password");
    verify(repository).findById("123456");
    verify(secureHashService).hash("password_in_clear");
    verify(spiedAuth).setFailureCounter(0);
  }

  @Test
  void testSetPasswordWhenAccountNotExist() {
    given(repository.findById(anyString())).willReturn(Optional.empty());
    underTest.setPassword("123456", "password_in_clear");
    verify(repository, never()).save(any(Claimant.class));
    verify(repository).findById("123456");
  }

  @Test
  void testFindAccountByRef() {
    given(repository.findById(anyString()))
        .willReturn(
            Optional.of(
                Claimant.builder()
                    .id("123456")
                    .enableCap(true)
                    .surname("last name")
                    .forename("first name")
                    .postcode("ls1 7xx")
                    .build()));
    var actual = underTest.findByRef("123456");
    assertThat(actual.getId()).isEqualTo("123456");
    assertThat(actual.getSurname()).isEqualTo("Last Name");
    assertThat(actual.getForename()).isEqualTo("First Name");
    assertThat(actual.getPostcode()).isEqualTo("LS1 7XX");
    verify(repository).findById("123456");
  }

  @Test
  @DisplayName("test find account by nino")
  void testFindAccountByNino() {
    given(repository.findByNino("123456"))
        .willReturn(
            List.of(
                Claimant.builder()
                    .id("123456")
                    .enableCap(true)
                    .surname("last name")
                    .forename("first name")
                    .postcode("ls1 7xx")
                    .build()));
    var actual = underTest.findByNino("123456").get(0);
    assertThat(actual.getId()).isEqualTo("123456");
    assertThat(actual.getSurname()).isEqualTo("Last Name");
    assertThat(actual.getForename()).isEqualTo("First Name");
    assertThat(actual.getPostcode()).isEqualTo("LS1 7XX");
    verify(repository).findByNino("123456");
  }

  @Test
  void testNotFindAccountByRefThrowsNotFoundException() {
    given(repository.findById(anyString())).willReturn(Optional.empty());
    assertThrows(AccountNotFoundException.class, () -> underTest.findByRef("123456"));
  }

  @Test
  @DisplayName("test not found account by nino throws not found exception")
  void testNotFoundAccountByNinoThrowsNotFoundException() {
    given(repository.findByNino(anyString())).willReturn(Collections.emptyList());
    assertThat(underTest.findByNino("nino")).isEmpty();
  }

  @Test
  void testFindAccountByEmailAndPassword() {
    given(repository.findByEmailAddress(anyString())).willReturn(Optional.empty());
    underTest.findAccountBy(TestFixtures.EMAIL);
    verify(repository).findByEmailAddress(TestFixtures.EMAIL);
  }

  @Test
  @DisplayName("test should return null with given email")
  void testShouldReturnNullWithGivenEmail() {
    when(repository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
    assertThat(underTest.findByEmail(TestFixtures.EMAIL)).isNull();
    verify(repository).findByEmailAddress(TestFixtures.EMAIL);
  }

  @Test
  @DisplayName("test should return an account with given email")
  void testShouldReturnAnAccountWithGivenEmail() {
    var claimant = new Claimant();
    claimant.setForename(TestFixtures.FORENAME);
    claimant.setSurname(TestFixtures.SURNAME);
    when(repository.findByEmailAddress(anyString())).thenReturn(Optional.of(claimant));
    assertThat(underTest.findByEmail(TestFixtures.EMAIL)).isEqualTo(claimant);
    verify(repository).findByEmailAddress(TestFixtures.EMAIL);
  }

  @Test
  @DisplayName("test update claimant and changestram channel is set")
  void testUpdateClaimantAndChangeStreamChannelIsSet() {
    var claimant =
        Claimant.builder()
            .id("123456")
            .emailAddress("citizen@dwp.gov.uk")
            .mobileNumber("0777777778")
            .surname("last name")
            .forename("first name")
            .postcode("SW1H 9NA")
            .nino("RN000002B")
            .dateOfBirth(LocalDate.of(1990, 9, 10))
            .language("EN")
            .build();
    var channel = new Channel();
    doAnswer(invocation -> {
      Object[] args = invocation.getArguments();
      ((Claimant)args[0]).setChannel(channel);
      return null; // void method in a block-style lambda, so return null
    }).when(watcherConfig).setChangeStreamChannel(claimant, "account");
    underTest.updateClaimant(claimant);
    assertThat(claimant.getInstanceId()).isNotNull();
    assertThat(claimant.getInstanceId()).isEqualTo(channel.getInstanceId());
  }

  @Test
  @DisplayName("test update claimant no channel information found throws runTimeException")
  void testUpdateClaimantNoChannelInformationFoundThrowsRunTimeException() {
    var claimant = Claimant.builder().build();
    doThrow(new RuntimeException()).when(watcherConfig).setChangeStreamChannel(any(MongoChangeStreamIdentifier.class), anyString());
    assertThrows(RuntimeException.class, () -> underTest.updateClaimant(claimant));
    verify(repository, never()).save(claimant);
  }

  @Test
  void should_log_duplicate_claimant_creation_message() {
    doNothing().when(watcherConfig).setChangeStreamChannel(any(MongoChangeStreamIdentifier.class), anyString());
    doThrow(DuplicateKeyException.class).when(repository).save(any(Claimant.class));
    assertThatThrownBy(() -> underTest.updateClaimant(Claimant.builder().build()))
        .isInstanceOf(AccountExistException.class)
        .hasMessage("Duplicate account creation attempted");
  }
}
