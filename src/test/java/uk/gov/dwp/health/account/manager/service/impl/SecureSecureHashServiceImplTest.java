package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.health.account.manager.config.SecureHashConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = {SecureHashConfig.class, SecureSecureHashServiceImpl.class})
class SecureSecureHashServiceImplTest {

  @Autowired private SecureSecureHashServiceImpl underTest;

  @Test
  void testHashSameClearStringsEquals() {
    String left = "my_password";
    String right = "my_password";
    assertThat(underTest.hash(left)).isEqualTo(underTest.hash(right));
  }

  @Test
  void testHashDiffClearStringsNotEqauls() {
    String left = "my_password";
    String right = "My_passworD";
    assertThat(underTest.hash(left)).isNotEqualTo(underTest.hash(right));
  }
}
