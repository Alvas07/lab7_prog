package common.commands;

import common.exceptions.CommandExecuteException;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;

/**
 * Базовый интерфейс для реализации команд.
 *
 * @author Alvas
 * @since 1.0
 */
public interface Command {
  Response execute(Request request);

  RequestBody packageBody(String[] args) throws CommandExecuteException;

  /**
   * Базовый метод для получения названия команды.
   *
   * @return Название команды.
   */
  String getName();

  /**
   * Базовый метод для получения описания команды.
   *
   * @return Описание команды.
   */
  String getDescription();
}
