package common.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Класс, представляющий пассажира.
 *
 * <p>Объект этого класса хранит информацию о пассажире: - {@code height} - рост пассажира (тип
 * {@code Float}). - {@code weight} - вес пассажира (тип {@code int}). - {@code passportID} - номер
 * паспорта пассажира (тип {@code String}). - {@code location} - местоположение пассажира (тип
 * {@link Location})
 *
 * <p>Значение {@code height} не может быть {@code null} и должно быть больше 0. Значение {@code
 * weight} не может быть {@code null} и должно быть больше 0. Длина {@code passportID} должна быть
 * не более 28 символов.
 *
 * @author Alvas
 * @since 1.0
 */
public class Person implements Serializable {
  private Float height; // Поле не может быть null, Значение поля должно быть больше 0
  private int weight; // Значение поля должно быть больше 0
  private String passportID; // Длина строки не должна быть больше 28, Поле может быть null
  private Location location; // Поле может быть null
  @Serial private static final long serialVersionUID = 9893759327985352L;

  /**
   * Конструктор по умолчанию.
   *
   * @author Alvas
   * @since 1.0
   */
  public Person() {}

  /**
   * Конструктор со всеми параметрами.
   *
   * @param height рост пассажира.
   * @param weight вес пассажира.
   * @param passportID номер паспорта пассажира.
   * @param location местоположение пассажира.
   * @author Alvas
   * @since 1.0
   */
  public Person(Float height, int weight, String passportID, Location location) {
    this.height = height;
    this.weight = weight;
    this.passportID = passportID;
    this.location = location;
  }

  /**
   * Возвращает значение роста пассажира.
   *
   * @return Рост пассажира.
   * @author Alvas
   * @since 1.0
   */
  public Float getHeight() {
    return height;
  }

  /**
   * Устанавливает заданное значение роста пассажира.
   *
   * @param height рост пассажира.
   * @author Alvas
   * @since 1.0
   */
  public void setHeight(Float height) {
    this.height = height;
  }

  /**
   * Возвращает значение веса пассажира.
   *
   * @return Вес пассажира.
   * @author Alvas
   * @since 1.0
   */
  public int getWeight() {
    return weight;
  }

  /**
   * Устанавливает заданное значение веса пассажира.
   *
   * @param weight вес пассажира.
   * @author Alvas
   * @since 1.0
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

  /**
   * Возвращает номер паспорта пассажира.
   *
   * @return Номер паспорта пассажира.
   * @author Alvas
   * @since 1.0
   */
  public String getPassportID() {
    return passportID;
  }

  /**
   * Устанавливает заданный номер паспорта пассажира.
   *
   * @param passportID номер паспорта пассажира.
   * @author Alvas
   * @since 1.0
   */
  public void setPassportID(String passportID) {
    this.passportID = passportID;
  }

  /**
   * Возвращает местоположение пассажира.
   *
   * @return Местоположение пассажира.
   * @author Alvas
   * @since 1.0
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Устанавливает заданное местоположение пассажира.
   *
   * @param location местоположение пассажира.
   * @author Alvas
   * @since 1.0
   */
  public void setLocation(Location location) {
    this.location = location;
  }

  /**
   * Возвращает строковое представление объекта {@link Person}.
   *
   * @return Строковое представление пассажира.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public String toString() {
    return "Person{"
        + "height="
        + height
        + ", weight="
        + weight
        + ", passportID='"
        + passportID
        + '\''
        + ", location="
        + location
        + '}';
  }
}
