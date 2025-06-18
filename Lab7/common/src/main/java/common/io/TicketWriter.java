package common.io;

import common.data.Ticket;
import common.exceptions.FileWriteException;
import java.util.List;

/**
 * Базовый интерфейс для реализации классов для записи данных об объектах класса {@link Ticket} в
 * файл.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public interface TicketWriter {
  /**
   * Базовый метод для записи списка объектов класса {@link Ticket} в файл.
   *
   * @param fileName путь к файлу.
   * @param tickets список объектов класса {@link Ticket}.
   * @see Ticket
   * @throws FileWriteException если невозможно записать в файл.
   * @author Alvas
   * @since 1.0
   */
  void writeTicketsToFile(String fileName, List<Ticket> tickets) throws FileWriteException;

  /**
   * Базовый метод, показывающий возможность записи данных в файл.
   *
   * @param fileName путь к файлу.
   * @return {@code true} - если файл доступен для записи, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  boolean canWrite(String fileName);
}
