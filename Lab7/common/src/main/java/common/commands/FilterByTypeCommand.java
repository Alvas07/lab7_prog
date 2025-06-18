package common.commands;

import common.data.Ticket;
import common.data.TicketType;
import common.exceptions.CommandExecuteException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import common.network.ResponseWithException;
import java.util.List;

/**
 * Класс, отвечающий за команду "filter_by_type".
 *
 * <p>Описание команды: "Вывести элементы {@link Ticket} с заданным значением {@code type}".
 *
 * <p>Принимает на вход один обязательный аргумент - тип билета (тип {@link TicketType}). Регистр не
 * имеет значения.
 *
 * @see Command
 * @see Ticket
 * @see TicketType
 * @author Alvas
 * @since 1.0
 */
public class FilterByTypeCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public FilterByTypeCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    String[] args = request.getRequestBody().getArgs();

    try {
      TicketType type = TicketType.valueOf(args[0].toUpperCase());
      List<Ticket> filteredTickets = collectionManager.getFilteredByType(type);
      if (filteredTickets.isEmpty()) {
        return new Response("Элементов, соответствующих данному типу, не найдено.");
      } else {
        return new Response(
            "ЭЛЕМЕНТЫ С ТИПОМ БИЛЕТА " + type.name().toUpperCase() + ":", filteredTickets);
      }
    } catch (IllegalArgumentException e) {
      return new ResponseWithException(e);
    }
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 1) {
      throw new CommandExecuteException("Команда принимает один обязательный аргумент.");
    }

    try {
      TicketType.valueOf(args[0].toUpperCase());
    } catch (IllegalArgumentException e) {
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
    return "filter_by_type";
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
    return "вывести элементы с заданным значением type";
  }
}
