package common.managers;

import common.data.Ticket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс, отвечающий за взаимодействие с {@code id} объектов класса {@link Ticket}.
 *
 * <p>Хранит список всех использованных {@code id}.
 *
 * <p>Для представления {@code id} используется класс {@link AtomicInteger}.
 *
 * @see Ticket
 * @see AtomicInteger
 * @author Alvas
 * @since 1.0
 */
public final class IdManager {
  private final AtomicInteger counter = new AtomicInteger(1);
  private final Set<Integer> idList = new HashSet<>();

  /**
   * Возвращает свободное значение {@code id} и переходит на следующее свободное значение.
   *
   * <p>Полученное значение записывает в список всех {@code id}.
   *
   * @return Свободное значение {@code id}.
   * @author Alvas
   * @since 1.0
   */
  public int getAndIncrement() {
    return counter.getAndIncrement();
  }

  /**
   * Проверяет значение {@code id} на уникальность.
   *
   * @param id значение для проверки.
   * @return {@code true} - если значение {@code id} еще не использовалось, {@code false} - если уже
   *     использовалось.
   * @author Alvas
   * @since 1.0
   */
  public boolean idIsUnique(int id) {
    return !idList.contains(id);
  }

  /**
   * Добавляет заданное значение {@code id} в общий список и устанавливает счетчик на максимальный
   * из существующих {@code id}.
   *
   * @param id значение для добавления.
   * @author Alvas
   * @since 2.0
   */
  public void addId(int id) {
    idList.add(id);
    counter.getAndUpdate(x -> Math.max(x, id + 1));
  }
}
