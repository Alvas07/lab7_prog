package common.network;

import java.io.Serial;
import java.util.UUID;

public class ResponseWithException extends Response {
  @Serial private static final long serialVersionUID = 72893759237359235L;
  private final Exception exception;

  public ResponseWithException(Exception exception, UUID requestId) {
    super("Сервер ответил с ошибкой.", requestId);
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
