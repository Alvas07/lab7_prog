package common.data;

import common.utils.comparators.TicketByDateComparator;
import common.utils.comparators.TicketComparator;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Класс, представляющий билет.
 *
 * <p>Объект этого класса хранит информацию о билете: - {@code id} - уникальный идентификатор билета
 * (тип {@code int}). - {@code name} - наименование билета (тип {@code String}). - {@code
 * coordinates} - координаты билета (тип {@link Coordinates}). - {@code creationDate} - дата
 * создания билета (тип {@code LocalDate}). - {@code price} - стоимость билета (тип {@code float}).
 * - {@code type} - тип билета (тип {@link TicketType}). - {@code person} - пассажир билета (тип
 * {@link Person}).
 *
 * <p>Значение {@code id} не может быть {@code null}, должно быть уникальным, больше 0 и
 * генерироваться автоматически. Значение {@code name} не может быть {@code null} и пустым. Значение
 * {@code coordinates} не может быть {@code null}. Значение {@code creationDate} не может быть
 * {@code null} и должно генерироваться автоматически. Значение {@code price} не может быть {@code
 * null} и должно быть больше 0. Значение {@code type} не может быть {@code null}.
 *
 * @author Alvas
 * @since 1.0
 */
public class Ticket implements Comparable<Ticket>, Serializable {
  private int id; // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным,
  // Значение этого поля должно генерироваться автоматически
  private String name; // Поле не может быть null, Строка не может быть пустой
  private Coordinates coordinates; // Поле не может быть null
  private LocalDate
      creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
  // автоматически
  private float price; // Значение поля должно быть больше 0
  private TicketType type; // Поле не может быть null
  private Person person; // Поле может быть null
  @Serial private static final long serialVersionUID = 23125235290852352L;

  /**
   * Конструктор по умолчанию.
   *
   * @author Alvas
   * @since 1.0
   */
  public Ticket() {}

  /**
   * Конструктор со всеми параметрами.
   *
   * @param id уникальный идентификатор.
   * @param name наименование.
   * @param coordinates координаты.
   * @param creationDate дата создания.
   * @param price стоимость.
   * @param type тип.
   * @param person пассажир.
   * @author Alvas
   * @since 1.0
   */
  public Ticket(
      int id,
      String name,
      Coordinates coordinates,
      LocalDate creationDate,
      float price,
      TicketType type,
      Person person) {
    this.id = id;
    this.name = name;
    this.coordinates = coordinates;
    this.creationDate = creationDate;
    this.price = price;
    this.type = type;
    this.person = person;
  }

  /**
   * Возвращает уникальный идентификатор билета.
   *
   * @return Уникальный идентификатор.
   * @author Alvas
   * @since 1.0
   */
  public int getId() {
    return id;
  }

  /**
   * Устанавливает заданный уникальный идентификатор билета.
   *
   * @param id уникальный идентификатор.
   * @author Alvas
   * @since 1.0
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Возвращает наименование билета.
   *
   * @return Наименование.
   * @author Alvas
   * @since 1.0
   */
  public String getName() {
    return name;
  }

  /**
   * Устанавливает заданное наименование билета.
   *
   * @param name наименование.
   * @author Alvas
   * @since 1.0
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Возвращает координаты билета.
   *
   * @return Координаты.
   * @author Alvas
   * @since 1.0
   */
  public Coordinates getCoordinates() {
    return coordinates;
  }

  /**
   * Устанавливает заданные координаты билета.
   *
   * @param coordinates координаты.
   * @author Alvas
   * @since 1.0
   */
  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  /**
   * Возвращает дату создания билета.
   *
   * @return Дата создания.
   * @author Alvas
   * @since 1.0
   */
  public LocalDate getCreationDate() {
    return creationDate;
  }

  /**
   * Устанавливает заданную дату создания билета.
   *
   * @param creationDate дата создания.
   * @author Alvas
   * @since 1.0
   */
  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Возвращает стоимость билета.
   *
   * @return Стоимость.
   * @author Alvas
   * @since 1.0
   */
  public float getPrice() {
    return price;
  }

  /**
   * Устанавливает заданную стоимость билета.
   *
   * @param price стоимость.
   * @author Alvas
   * @since 1.0
   */
  public void setPrice(float price) {
    this.price = price;
  }

  /**
   * Возвращает тип билета.
   *
   * @return Тип.
   * @author Alvas
   * @since 1.0
   */
  public TicketType getType() {
    return type;
  }

  /**
   * Устанавливает заданный тип билета.
   *
   * @param type тип.
   * @author Alvas
   * @since 1.0
   */
  public void setType(TicketType type) {
    this.type = type;
  }

  /**
   * Возвращает пассажира билета.
   *
   * @return Пассажир.
   * @author Alvas
   * @since 1.0
   */
  public Person getPerson() {
    return person;
  }

  /**
   * Устанавливает заданного пассажира билета.
   *
   * @param person пассажир.
   * @author Alvas
   * @since 1.0
   */
  public void setPerson(Person person) {
    this.person = person;
  }

  /**
   * Сравнивает билет с другим по дате создания.
   *
   * @param other билет для сравнения.
   * @author Alvas
   * @since 1.0
   */
  public int compareToByDate(Ticket other) {
    return new TicketByDateComparator().compare(this, other);
  }

  /**
   * Сравнивает билет с другим по умолчанию.
   *
   * @param other билет для сравнения.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public int compareTo(Ticket other) {
    return new TicketComparator().compare(this, other);
  }

  /**
   * Возвращает строковое представление объекта {@link Ticket}.
   *
   * @return Строковое представление билета.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public String toString() {
    return "Ticket{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", coordinates="
        + coordinates
        + ", creationDate="
        + creationDate
        + ", price="
        + price
        + ", type="
        + type
        + ", person="
        + person
        + '}';
  }
}
