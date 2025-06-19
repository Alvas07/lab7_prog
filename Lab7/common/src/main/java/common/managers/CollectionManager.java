package common.managers;

import common.data.Ticket;
import common.data.TicketType;
import common.exceptions.EmptyCollectionException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;

public interface CollectionManager {
  Deque<Ticket> getCollection();

  LocalDateTime getInitializationTime();

  LocalDateTime getLastUpdateTime();

  void updateLastModifiedTime();

  int getCollectionSize();

  int clearCollection(String username) throws SQLException;

  void addTicket(Ticket ticket) throws WrongArgumentException, SQLException;

  Ticket getById(int id) throws WrongArgumentException;

  boolean updateTicket(int id, Ticket newTicket, String username)
      throws WrongArgumentException, SQLException;

  boolean removeTicket(Ticket ticket, String username) throws RemoveException;

  Ticket removeHead(String username) throws RemoveException;

  float getAveragePrice();

  Ticket getMaxByDate() throws EmptyCollectionException;

  List<Ticket> getFilteredByType(TicketType type);

  Ticket getMaxTicket();

  void removeLower(Ticket ticket, String username) throws RemoveException;

  List<Ticket> getTicketsList();
}
