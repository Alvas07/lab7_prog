package common.utils.comparators;

import common.data.Ticket;
import java.util.Comparator;

/**
 * Класс, отвечающий за сравнение двух объектов класса {@link Ticket} по дате создания {@code
 * creationDate}.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class TicketByDateComparator implements Comparator<Ticket> {
  /**
   * Сравнивает два объекта класса {@link Ticket} по значению поля {@code creationDate}.
   *
   * @param ticket1 первый объект класса {@link Ticket} для сравнения.
   * @param ticket2 второй объект класса {@link Ticket} для сравнения.
   * @return Значение компаратора: отрицательное, если меньше; положительное, если больше; нуль,
   *     если равен.
   * @see Ticket
   * @author Alvas
   * @since 1.0
   */
  @Override
  public int compare(Ticket ticket1, Ticket ticket2) {
    return ticket1.getCreationDate().compareTo(ticket2.getCreationDate());
  }
}
