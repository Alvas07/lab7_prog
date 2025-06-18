package common.commands;

import common.exceptions.CommandExecuteException;
import common.managers.CollectionManager;
import common.managers.CommandManager;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import java.util.HashMap;

/**
 * Класс, отвечающий за команду "help".
 *
 * <p>Описание команды: "Вывести справку по доступным командам".
 *
 * <p>Не принимает входных аргументов.
 *
 * @see Command
 * @author Alvas
 * @since 1.0
 */
public class HelpCommand implements Command {
  private final CollectionManager collectionManager;

  /**
   * Конструктор команды.
   *
   * @param collectionManager менеджер коллекции.
   * @see CollectionManager
   * @author Alvas
   * @since 2.0
   */
  public HelpCommand(CollectionManager collectionManager) {
    this.collectionManager = collectionManager;
  }

  @Override
  public Response execute(Request request) {
    CommandManager commandManager = new CommandManager(collectionManager, null, null);
    HashMap<String, Command> commandList = commandManager.getCommandList();
    StringBuilder sb = new StringBuilder();
    sb.append("ДОСТУПНЫЕ КОМАНДЫ:\n");
    for (String commandName : commandList.keySet()) {
      Command command = commandList.get(commandName);
      sb.append(command.getName()).append(" - ").append(command.getDescription()).append("\n");
    }

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
    return "help";
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
    return "вывести справку по доступным командам";
  }
}
