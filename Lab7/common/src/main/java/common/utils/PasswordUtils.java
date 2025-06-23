package common.utils;

import common.exceptions.PasswordHashException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
  public static String hashPassword(String password, String salt) throws PasswordHashException {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] hash = messageDigest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
      throw new PasswordHashException(e.getMessage());
    }
  }

  public static String generateSalt(int length) {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[length];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }
}
