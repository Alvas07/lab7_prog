package common.managers;

import common.commands.*;
import common.exceptions.CommandExecuteException;
import common.exceptions.UnknownCommandException;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Класс, отвечающий за связь между командами и {@link CollectionManager}.
 *
 * @see CollectionManager
 * @author Alvas
 * @since 1.0
 */
public class CommandManager {
  private final HashMap<String, Command> commandList;

  /**
   * Конструктор менеджера команд.
   *
   * <p>Создает коллекцию {@link LinkedHashMap} для хранения всех команд и помещает туда все
   * существующие.
   *
   * @param collectionManager менеджер коллекции {@link CollectionManager}.
   * @see CollectionManager
   * @see LinkedHashMap
   * @author Alvas
   * @since 2.0
   */
  public CommandManager(
      CollectionManager collectionManager,
      ScriptManager scriptManager,
      ScannerManager scannerManager) {
    commandList = new LinkedHashMap<>();
    commandList.put("help", new HelpCommand(collectionManager));
    commandList.put("info", new InfoCommand(collectionManager));
    commandList.put("show", new ShowCommand(collectionManager));
    commandList.put("add", new AddCommand(collectionManager, scriptManager, scannerManager));
    commandList.put("update", new UpdateCommand(collectionManager, scriptManager, scannerManager));
    commandList.put("remove_by_id", new RemoveByIdCommand(collectionManager));
    commandList.put("clear", new ClearCommand(collectionManager));
    commandList.put("remove_head", new RemoveHeadCommand(collectionManager));
    commandList.put(
        "remove_lower", new RemoveLowerCommand(collectionManager, scriptManager, scannerManager));
    commandList.put("max_by_creation_date", new MaxByCreationDateCommand(collectionManager));
    commandList.put("filter_by_type", new FilterByTypeCommand(collectionManager));
    commandList.put(
        "add_if_max", new AddIfMaxCommand(collectionManager, scriptManager, scannerManager));
    commandList.put("average_of_price", new AverageOfPriceCommand(collectionManager));
  }

  public Request convertInputToCommandRequest(String line)
      throws UnknownCommandException, CommandExecuteException {
    String[] parts = line.strip().trim().split("\\s+", 2);
    String commandName = parts[0];
    String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

    if (!commandList.containsKey(commandName)) {
      throw new UnknownCommandException(commandName);
    }
    Command command = commandList.get(commandName);
    RequestBody body = command.packageBody(args);
    return new Request(commandName, body);
  }

  public Response executeRequest(Request request) {
    Command command = commandList.get(request.getCommandName());

    if (request.getCommandName().equals("save")) {
      return new Response(
          "Недостаточно прав. Команда 'save' может быть использована только на сервере.");
    }

    return command.execute(request);
  }

  /**
   * Возвращает все существующие команды в виде {@link LinkedHashMap}.
   *
   * @return Все существующие команды.
   * @see LinkedHashMap
   * @author Alvas
   * @since 1.0
   */
  public HashMap<String, Command> getCommandList() {
    return commandList;
  }
}
