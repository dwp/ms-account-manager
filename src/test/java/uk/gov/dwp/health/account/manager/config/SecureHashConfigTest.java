package uk.gov.dwp.health.account.manager.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = SecureHashConfig.class)
class SecureHashConfigTest {

  @Autowired private MessageDigest instance;

  @Test
  void testMsgDigestWithDefaultAlgBouncyCastleProviderCreated() {
    assertThat(instance.getAlgorithm()).isEqualTo("SHA-512");
  }

  @Test
  void testMsgDigestWithCustomAlgBouncyCastleProviderCreated() {
    SecureHashConfig methodInstance = new SecureHashConfig();
    MessageDigest actual = methodInstance.messageDigest("SHA-256");

    assertThat(actual.getAlgorithm()).isEqualTo("SHA-256");
    assertThat(actual.getProvider().getName()).isEqualTo("BCFIPS");
  }

  @Test
  void testUnsupportedAlgIllegalStateExceptionThrow() {
    SecureHashConfig methodInstance = new SecureHashConfig();
    assertThatThrownBy(() -> methodInstance.messageDigest("bad-alg"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageStartingWith("Error config security bad-alg");
  }
}
