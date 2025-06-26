package common.commands;

import common.exceptions.AuthenticationException;
import common.exceptions.CommandExecuteException;
import common.network.Request;
import common.network.RequestBody;
import common.network.Response;
import common.network.ResponseWithException;

public class SendMessageCommand implements Command {
  @Override
  public Response execute(Request request) {
    if (request.getAuth() == null) {
      return new ResponseWithException(
          new AuthenticationException(
              "Команда "
                  + request.getCommandName()
                  + " доступна только авторизованным пользователям."),
          request.getRequestId());
    }

    String message = request.getAuth().username() + ": " + request.getRequestBody().getArg(0);
    return new Response(Response.ResponseType.BROADCAST, message, request.getRequestId());
  }

  @Override
  public RequestBody packageBody(String[] args) throws CommandExecuteException {
    if (args.length != 1) {
      throw new CommandExecuteException("Команда принимает один обязательный аргумент.");
    }

    return new RequestBody(args);
  }

  @Override
  public String getName() {
    return "send_message";
  }

  @Override
  public String getDescription() {
    return "отправить сообщение всем активным пользователям";
  }
}
