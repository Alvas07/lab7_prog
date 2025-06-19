package common.exceptions;

public class RegistrationException extends Exception {
  public RegistrationException(String message) {
    super("Ошибка регистрации: " + message);
  }
}
