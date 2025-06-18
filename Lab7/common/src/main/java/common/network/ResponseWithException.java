package common.network;

import java.io.Serial;

public class ResponseWithException extends Response {
  @Serial private static final long serialVersionUID = 72893759237359235L;
  private final Exception exception;

  public ResponseWithException(Exception exception) {
    super("Сервер ответил с ошибкой.");
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
