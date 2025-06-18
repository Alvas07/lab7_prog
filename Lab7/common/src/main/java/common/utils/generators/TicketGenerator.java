package common.utils.generators;

import common.data.*;
import common.exceptions.ObjectCreationException;
import common.managers.IdManager;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import java.time.LocalDate;

/**
 * Класс, отвечающий за запрос необходимых данных от пользователя и генерацию объекта класса {@link
 * Ticket}.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class TicketGenerator extends ObjectGenerator<Ticket> {
  private final IdManager idManager;
  private final ScriptManager scriptManager;
  private final ScannerManager scannerManager;

  /**
   * Конструктор генератора билетов.
   *
   * @param idManager менеджер {@code id}.
   * @param scriptManager менеджер выполнения скриптов.
   * @param scannerManager менеджер сканеров.
   * @see IdManager
   * @see ScriptManager
   * @see ScannerManager
   * @author Alvas
   * @since 2.0
   */
  public TicketGenerator(
      IdManager idManager, ScriptManager scriptManager, ScannerManager scannerManager) {
    this.idManager = idManager;
    this.scriptManager = scriptManager;
    this.scannerManager = scannerManager;
  }

  /**
   * Генерирует объект класса {@link Ticket}, запрашивая от пользователя значения полей.
   *
   * @return Объект класса {@link Ticket}.
   * @see Ticket
   * @throws ObjectCreationException если происходит ошибка при создании объекта.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public Ticket create() throws ObjectCreationException {
    System.out.println("Добро пожаловать в Формирователь билета.");
    return new Ticket(
        idManager.getAndIncrement(),
        askValue(
            "Наименование (string, not null, not empty): ",
            x -> (x != null && !x.isEmpty()),
            s -> s,
            scriptManager,
            scannerManager),
        askCoordinates(),
        LocalDate.now(),
        askValue(
            "Стоимость (float, not null, >0): ",
            x -> (x != null && x > 0),
            Float::parseFloat,
            scriptManager,
            scannerManager),
        askTicketType(),
        askPerson());
  }

  /**
   * Запрашивает от пользователя поле {@link Coordinates}.
   *
   * @return Объект класса {@link Coordinates}.
   * @see Coordinates
   * @author Alvas
   * @since 1.0
   */
  private Coordinates askCoordinates() {
    return new CoordinatesGenerator(scriptManager, scannerManager).create();
  }

  /**
   * Запрашивает от пользователя поле {@link Person}.
   *
   * @return Объект класса {@link Person}.
   * @see Person
   * @throws ObjectCreationException если происходит ошибка при создании объекта.
   * @author Alvas
   * @since 1.0
   */
  private Person askPerson() throws ObjectCreationException {
    return new PersonGenerator(scriptManager, scannerManager).create();
  }

  /**
   * Запрашивает от пользователя поле {@link TicketType}.
   *
   * @return Объект перечисления {@link TicketType}.
   * @see TicketType
   * @author Alvas
   * @since 1.0
   */
  private TicketType askTicketType() {
    return (TicketType)
        askEnum("Тип билета: ", TicketType.values(), x -> true, scriptManager, scannerManager);
  }
}
