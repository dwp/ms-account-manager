package uk.gov.dwp.health.account.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.account.manager.service.SecureHashService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Service
public class SecureSecureHashServiceImpl implements SecureHashService<String, String> {

  private final MessageDigest messageDigest;

  public SecureSecureHashServiceImpl(MessageDigest messageDigest) {
    this.messageDigest = messageDigest;
  }

  public String hash(final String clearString) {
    byte[] hash = messageDigest.digest(clearString.getBytes(StandardCharsets.UTF_8));
    log.debug("Secure hash successful and returning in hex format");
    return new String(Hex.encode(hash), StandardCharsets.UTF_8);
  }
}
