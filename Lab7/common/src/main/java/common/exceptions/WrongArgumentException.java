package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит ошибка при вводе аргументов команды.
 *
 * @author Alvas
 * @since 1.0
 */
public class WrongArgumentException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка с аргументом команды.
   * @author Alvas
   * @since 1.0
   */
  public WrongArgumentException(String message) {
    super("Ошибка с аргументом команды: " + message);
  }
}
