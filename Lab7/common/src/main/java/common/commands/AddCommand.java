package common.commands;

import common.data.Ticket;
import common.exceptions.AuthenticationException;
import common.exceptions.CommandExecuteException;
import common.exceptions.ObjectCreationException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import common.network.*;
import common.utils.generators.TicketGenerator;
import java.sql.SQLException;

/**
 * Класс, отвечающий за команду "add".
 *
 * <p>Описание команды: "Добавить новый элемент {@link Ticket} в коллекцию".
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
public class AddCommand implements Command {
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
  public AddCommand(
      CollectionManager collectionManager,
      ScriptManager scriptManager,
      ScannerManager scannerManager) {
    this.collectionManager = collectionManager;
    this.scriptManager = scriptManager;
    this.scannerManager = scannerManager;
  }

  @Override
  public Response execute(Request request) {
    if (request.getAuth() == null) {
      return new ResponseWithException(
          new AuthenticationException(
              "Команда "
                  + request.getCommandName()
                  + " доступна только авторизованным пользователям."),
          request.getRequestId());
    }

    RequestBody body = request.getRequestBody();

    if (!(body instanceof RequestBodyWithTicket)) {
      return new ResponseWithException(
          new CommandExecuteException("Ожидался билет Ticket."), request.getRequestId());
    }

    try {
      Ticket ticket = ((RequestBodyWithTicket) body).getTicket();
      ticket.setOwnerUsername(request.getAuth().username());
      collectionManager.addTicket(ticket);
      return new Response("Билет успешно добавлен.", request.getRequestId());
    } catch (WrongArgumentException | SQLException e) {
      return new ResponseWithException(e, request.getRequestId());
    }
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 0) {
      throw new CommandExecuteException("Команда не принимает аргументы.");
    }

    Ticket ticket;
    try {
      ticket = new TicketGenerator(scriptManager, scannerManager).create();
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
    return "add";
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
    return "добавить новый элемент в коллекцию";
  }
}
