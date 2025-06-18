package common.network;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
  @Serial private static final long serialVersionUID = 98795723595203572L;

  private final String commandName;
  private final RequestBody requestBody;

  public Request(String commandName, RequestBody requestBody) {
    this.commandName = commandName;
    this.requestBody = requestBody;
  }

  public String getCommandName() {
    return commandName;
  }

  public RequestBody getRequestBody() {
    return requestBody;
  }
}
