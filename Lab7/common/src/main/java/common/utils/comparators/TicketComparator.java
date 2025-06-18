package common.utils.comparators;

import common.data.Ticket;
import java.util.Comparator;

/**
 * Класс, отвечающий за сравнение двух объектов класса {@link Ticket} по умолчанию.
 *
 * <p>Производит сравнение последовательно по всем полям объекта, не учитывает {@code id} и {@code
 * creationDate}.
 *
 * <p>Значение {@code null} считается меньше любого другого.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public final class TicketComparator implements Comparator<Ticket> {
  /**
   * Сравнивает два объекта класса {@link Ticket} по умолчанию.
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
    int nameCompare = ticket1.getName().compareTo(ticket2.getName());
    if (nameCompare != 0) return nameCompare;

    int coordinatesXCompare =
        Float.compare(ticket1.getCoordinates().getX(), ticket2.getCoordinates().getX());
    if (coordinatesXCompare != 0) return coordinatesXCompare;

    int coordinatesYCompare =
        Long.compare(ticket1.getCoordinates().getY(), ticket2.getCoordinates().getY());
    if (coordinatesYCompare != 0) return coordinatesYCompare;

    int priceCompare = Float.compare(ticket1.getPrice(), ticket2.getPrice());
    if (priceCompare != 0) return priceCompare;

    int ticketTypeCompare = ticket1.getType().compareTo(ticket2.getType());
    if (ticketTypeCompare != 0) return ticketTypeCompare;

    if (ticket1.getPerson() == null && ticket2.getPerson() != null) return -1;
    if (ticket1.getPerson() != null && ticket2.getPerson() == null) return 1;
    if (ticket1.getPerson() != null && ticket2.getPerson() != null) {
      int heightCompare =
          Float.compare(ticket1.getPerson().getHeight(), ticket2.getPerson().getHeight());
      if (heightCompare != 0) return heightCompare;

      int weightCompare =
          Integer.compare(ticket1.getPerson().getWeight(), ticket2.getPerson().getWeight());
      if (weightCompare != 0) return weightCompare;

      if (ticket1.getPerson().getPassportID() == null
          && ticket2.getPerson().getPassportID() != null) return -1;
      if (ticket1.getPerson().getPassportID() != null
          && ticket2.getPerson().getPassportID() == null) return 1;
      if (ticket1.getPerson().getPassportID() != null
          && ticket2.getPerson().getPassportID() != null) {
        int passportIDCompare =
            ticket1.getPerson().getPassportID().compareTo(ticket2.getPerson().getPassportID());
        if (passportIDCompare != 0) return passportIDCompare;
      }

      if (ticket1.getPerson().getLocation() == null && ticket2.getPerson().getLocation() != null)
        return -1;
      if (ticket1.getPerson().getLocation() != null && ticket2.getPerson().getLocation() == null)
        return 1;
      if (ticket1.getPerson().getLocation() != null && ticket2.getPerson().getLocation() != null) {
        int locationXCompare =
            Long.compare(
                ticket1.getPerson().getLocation().getX(), ticket2.getPerson().getLocation().getX());
        if (locationXCompare != 0) return locationXCompare;

        int locationYCompare =
            Long.compare(
                ticket1.getPerson().getLocation().getY(), ticket2.getPerson().getLocation().getY());
        if (locationYCompare != 0) return locationYCompare;

        int locationZCompare =
            Integer.compare(
                ticket1.getPerson().getLocation().getZ(), ticket2.getPerson().getLocation().getZ());
        if (locationZCompare != 0) return locationZCompare;
      }
    }

    return 0;
  }
}
