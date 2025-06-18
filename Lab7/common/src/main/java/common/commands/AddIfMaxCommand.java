package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.exceptions.ObjectCreationException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import common.managers.IdManager;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import common.network.*;
import common.utils.generators.TicketGenerator;

/**
 * Класс, отвечающий за команду "add_if_max".
 *
 * <p>Описание команды: "Добавить новый элемент {@link Ticket} в коллекцию, если его значение
 * превышает максимальное из коллекции".
 *
 * <p>Не принимает входных аргументов. Вызывает {@link TicketGenerator}, запрашивающий входные
 * данные для создания нового элемента.
 *
 * @see Command
 * @see Ticket
 * @see TicketGenerator
 * @author Alvas
 * @since 1.0
 */
public class AddIfMaxCommand implements Command {
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
  public AddIfMaxCommand(
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

    Ticket maxTicket = collectionManager.getMaxTicket();

    try {
      Ticket ticket = ((RequestBodyWithTicket) body).getTicket();
      IdManager idManager = collectionManager.getIdManager();
      ticket.setId(idManager.getAndIncrement());
      if (maxTicket == null || collectionManager.getCollection().isEmpty()) {
        collectionManager.addTicket(ticket);
        return new Response("Билет успешно добавлен.");
      } else if (ticket.compareTo(maxTicket) > 0) {
        collectionManager.addTicket(ticket);
        return new Response("Билет успешно добавлен.");
      } else {
        return new Response("Билет не был добавлен.");
      }
    } catch (WrongArgumentException e) {
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
    return "add_if_max";
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
    return "добавить элемент, если его значение превышает максимальное из коллекции";
  }
}
