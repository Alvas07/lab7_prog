package common.io;

import common.data.Ticket;
import common.exceptions.FileReadException;
import java.util.List;

/**
 * Базовый интерфейс для реализации классов для чтения данных об объектах класса {@link Ticket} из
 * файла.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public interface TicketReader {
  /**
   * Базовый метод для получения списка объектов класса {@link Ticket} из файла.
   *
   * @param fileName путь к файлу.
   * @return Список объектов класса {@link Ticket}.
   * @see Ticket
   * @throws FileReadException если невозможно прочитать файл.
   * @author Alvas
   * @since 1.0
   */
  List<Ticket> readTickets(String fileName) throws FileReadException;

  /**
   * Базовый метод, показывающий возможность чтения данных из файла.
   *
   * @param fileName путь к файлу.
   * @return {@code true} - если файл доступен для чтения, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  boolean canRead(String fileName);
}
