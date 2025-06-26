package common.network;

import common.data.Ticket;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Response implements Serializable {
  public enum ResponseType {
    NORMAL,
    BROADCAST
  }

  @Serial private static final long serialVersionUID = 6349685376893778396L;
  private final String message;
  private final List<Ticket> tickets;
  private ResponseType responseType;
  private final UUID requestId;

  public Response(ResponseType responseType, String message, List<Ticket> tickets, UUID requestId) {
    this.responseType = responseType;
    this.message = message;
    this.tickets = tickets != null ? tickets : Collections.emptyList();
    this.requestId = requestId;
  }

  public Response(String message, UUID requestId) {
    this(ResponseType.NORMAL, message, null, requestId);
  }

  public Response(String message, List<Ticket> tickets, UUID requestId) {
    this(ResponseType.NORMAL, message, tickets, requestId);
  }

  public Response(ResponseType responseType, String message, UUID requestId) {
    this(responseType, message, null, requestId);
  }

  public String getMessage() {
    return message;
  }

  public List<Ticket> getTickets() {
    return tickets;
  }

  public ResponseType getResponseType() {
    return responseType;
  }

  public UUID getRequestId() {
    return requestId;
  }
}
