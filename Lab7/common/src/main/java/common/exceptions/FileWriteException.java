package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит ошибка при записи в файл.
 *
 * @author Alvas
 * @since 1.0
 */
public class FileWriteException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка при записи в файл.
   * @author Alvas
   * @since 1.0
   */
  public FileWriteException(String message) {
    super("Ошибка при записи информации в файл: " + message);
  }
}
