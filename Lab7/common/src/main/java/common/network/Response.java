package common.network;

import common.data.Ticket;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Response implements Serializable {
  @Serial private static final long serialVersionUID = 6349685376893778396L;
  private final String message;
  private final List<Ticket> tickets;

  public Response(String message, List<Ticket> tickets, boolean stopFlag) {
    this.message = message;
    this.tickets = tickets != null ? tickets : Collections.emptyList();
  }

  public Response(String message) {
    this(message, null, false);
  }

  public Response(String message, List<Ticket> tickets) {
    this(message, tickets, false);
  }

  public String getMessage() {
    return message;
  }

  public List<Ticket> getTickets() {
    return tickets;
  }
}
