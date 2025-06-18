package client.system;

import common.data.Ticket;
import common.data.TicketType;
import common.exceptions.EmptyCollectionException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import common.managers.IdManager;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;

public class PlaceholderCollectionManager implements CollectionManager {
  @Override
  public ArrayDeque<Ticket> getCollection() {
    return null;
  }

  @Override
  public IdManager getIdManager() {
    return new IdManager();
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
  public void clearCollection() {}

  @Override
  public void addTicket(Ticket ticket) throws WrongArgumentException {}

  @Override
  public void fillCollection(List<Ticket> tickets) {}

  @Override
  public Ticket getById(int id) throws WrongArgumentException {
    return null;
  }

  @Override
  public void updateTicket(int id, Ticket newTicket) {}

  @Override
  public void removeTicket(Ticket ticket) throws RemoveException {}

  @Override
  public Ticket removeHead() throws RemoveException {
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
  public void removeLower(Ticket ticket) throws RemoveException {}

  @Override
  public List<Ticket> getTicketsList() {
    return List.of();
  }
}
