package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит обращение к пустой коллекции.
 *
 * @author Alvas
 * @since 1.0
 */
public class EmptyCollectionException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка при обращении к пустой коллекции.
   * @author Alvas
   * @since 1.0
   */
  public EmptyCollectionException(String message) {
    super("Коллекция пуста: " + message);
  }
}
