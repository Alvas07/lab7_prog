package common.managers;

import common.data.Ticket;
import common.data.TicketType;
import common.exceptions.EmptyCollectionException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;

public interface CollectionManager {
  ArrayDeque<Ticket> getCollection();

  IdManager getIdManager();

  LocalDateTime getInitializationTime();

  LocalDateTime getLastUpdateTime();

  void updateLastModifiedTime();

  int getCollectionSize();

  void clearCollection();

  void addTicket(Ticket ticket) throws WrongArgumentException;

  void fillCollection(List<Ticket> tickets);

  Ticket getById(int id) throws WrongArgumentException;

  void updateTicket(int id, Ticket newTicket);

  void removeTicket(Ticket ticket) throws RemoveException;

  Ticket removeHead() throws RemoveException;

  float getAveragePrice();

  Ticket getMaxByDate() throws EmptyCollectionException;

  List<Ticket> getFilteredByType(TicketType type);

  Ticket getMaxTicket();

  void removeLower(Ticket ticket) throws RemoveException;

  List<Ticket> getTicketsList();
}
