package common.utils.generators;

import common.data.Coordinates;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import java.util.Objects;

/**
 * Класс, отвечающий за запрос необходимых данных от пользователя и генерацию объекта класса {@link
 * Coordinates}.
 *
 * @see Coordinates
 * @author Alvas
 * @since 1.0
 */
public class CoordinatesGenerator extends ObjectGenerator<Coordinates> {
  private final ScriptManager scriptManager;
  private final ScannerManager scannerManager;

  /**
   * Конструктор генератора координат.
   *
   * @param scriptManager менеджер выполнения скриптов.
   * @param scannerManager менеджер сканеров.
   * @see ScriptManager
   * @see ScannerManager
   * @author Alvas
   * @since 2.0
   */
  public CoordinatesGenerator(ScriptManager scriptManager, ScannerManager scannerManager) {
    this.scriptManager = scriptManager;
    this.scannerManager = scannerManager;
  }

  /**
   * Генерирует объект класса {@link Coordinates}, запрашивая от пользователя значения полей.
   *
   * @return Объект класса {@link Coordinates}.
   * @see Coordinates
   * @author Alvas
   * @since 1.0
   */
  @Override
  public Coordinates create() {
    System.out.println("Добро пожаловать в Формирователь координат.");
    return new Coordinates(
        askValue(
            "Координата по X (float, not null): ",
            Objects::nonNull,
            Float::parseFloat,
            scriptManager,
            scannerManager),
        askValue(
            "Координата по Y (Long, not null, <=332): ",
            x -> (x != null && x <= 332),
            Long::parseLong,
            scriptManager,
            scannerManager));
  }
}
