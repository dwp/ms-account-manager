package uk.gov.dwp.health.account.manager.api.v5;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.service.V5AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.AccountCheckCanApplyV5Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class V5AccountManagerApiImplTest {

  @InjectMocks private V5AccountManagerApiImpl api;
  @Mock private V5AccountManagerServices services;

  @Test
  @DisplayName("test check can apply endpoint")
  void v5CanApplyNinoGet() {
    final AccountCheckCanApplyV5Impl service = mock(AccountCheckCanApplyV5Impl.class);
    when(services.getAccountCheckCanApplyV5()).thenReturn(service);
    final String nino = "RN123123A";
    api.v5CanApplyNinoGet("123", nino);
    final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(service).checkCanApply(captor.capture());
    assertThat(captor.getValue()).isEqualTo(nino);
  }

}
