package common.managers;

import common.commands.*;
import common.data.auth.AuthCredentials;
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
      ScannerManager scannerManager,
      UserManager userManager) {
    commandList = new LinkedHashMap<>();
    commandList.put("help", new HelpCommand(collectionManager));
    commandList.put("login", new LoginCommand(userManager, scannerManager, scriptManager));
    commandList.put("register", new RegisterCommand(userManager, scannerManager, scriptManager));
    commandList.put("info", new InfoCommand(collectionManager));
    commandList.put("send_message", new SendMessageCommand());
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

  public Request convertInputToCommandRequest(String line, AuthCredentials auth)
      throws UnknownCommandException, CommandExecuteException {
    String[] parts = line.strip().trim().split("\\s+", 2);
    String commandName = parts[0];
    String[] args;
    if (!commandName.equals("send_message")) {
      args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];
    } else {
      args = parts.length > 1 ? new String[] {parts[1]} : new String[0];
    }

    if (!commandList.containsKey(commandName)) {
      throw new UnknownCommandException(commandName);
    }
    Command command = commandList.get(commandName);
    RequestBody body = command.packageBody(args);
    return new Request(commandName, body, auth);
  }

  public Response executeRequest(Request request) {
    Command command = commandList.get(request.getCommandName());
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
