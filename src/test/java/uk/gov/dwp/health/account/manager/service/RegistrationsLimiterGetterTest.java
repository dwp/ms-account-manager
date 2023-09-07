package uk.gov.dwp.health.account.manager.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.config.properties.ApplicationProperties;
import uk.gov.dwp.health.account.manager.entity.Registration;
import uk.gov.dwp.health.account.manager.repository.RegistrationRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class RegistrationsLimiterGetterTest {

  @Mock private ApplicationProperties applicationProperties;
  @Mock private RegistrationRepository registrationRepository;
  @InjectMocks private RegistrationsLimiterGetter registrationsLimiterGetter;

  @ParameterizedTest
  @CsvSource({"11, 0", "10, 10", "11, 10"})
  void when_registrations_limit_reached(int count, int limit) {
    when(registrationRepository.findAll())
        .thenReturn(List.of(Registration.builder().count(count).build()));
    when(applicationProperties.getAccountRegistrationsLimit()).thenReturn(limit);

    var registrationsLimiterDto = registrationsLimiterGetter.getRegistrationsLimiter();

    assertThat(registrationsLimiterDto.getLimitReached()).isTrue();
  }

  @Test
  void when_registrations_empty_and_limit_not_set() {
    when(registrationRepository.findAll()).thenReturn(Collections.emptyList());
    when(applicationProperties.getAccountRegistrationsLimit()).thenReturn(0);

    var registrationsLimiterDto = registrationsLimiterGetter.getRegistrationsLimiter();

    assertThat(registrationsLimiterDto.getLimitReached()).isTrue();
  }

  @Test
  void when_registrations_empty() {
    when(registrationRepository.findAll()).thenReturn(Collections.emptyList());
    when(applicationProperties.getAccountRegistrationsLimit()).thenReturn(10);

    var registrationsLimiterDto = registrationsLimiterGetter.getRegistrationsLimiter();

    assertThat(registrationsLimiterDto.getLimitReached()).isFalse();
  }

  @Test
  void when_registrations_zero() {
    when(registrationRepository.findAll())
        .thenReturn(List.of(Registration.builder().count(0).build()));
    when(applicationProperties.getAccountRegistrationsLimit()).thenReturn(10);

    var registrationsLimiterDto = registrationsLimiterGetter.getRegistrationsLimiter();

    assertThat(registrationsLimiterDto.getLimitReached()).isFalse();
  }

  @Test
  void when_account_limit_not_reached() {
    when(registrationRepository.findAll())
        .thenReturn(List.of(Registration.builder().count(9).build()));
    when(applicationProperties.getAccountRegistrationsLimit()).thenReturn(10);

    var registrationsLimiterDto = registrationsLimiterGetter.getRegistrationsLimiter();

    assertThat(registrationsLimiterDto.getLimitReached()).isFalse();
  }
}
