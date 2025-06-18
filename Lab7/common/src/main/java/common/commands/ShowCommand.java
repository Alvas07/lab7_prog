package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;

/**
 * Класс, отвечающий за команду "show".
 *
 * <p>Описание команды: "Вывести все элементы {@link Ticket} коллекции".
 *
 * <p>Не принимает входных аргументов.
 *
 * @see Command
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class ShowCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public ShowCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    if (collectionManager.getCollectionSize() == 0) {
      return new Response("Коллекция пуста.");
    } else {
      return new Response("ЭЛЕМЕНТЫ КОЛЛЕКЦИИ:", collectionManager.getTicketsList());
    }
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 0) {
      throw new CommandExecuteException("Команда не принимает аргументы.");
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
    return "show";
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
    return "вывести все элементы коллекции";
  }
}
