package uk.gov.dwp.health.account.manager.schedule;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.repository.RegistrationRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class RegistrationsCountResetterTest {

  @Mock private RegistrationRepository registrationRepository;
  @InjectMocks private RegistrationsCountResetter registrationsCountResetter;

  @Test
  void when_resetting_registrations_count() {
    registrationsCountResetter.resetRegistrationsCount();

    verify(registrationRepository, times(1)).resetRegistrationCount();
  }
}
