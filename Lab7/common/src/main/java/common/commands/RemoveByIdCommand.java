package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import common.network.ResponseWithException;

/**
 * Класс, отвечающий за команду "remove_by_id".
 *
 * <p>Описание команды: "Удалить элемент {@link Ticket} из коллекции по {@code id}".
 *
 * <p>Принимает на вход один обязательный аргумент - {@code id} элемента в коллекции (тип {@code
 * int}).
 *
 * @see Command
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class RemoveByIdCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public RemoveByIdCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    String[] args = request.getRequestBody().getArgs();

    try {
      int id = Integer.parseInt(args[0]);
      Ticket ticket = collectionManager.getById(id);
      collectionManager.removeTicket(ticket);
      return new Response("Удален элемент с id=" + id);
    } catch (WrongArgumentException | NumberFormatException | RemoveException e) {
      return new ResponseWithException(e);
    }
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 1) {
      throw new CommandExecuteException("Команда принимает один обязательный аргумент.");
    }

    try {
      Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      throw new CommandExecuteException(e.getMessage());
    }

    return new RequestBody(args);
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
    return "remove_by_id";
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
    return "удалить элемент по id";
  }
}
