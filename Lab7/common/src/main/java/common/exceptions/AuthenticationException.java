package common.exceptions;

public class AuthenticationException extends Exception {
  public AuthenticationException(String message) {
    super("Ошибка авторизации: " + message);
  }
}
