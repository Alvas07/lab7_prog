package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит ошибка при исполнении команды.
 *
 * @author Alvas
 * @since 1.0
 */
public class CommandExecuteException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка при исполнении команды.
   * @author Alvas
   * @since 1.0
   */
  public CommandExecuteException(String message) {
    super("Ошибка при исполнении команды: " + message);
  }
}
