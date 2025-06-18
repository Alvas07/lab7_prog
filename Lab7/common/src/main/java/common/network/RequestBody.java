package common.network;

import java.io.Serial;
import java.io.Serializable;

public class RequestBody implements Serializable {
  @Serial private static final long serialVersionUID = 519875102759122521L;
  private final String[] args;

  public RequestBody(String[] args) {
    this.args = args;
  }

  public String getArg(int i) {
    return args[i];
  }

  public String[] getArgs() {
    return args;
  }

  public int getArgsLength() {
    return args.length;
  }
}
