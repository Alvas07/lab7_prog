package common.utils.generators;

import common.data.Location;
import common.exceptions.ObjectCreationException;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import java.util.Objects;

/**
 * Класс, отвечающий за запрос необходимых данных от пользователя и генерацию объекта класса {@link
 * Location}.
 *
 * @see Location
 * @author Alvas
 * @since 1.0
 */
public class LocationGenerator extends ObjectGenerator<Location> {
  private final ScriptManager scriptManager;
  private final ScannerManager scannerManager;

  /**
   * Конструктор генератора местоположения.
   *
   * @param scriptManager менеджер выполнения скриптов.
   * @param scannerManager менеджер сканеров.
   * @see ScriptManager
   * @see ScannerManager
   * @author Alvas
   * @since 2.0
   */
  public LocationGenerator(ScriptManager scriptManager, ScannerManager scannerManager) {
    this.scriptManager = scriptManager;
    this.scannerManager = scannerManager;
  }

  /**
   * Генерирует объект класса {@link Location}, запрашивая от пользователя значения полей.
   *
   * @return Объект класса {@link Location}.
   * @throws ObjectCreationException если происходит ошибка при создании объекта класса {@link
   *     Location}.
   * @author Alvas
   * @see Location
   * @since 1.0
   */
  @Override
  public Location create() throws ObjectCreationException {
    String choice =
        askValue(
                "Добавить местоположение? (1 - Да, 2 - Нет): ",
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
        System.out.println("Добро пожаловать в Формирователь местоположения.");
        yield new Location(
            askValue(
                "Местоположение по X (Long, not null): ",
                Objects::nonNull,
                Long::parseLong,
                scriptManager,
                scannerManager),
            askValue(
                "Местоположение по Y (Long, not null): ",
                Objects::nonNull,
                Long::parseLong,
                scriptManager,
                scannerManager),
            askValue(
                "Местоположение по Z (Integer, not null): ",
                Objects::nonNull,
                Integer::parseInt,
                scriptManager,
                scannerManager));
      }
      case "2", "нет" -> null;
      default -> throw new ObjectCreationException("Некорректный выбор.");
    };
  }
}
