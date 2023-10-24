package uk.gov.dwp.health.account.manager.api.v6;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.service.V6AccountManagerServices;
import uk.gov.dwp.health.account.manager.service.impl.CheckCanApplyServiceImplV6;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class V6AccountManagerApiImplTest {

    @InjectMocks
    private V6AccountManagerApiImpl v6AccountManagerApi;
    @Mock
    private V6AccountManagerServices v6AccountManagerServices;

    @Test
    void when_checking_whether_claimant_can_apply() {
        final var service = mock(CheckCanApplyServiceImplV6.class);
        when(v6AccountManagerServices.getAccountCheckCanApplyV6()).thenReturn(service);
        final String nino = "RN123123A";
        final Boolean checkPipApply = Boolean.FALSE;
        v6AccountManagerApi.v6CanApplyNinoGet("123", nino, checkPipApply);
        final ArgumentCaptor<String> ninoCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Boolean> checkPipApplyCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(service).checkCanApply(ninoCaptor.capture(), checkPipApplyCaptor.capture());
        assertThat(ninoCaptor.getValue()).isEqualTo(nino);
        assertThat(checkPipApplyCaptor.getValue()).isEqualTo(checkPipApply);
    }

}