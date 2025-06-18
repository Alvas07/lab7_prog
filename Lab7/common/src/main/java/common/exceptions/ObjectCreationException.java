package common.exceptions;

import common.data.Ticket;

/**
 * Класс, обеспечивающий исключение, если происходит ошибка при создании объекта класса {@link
 * Ticket}.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class ObjectCreationException extends Exception {
  /**
   * Конструктор исключения с поясняющим сообщением.
   *
   * @param message ошибка при создании объекта.
   * @author Alvas
   * @since 1.0
   */
  public ObjectCreationException(String message) {
    super("Ошибка при создании объекта: " + message);
  }
}
