package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.exceptions.EmptyCollectionException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import common.network.ResponseWithException;

/**
 * Класс, отвечающий за команду "max_by_creation_date".
 *
 * <p>Описание команды: "Вывести максимальный по полю {@code creationDate} элемент {@link Ticket}
 * коллекции".
 *
 * <p>Не принимает входных аргументов.
 *
 * @see Command
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class MaxByCreationDateCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public MaxByCreationDateCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    try {
      return new Response(
          "МАКСИМАЛЬНЫЙ ЭЛЕМЕНТ ПО ДАТЕ СОЗДАНИЯ:\n" + collectionManager.getMaxByDate());
    } catch (EmptyCollectionException e) {
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
    return "max_by_creation_date";
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
    return "вывести максимальный по creationDate элемент коллекции";
  }
}
