package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.exceptions.RemoveException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import common.network.ResponseWithException;

/**
 * Класс, отвечающий за команду "remove_head".
 *
 * <p>Описание команды: "Вывести первый элемент {@link Ticket} коллекции и удалить его".
 *
 * <p>Не принимает входных аргументов.
 *
 * @see Command
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class RemoveHeadCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public RemoveHeadCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    try {
      Ticket head = collectionManager.removeHead();
      return new Response("ПЕРВЫЙ ЭЛЕМЕНТ КОЛЛЕКЦИИ:\n" + head);
    } catch (RemoveException e) {
      return new ResponseWithException(e);
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
    return "remove_head";
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
    return "вывести первый элемент коллекции и удалить его";
  }
}
