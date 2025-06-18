package common.utils;

import java.time.LocalDateTime;

/**
 * Класс, предоставляющий вспомогательные методы для работы с датой и временем.
 *
 * @author Alvas
 * @since 1.0
 */
public class DateTimeUtils {
  private static final LocalDateTime START_TIME = LocalDateTime.now();

  /**
   * Возвращает время начала работы программы.
   *
   * @return Время начала работы программы.
   * @author Alvas
   * @since 1.0
   */
  public static LocalDateTime getStartTime() {
    return START_TIME;
  }

  /**
   * Возвращает текущее время.
   *
   * @return Текущее время.
   * @author Alvas
   * @since 1.0
   */
  public static LocalDateTime getCurrentTime() {
    return LocalDateTime.now();
  }
}
