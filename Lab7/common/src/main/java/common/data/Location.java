package common.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Класс, представляющий местоположение пассажира.
 *
 * <p>Объект этого класса хранит координаты местоположения пассажира в виде двух значений: - {@code
 * x} - координата по оси X (тип {@code Long}). - {@code y} - координата по оси Y (тип {@code
 * Long}). - {@code z} - координата по оси Z (тип {@code Integer}).
 *
 * <p>Значения {@code x}, {@code y}, {@code z} не могут быть {@code null}.
 *
 * @author Alvas
 * @since 1.0
 */
public class Location implements Serializable {
  private Long x; // Поле не может быть null
  private Long y; // Поле не может быть null
  private Integer z; // Поле не может быть null
  @Serial private static final long serialVersionUID = 858592375298357925L;

  /**
   * Конструктор по умолчанию.
   *
   * @author Alvas
   * @since 1.0
   */
  public Location() {}

  /**
   * Конструктор со всеми параметрами.
   *
   * @param x координата местоположения по оси X.
   * @param y координата местоположения по оси Y.
   * @param z координата местоположения по оси Z.
   * @author Alvas
   * @since 1.0
   */
  public Location(Long x, Long y, Integer z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Возвращает значение координаты местоположения по оси X.
   *
   * @return Координата местоположения по оси X.
   * @author Alvas
   * @since 1.0
   */
  public Long getX() {
    return x;
  }

  /**
   * Устанавливает заданное значение координаты местоположения по оси X.
   *
   * @param x координата местоположения по оси X.
   * @author Alvas
   * @since 1.0
   */
  public void setX(Long x) {
    this.x = x;
  }

  /**
   * Возвращает значение координаты местоположения по оси Y.
   *
   * @return Координата местоположения по оси Y.
   * @author Alvas
   * @since 1.0
   */
  public Long getY() {
    return y;
  }

  /**
   * Устанавливает заданное значение координаты местоположения по оси Y.
   *
   * @param y координата местоположения по оси Y.
   * @author Alvas
   * @since 1.0
   */
  public void setY(Long y) {
    this.y = y;
  }

  /**
   * Возвращает значение координаты местоположения по оси Z.
   *
   * @return Координата местоположения по оси Z.
   * @author Alvas
   * @since 1.0
   */
  public Integer getZ() {
    return z;
  }

  /**
   * Устанавливает заданное значение координаты местоположения по оси Z.
   *
   * @param z координата местоположения по оси Z.
   * @author Alvas
   * @since 1.0
   */
  public void setZ(Integer z) {
    this.z = z;
  }

  /**
   * Возвращает строковое представление объекта {@link Location}.
   *
   * @return Строковое представление местоположения.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public String toString() {
    return "Location{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
  }
}
