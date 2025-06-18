package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.exceptions.ObjectCreationException;
import common.managers.CollectionManager;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import common.network.*;
import common.utils.generators.TicketGenerator;

/**
 * Класс, отвечающий за команду "update".
 *
 * <p>Описание команды: "Обновить элемент {@link Ticket} из коллекции по {@code id}".
 *
 * <p>Принимает на вход один обязательный аргумент - {@code id} элемента в коллекции (тип {@code
 * int}).
 *
 * @see Command
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class UpdateCommand implements Command {
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
  public UpdateCommand(
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

    String[] args = body.getArgs();

    try {
      int id = Integer.parseInt(args[0]);
      Ticket ticket = ((RequestBodyWithTicket) body).getTicket();
      collectionManager.updateTicket(id, ticket);
      return new Response("Элемент с id=" + id + " обновлен.");
    } catch (NumberFormatException e) {
      return new ResponseWithException(e);
    }
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 1) {
      throw new CommandExecuteException("Команда принимает один обязательный аргумент.");
    }

    Ticket ticket;
    try {
      ticket =
          new TicketGenerator(collectionManager.getIdManager(), scriptManager, scannerManager)
              .create();
      Integer.parseInt(args[0]);
    } catch (ObjectCreationException | NumberFormatException e) {
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
    return "update";
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
    return "обновить элемент по id";
  }
}
