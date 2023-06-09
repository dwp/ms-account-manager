package uk.gov.dwp.health.account.manager.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.health.account.manager.config.properties.CryptoConfigProperties;
import uk.gov.dwp.health.account.manager.exception.CryptoConfigException;
import uk.gov.dwp.health.crypto.CryptoDataManager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KmsConfigTest {

  @InjectMocks private KmsConfig cut;
  @Mock private CryptoConfigProperties cryptoConfigProperties;

  @Test
  @DisplayName("test create crypto data manager for messaging ")
  void testCreateCryptoDataManagerForMessaging() {
    when(cryptoConfigProperties.getMessageDataKeyId()).thenReturn("event-mock-key-id");
    when(cryptoConfigProperties.isKmsKeyCache()).thenReturn(true);
    when(cryptoConfigProperties.getKmsOverride()).thenReturn("");
    when(cryptoConfigProperties.getRegion()).thenReturn("");
    assertThat(cut.cryptoDataManager()).isNotNull().isExactlyInstanceOf(CryptoDataManager.class);
    verify(cryptoConfigProperties, times(2)).getKmsOverride();
    verify(cryptoConfigProperties, times(2)).getRegion();
  }

  @Test
  @DisplayName("test create crypto data manager for messaging with overrides")
  void testCreateCryptoDataManagerForMessagingWithOverride() {
    when(cryptoConfigProperties.getMessageDataKeyId()).thenReturn("event-mock-key-id");
    when(cryptoConfigProperties.isKmsKeyCache()).thenReturn(true);
    when(cryptoConfigProperties.getKmsOverride()).thenReturn("http://localhost");
    when(cryptoConfigProperties.getRegion()).thenReturn("EU_WEST_2");
    assertThat(cut.cryptoDataManager()).isNotNull().isExactlyInstanceOf(CryptoDataManager.class);
    verify(cryptoConfigProperties, times(3)).getKmsOverride();
    verify(cryptoConfigProperties, times(3)).getRegion();
  }

  @Test
  @DisplayName("test create crypto data manager throws CryptoConfigurationException")
  void testCreateCryptoDataManagerThrowsCryptoConfigurationException() {
    assertThatThrownBy(() -> cut.cryptoDataManager())
        .isInstanceOf(CryptoConfigException.class)
        .hasMessageStartingWith("Failed to config DataCryptoManager for Messaging");
  }
}
