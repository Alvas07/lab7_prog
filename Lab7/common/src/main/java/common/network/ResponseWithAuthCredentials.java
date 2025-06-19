package common.network;

import common.data.auth.AuthCredentials;
import java.io.Serial;

public class ResponseWithAuthCredentials extends Response {
  @Serial private static final long serialVersionUID = 98572893579137552L;
  private final AuthCredentials auth;

  public ResponseWithAuthCredentials(AuthCredentials auth, String message) {
    super(message);
    this.auth = auth;
  }

  public AuthCredentials getAuth() {
    return auth;
  }
}
