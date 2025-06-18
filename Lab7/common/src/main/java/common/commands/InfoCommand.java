package common.commands;

import common.exceptions.CommandExecuteException;
import common.managers.CollectionManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;

/**
 * Класс, отвечающий за команду "info".
 *
 * <p>Описание команды: "Вывести информацию о коллекции".
 *
 * <p>Не принимает входных аргументов.
 *
 * @see Command
 * @author Alvas
 * @since 1.0
 */
public class InfoCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 1.0
   */
  public InfoCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    StringBuilder sb = new StringBuilder();
    sb.append("ИНФОРМАЦИЯ О КОЛЛЕКЦИИ\n")
        .append("Тип коллекции: ")
        .append(collectionManager.getCollection().getClass().getSimpleName())
        .append("\n")
        .append("Количество элементов: ")
        .append(collectionManager.getCollectionSize())
        .append("\n")
        .append("Дата инициализации: ")
        .append(collectionManager.getInitializationTime())
        .append("\n")
        .append("Дата последнего изменения: ")
        .append(collectionManager.getLastUpdateTime());

    return new Response(sb.toString().trim());
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
    return "info";
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
    return "вывести информацию о коллекции";
  }
}
