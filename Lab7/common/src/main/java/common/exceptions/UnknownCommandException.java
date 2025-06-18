package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит ввод неизвестной команды.
 *
 * @author Alvas
 * @since 1.0
 */
public class UnknownCommandException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message неизвестное имя команды.
   * @author Alvas
   * @since 1.0
   */
  public UnknownCommandException(String message) {
    super("Неизвестное имя команды: " + message);
  }
}
