package common.utils;

import common.data.*;
import common.managers.IdManager;

/**
 * Класс, предоставляющий вспомогательные методы для валидации объектов, использующихся в программе.
 *
 * @author Alvas
 * @since 1.0
 */
public class Validator {
  /**
   * Показывает валидность объекта класса {@link Ticket}.
   *
   * @param t объект класса {@link Ticket} для проверки.
   * @param idManager менеджер {@code id}.
   * @return {@code true} - если объект валиден, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public static boolean isValidTicket(Ticket t, IdManager idManager) {
    return idManager.idIsUnique(t.getId())
        && t.getName() != null
        && !t.getName().isEmpty()
        && t.getCoordinates() != null
        && isValidCoordinates(t.getCoordinates())
        && t.getCreationDate() != null
        && t.getPrice() > 0
        && t.getType() != null
        && isValidPerson(t.getPerson());
  }

  /**
   * Показывает валидность объекта класса {@link Coordinates}.
   *
   * @param c объект класса {@link Coordinates} для проверки.
   * @return {@code true} - если объект валиден, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public static boolean isValidCoordinates(Coordinates c) {
    return c.getY() != null && c.getY() <= 332;
  }

  /**
   * Показывает валидность объекта класса {@link Location}.
   *
   * @param l объект класса {@link Location} для проверки.
   * @return {@code true} - если объект валиден, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public static boolean isValidLocation(Location l) {
    return (l == null) || (l.getX() != null && l.getY() != null && l.getZ() != null);
  }

  /**
   * Показывает валидность объекта класса {@link Person}.
   *
   * @param p объект класса {@link Person} для проверки.
   * @return {@code true} - если объект валиден, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public static boolean isValidPerson(Person p) {
    return (p == null)
        || (p.getHeight() != null
            && p.getHeight() > 0
            && p.getWeight() > 0
            && (p.getPassportID() == null || p.getPassportID().length() <= 28)
            && isValidLocation(p.getLocation()));
  }
}
