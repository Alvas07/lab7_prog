package common.exceptions;

/**
 * Класс, обеспечивающий исключение, если происходит ошибка при чтении файла.
 *
 * @author Alvas
 * @since 1.0
 */
public class FileReadException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка при чтении файла.
   * @author Alvas
   * @since 1.0
   */
  public FileReadException(String message) {
    super("Ошибка при чтении информации из файла: " + message);
  }
}
