package common.commands;

import common.data.Ticket;
import common.exceptions.CommandExecuteException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;

/**
 * Класс, отвечающий за команду "average_of_price".
 *
 * <p>Описание команды: "Вывести среднее значение поля {@code price} для всех элементов {@link
 * Ticket} коллекции".
 *
 * <p>Не принимает входных аргументов.
 *
 * @see Command
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class AverageOfPriceCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public AverageOfPriceCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    return new Response(
        "Cредняя цена по всем элементам коллекции равна " + collectionManager.getAveragePrice());
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
    return "average_of_price";
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
    return "вывести среднее значение поля price для всех элементов коллекции";
  }
}
