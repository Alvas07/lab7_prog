package client.system.placeholders;

import common.data.Ticket;
import common.data.TicketType;
import common.exceptions.EmptyCollectionException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;

public class PlaceholderCollectionManager implements CollectionManager {

  @Override
  public Deque<Ticket> getCollection() {
    return null;
  }

  @Override
  public LocalDateTime getInitializationTime() {
    return null;
  }

  @Override
  public LocalDateTime getLastUpdateTime() {
    return null;
  }

  @Override
  public void updateLastModifiedTime() {}

  @Override
  public int getCollectionSize() {
    return 0;
  }

  @Override
  public int clearCollection(String username) {
    return 0;
  }

  @Override
  public void addTicket(Ticket ticket) throws WrongArgumentException {}

  @Override
  public Ticket getById(int id) throws WrongArgumentException {
    return null;
  }

  @Override
  public boolean updateTicket(int id, Ticket newTicket, String username)
      throws WrongArgumentException {
    return false;
  }

  @Override
  public boolean removeTicket(Ticket ticket, String username) throws RemoveException {
    return false;
  }

  @Override
  public Ticket removeHead(String username) throws RemoveException {
    return null;
  }

  @Override
  public float getAveragePrice() {
    return 0;
  }

  @Override
  public Ticket getMaxByDate() throws EmptyCollectionException {
    return null;
  }

  @Override
  public List<Ticket> getFilteredByType(TicketType type) {
    return List.of();
  }

  @Override
  public Ticket getMaxTicket() {
    return null;
  }

  @Override
  public void removeLower(Ticket ticket, String username) throws RemoveException {}

  @Override
  public List<Ticket> getTicketsList() {
    return List.of();
  }
}
