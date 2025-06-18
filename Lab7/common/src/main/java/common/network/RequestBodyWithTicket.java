package common.network;

import common.data.Ticket;
import java.io.Serial;

public class RequestBodyWithTicket extends RequestBody {
  @Serial private static final long serialVersionUID = 254325892375295532L;
  private final Ticket ticket;

  public RequestBodyWithTicket(String[] args, Ticket ticket) {
    super(args);
    this.ticket = ticket;
  }

  public Ticket getTicket() {
    return ticket;
  }
}
