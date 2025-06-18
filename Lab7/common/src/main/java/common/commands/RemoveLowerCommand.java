package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.exceptions.ObjectCreationException;
import common.exceptions.RemoveException;
import common.managers.CollectionManager;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import common.network.*;
import common.utils.generators.TicketGenerator;

/**
 * Класс, отвечающий за команду "remove_lower".
 *
 * <p>Описание команды: "Удалить из коллекции все элементы {@link Ticket}, меньшие заданного".
 *
 * <p>Не принимает входных аргументов. Вызывает {@link TicketGenerator}, запрашивающий входные
 * данные для создания элемента, с которым происходит сравнение.
 *
 * @see Command
 * @see Ticket
 * @see TicketGenerator
 * @author Alvas
 * @since 1.0
 */
public class RemoveLowerCommand implements Command {
  private final CollectionManager collectionManager;
  private final ScriptManager scriptManager;
  private final ScannerManager scannerManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @param scriptManager менеджер выполнения скриптов.
   * @param scannerManager менеджер сканеров.
   * @see CollectionManager
   * @see ScriptManager
   * @see ScannerManager
   * @author Alvas
   * @since 2.0
   */
  public RemoveLowerCommand(
      CollectionManager collectionManager,
      ScriptManager scriptManager,
      ScannerManager scannerManager) {
    this.collectionManager = collectionManager;
    this.scriptManager = scriptManager;
    this.scannerManager = scannerManager;
  }

  @Override
  public Response execute(Request request) {
    RequestBody body = request.getRequestBody();

    if (!(body instanceof RequestBodyWithTicket)) {
      return new ResponseWithException(new CommandExecuteException("Ожидался билет Ticket."));
    }

    int size = collectionManager.getCollectionSize();
    try {
      Ticket ticket = ((RequestBodyWithTicket) body).getTicket();
      collectionManager.removeLower(ticket);
      return new Response(
          "Удалено "
              + (size - collectionManager.getCollectionSize())
              + " элементов, меньших заданного.");
    } catch (RemoveException e) {
      return new ResponseWithException(e);
    }
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 0) {
      throw new CommandExecuteException("Команда не принимает аргументы.");
    }

    Ticket ticket;
    try {
      ticket =
          new TicketGenerator(collectionManager.getIdManager(), scriptManager, scannerManager)
              .create();
    } catch (ObjectCreationException e) {
      throw new CommandExecuteException(e.getMessage());
    }
    return new RequestBodyWithTicket(args, ticket);
  }

  /**
   * Возвращает название команды.
   *
   * @return Название команды.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public String getName() {
    return "remove_lower";
  }

  /**
   * Возвращает описание команды.
   *
   * @return Описание команды.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public String getDescription() {
    return "удалить из коллекции все элементы, меньшие заданного";
  }
}
