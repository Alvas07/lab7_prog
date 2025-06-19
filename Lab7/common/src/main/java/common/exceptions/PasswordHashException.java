package common.exceptions;

public class PasswordHashException extends Exception {
  public PasswordHashException(String message) {
    super("Ошибка при хешировании пароля: " + message);
  }
}
