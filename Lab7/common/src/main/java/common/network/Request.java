package common.network;

import common.data.auth.AuthCredentials;
import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
  @Serial private static final long serialVersionUID = 98795723595203572L;

  private final String commandName;
  private final RequestBody requestBody;
  private final AuthCredentials auth;

  public Request(String commandName, RequestBody requestBody, AuthCredentials auth) {
    this.commandName = commandName;
    this.requestBody = requestBody;
    this.auth = auth;
  }

  public String getCommandName() {
    return commandName;
  }

  public RequestBody getRequestBody() {
    return requestBody;
  }

  public AuthCredentials getAuth() {
    return auth;
  }
}
