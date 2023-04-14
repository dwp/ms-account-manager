package uk.gov.dwp.health.account.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Configuration
public class SecureHashConfig {

  @Bean
  public MessageDigest messageDigest(@Value("${security.alg:SHA-512}") final String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm, new BouncyCastleFipsProvider());
    } catch (NoSuchAlgorithmException e) {
      final String msg =
          String.format("Error config security %s not supported %s", algorithm, e.getMessage());
      log.error(msg);
      throw new IllegalStateException(msg);
    }
  }
}
