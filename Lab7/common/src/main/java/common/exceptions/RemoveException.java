package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит ошибка при удалении элемента из коллекции.
 *
 * @author Alvas
 * @since 1.0
 */
public class RemoveException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка при удалении элемента.
   * @author Alvas
   * @since 1.0
   */
  public RemoveException(String message) {
    super("Ошибка при удалении элемента коллекции: " + message);
  }
}
