package common.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Класс, представляющий координаты билета.
 *
 * <p>Объект этого класса хранит координаты билета в виде двух значений: - {@code x} - координата по
 * оси X (тип {@code float}). - {@code y} - координата по оси Y (тип {@code Long}).
 *
 * <p>Значение {@code x} не может быть {@code null}. Значение {@code y} не может быть {@code null} и
 * должно быть не больше 332.
 *
 * @author Alvas
 * @since 1.0
 */
public class Coordinates implements Serializable {
  private float x;
  private Long y; // Максимальное значение поля: 332, Поле не может быть null
  @Serial private static final long serialVersionUID = 1259015012512509L;

  /**
   * Конструктор по умолчанию.
   *
   * @author Alvas
   * @since 1.0
   */
  public Coordinates() {}

  /**
   * Конструктор со всеми параметрами.
   *
   * @param x координата по оси X.
   * @param y координата по оси Y.
   * @author Alvas
   * @since 1.0
   */
  public Coordinates(float x, Long y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Возвращает значение координаты по оси X.
   *
   * @return Координата по оси X.
   * @author Alvas
   * @since 1.0
   */
  public float getX() {
    return x;
  }

  /**
   * Устанавливает заданное значение координаты по оси X.
   *
   * @param x координата по оси X.
   * @author Alvas
   * @since 1.0
   */
  public void setX(float x) {
    this.x = x;
  }

  /**
   * Возвращает значение координаты по оси Y.
   *
   * @return Координата по оси Y.
   * @author Alvas
   * @since 1.0
   */
  public Long getY() {
    return y;
  }

  /**
   * Устанавливает заданное значение координаты по оси Y.
   *
   * @param y координата по оси Y.
   * @author Alvas
   * @since 1.0
   */
  public void setY(Long y) {
    this.y = y;
  }

  /**
   * Возвращает строковое представление объекта {@link Coordinates}.
   *
   * @return Строковое представление координат.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public String toString() {
    return "Coordinates{" + "x=" + x + ", y=" + y + '}';
  }
}
