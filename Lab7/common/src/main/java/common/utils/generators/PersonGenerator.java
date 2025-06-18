package common.utils.generators;

import common.data.Location;
import common.data.Person;
import common.exceptions.ObjectCreationException;
import common.managers.ScannerManager;
import common.managers.ScriptManager;

/**
 * Класс, отвечающий за запрос необходимых данных от пользователя и генерацию объекта класса {@link
 * Person}.
 *
 * @see Person
 * @author Alvas
 * @since 1.0
 */
public class PersonGenerator extends ObjectGenerator<Person> {
  private final ScriptManager scriptManager;
  private final ScannerManager scannerManager;

  /**
   * Конструктор генератора пассажира.
   *
   * @param scriptManager менеджер выполнения скриптов.
   * @param scannerManager менеджер сканеров.
   * @see ScriptManager
   * @see ScannerManager
   * @author Alvas
   * @since 2.0
   */
  public PersonGenerator(ScriptManager scriptManager, ScannerManager scannerManager) {
    this.scriptManager = scriptManager;
    this.scannerManager = scannerManager;
  }

  /**
   * Генерирует объект класса {@link Person}, запрашивая от пользователя значения полей.
   *
   * @return Объект класса {@link Person}.
   * @see Person
   * @throws ObjectCreationException если происходит ошибка при создании объекта.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public Person create() throws ObjectCreationException {
    String choice =
        askValue(
                "Добавить пассажира? (1 - Да, 2 - Нет): ",
                s ->
                    (s.equals("1")
                        || s.equals("2")
                        || s.equalsIgnoreCase("да")
                        || s.equalsIgnoreCase("нет")),
                s -> s,
                scriptManager,
                scannerManager)
            .toLowerCase();
    return switch (choice) {
      case "1", "да" -> {
        System.out.println("Добро пожаловать в Формирователь пассажира.");
        yield new Person(
            askValue(
                "Рост (float, not null, >0): ",
                x -> (x != null && x > 0),
                Float::parseFloat,
                scriptManager,
                scannerManager),
            askValue(
                "Вес (int, not null, >0): ",
                x -> (x != null && x > 0),
                Integer::parseInt,
                scriptManager,
                scannerManager),
            askValue(
                "Номер паспорта (string, len<=28): ",
                x -> x.length() <= 28,
                s -> s,
                scriptManager,
                scannerManager),
            askLocation());
      }
      case "2", "нет" -> null;
      default -> throw new ObjectCreationException("Некорректный выбор.");
    };
  }

  /**
   * Запрашивает от пользователя поле {@link Location}.
   *
   * @return Объект класса {@link Location}.
   * @see Location
   * @throws ObjectCreationException если происходит ошибка при создании объекта.
   * @author Alvas
   * @since 1.0
   */
  private Location askLocation() throws ObjectCreationException {
    return new LocationGenerator(scriptManager, scannerManager).create();
  }
}
