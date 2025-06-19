package server.managers;

import common.data.*;
import common.exceptions.EmptyCollectionException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import common.utils.DateTimeUtils;
import common.utils.Validator;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlCollectionManager implements CollectionManager {
  private final Connection connection;
  private Deque<Ticket> collection = new ConcurrentLinkedDeque<>();
  private static final Logger logger = LogManager.getLogger();
  private final LocalDateTime initializationTime;
  private LocalDateTime lastUpdateTime;

  private static final String CREATE_TABLE_QUERY =
      "CREATE TABLE IF NOT EXISTS locations ("
          + "id SERIAL PRIMARY KEY,"
          + "lx BIGINT NOT NULL,"
          + "ly BIGINT NOT NULL,"
          + "lz INTEGER NOT NULL"
          + "); "
          + "CREATE TABLE IF NOT EXISTS persons ("
          + "id SERIAL PRIMARY KEY,"
          + "height REAL NOT NULL CHECK(height > 0),"
          + "weight INTEGER NOT NULL CHECK(weight > 0),"
          + "passport_id VARCHAR(28),"
          + "location_id INTEGER,"
          + "CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL); "
          + "CREATE TABLE IF NOT EXISTS tickets ("
          + "id SERIAL PRIMARY KEY,"
          + "name text NOT NULL,"
          + "cx REAL NOT NULL,"
          + "cy BIGINT NOT NULL CHECK(cy <= 332),"
          + "creation_date TIMESTAMP NOT NULL,"
          + "price REAL NOT NULL CHECK(price > 0),"
          + "type TEXT NOT NULL CHECK(type IN('VIP', 'USUAL', 'BUDGETARY', 'CHEAP')),"
          + "person_id INTEGER,"
          + "owner_username TEXT NOT NULL,"
          + "CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE SET NULL,"
          + "CONSTRAINT fk_owner FOREIGN KEY (owner_username) REFERENCES users(username) ON DELETE CASCADE)";

  private static final String SELECT_TICKETS_QUERY =
      "SELECT t.id, t.name, t.cx AS coordinates_x, t.cy AS coordinates_y, t.creation_date, "
          + "t.price, t.type, p.id AS person_id, p.height AS person_height, p.weight AS person_weight, "
          + "p.passport_id AS person_passport_id, l.id AS location_id, l.lx AS location_x, l.ly AS location_y, l.lz AS location_z, "
          + "t.owner_username AS owner_username "
          + "FROM tickets AS t "
          + "LEFT JOIN persons AS p ON t.person_id = p.id "
          + "LEFT JOIN locations AS l ON p.location_id = l.id";

  private static final String SELECT_PERSON_QUERY = "SELECT * FROM persons WHERE id = ?";
  private static final String SELECT_LOCATION_QUERY = "SELECT * FROM locations WHERE id = ?";
  private static final String INSERT_PERSON_QUERY =
      "INSERT INTO persons (height, weight, passport_id, location_id) "
          + "VALUES (?, ?, ?, ?) RETURNING id";
  private static final String INSERT_LOCATION_QUERY =
      "INSERT INTO locations (lx, ly, lz) " + "VALUES (?, ?, ?) RETURNING id";
  private static final String SELECT_PERSON_ID_QUERY =
      "SELECT id FROM persons WHERE height = ? AND weight = ? AND passport_id = ? AND location_id = ?";
  private static final String SELECT_LOCATION_ID_QUERY =
      "SELECT id FROM locations WHERE lx = ? AND ly = ? AND lz = ?";
  private static final String DELETE_TICKETS_QUERY = "DELETE FROM tickets WHERE owner_username = ?";
  private static final String INSERT_TICKET_QUERY =
      "INSERT INTO tickets (name, cx, cy, creation_date, price, type, person_id, owner_username) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
  private static final String SELECT_OWNER_QUERY =
      "SELECT owner_username FROM tickets WHERE id = ?";
  private static final String UPDATE_TICKET_QUERY =
      "UPDATE tickets SET name = ?, cx = ?, cy = ?, price = ?, type = ?, person_id = ? "
          + "WHERE id = ? AND owner_username = ?";
  private static final String DELETE_TICKET_QUERY =
      "DELETE FROM tickets WHERE id = ? and owner_username = ?";

  public SqlCollectionManager(Connection connection) throws SQLException {
    this.connection = connection;
    this.initializationTime = DateTimeUtils.getStartTime();
    this.lastUpdateTime = DateTimeUtils.getStartTime();

    try (Statement statement = connection.createStatement()) {
      statement.execute(CREATE_TABLE_QUERY);
    }

    try (PreparedStatement statement = connection.prepareStatement(SELECT_TICKETS_QUERY)) {
      ResultSet resultSet = statement.executeQuery();

      int invalidTickets = 0;

      while (resultSet.next()) {
        Ticket ticket = convertSqlRowToTicket(resultSet);
        if (ticket != null) {
          collection.addLast(ticket);
        } else {
          invalidTickets++;
        }
      }

      logger.info(
          "Загружено из БД "
              + collection.size()
              + " билетов. Не прошли валидацию "
              + invalidTickets
              + " билетов.");
    }
  }

  private Ticket convertSqlRowToTicket(ResultSet resultSet) {
    try {
      int id = resultSet.getInt("id");
      String name = resultSet.getString("name");

      float cx = resultSet.getFloat("coordinates_x");
      Long cy = resultSet.getLong("coordinates_y");
      Coordinates coordinates = new Coordinates(cx, cy);

      LocalDate creationDate = resultSet.getDate("creation_date").toLocalDate();
      float price = resultSet.getFloat("price");
      TicketType type = TicketType.valueOf(resultSet.getString("type"));

      int personId = resultSet.getInt("person_id");
      Person person = null;
      if (!resultSet.wasNull()) {
        Float height = resultSet.getFloat("person_height");
        int weight = resultSet.getInt("person_weight");
        String passportID = resultSet.getString("person_passport_id");

        int locationId = resultSet.getInt("location_id");
        Location location = null;
        if (!resultSet.wasNull()) {
          Long lx = resultSet.getLong("location_x");
          Long ly = resultSet.getLong("location_y");
          Integer lz = resultSet.getInt("location_z");
          location = new Location(lx, ly, lz);
          location.setId(locationId);
        }

        person = new Person(height, weight, passportID, location);
        person.setId(personId);
      }

      String ownerUsername = resultSet.getString("owner_username");
      Ticket ticket = new Ticket(id, name, coordinates, creationDate, price, type, person);
      ticket.setOwnerUsername(ownerUsername);

      if (!Validator.isValidTicket(ticket)) {
        return null;
      }
      return ticket;
    } catch (SQLException e) {
      logger.error("Возникла ошибка при конвертировании данных из базы.");
      return null;
    }
  }

  private void prepareTicketStatement(PreparedStatement statement, Ticket ticket, int paramOffset)
      throws SQLException {
    statement.setString(1 + paramOffset, ticket.getName());
    statement.setFloat(2 + paramOffset, ticket.getCoordinates().getX());
    statement.setLong(3 + paramOffset, ticket.getCoordinates().getY());
    statement.setDate(4 + paramOffset, Date.valueOf(ticket.getCreationDate()));
    statement.setFloat(5 + paramOffset, ticket.getPrice());
    statement.setString(6 + paramOffset, ticket.getType().toString());
    if (ticket.getPerson() != null) {
      statement.setInt(7 + paramOffset, ticket.getPerson().getId());
    } else {
      statement.setNull(7 + paramOffset, Types.INTEGER);
    }
    statement.setString(8 + paramOffset, ticket.getOwnerUsername());
  }

  private void preparePersonStatement(PreparedStatement statement, Person person, int paramOffset)
      throws SQLException {
    statement.setFloat(1 + paramOffset, person.getHeight());
    statement.setInt(2 + paramOffset, person.getWeight());
    statement.setString(3 + paramOffset, person.getPassportID());
    if (person.getLocation() != null) {
      statement.setInt(4 + paramOffset, person.getLocation().getId());
    } else {
      statement.setNull(4 + paramOffset, Types.INTEGER);
    }
  }

  private void prepareLocationStatement(
      PreparedStatement statement, Location location, int paramOffset) throws SQLException {
    statement.setLong(1 + paramOffset, location.getX());
    statement.setLong(2 + paramOffset, location.getY());
    statement.setInt(3 + paramOffset, location.getZ());
  }

  private Person getPersonById(int id) {
    try (PreparedStatement statement = connection.prepareStatement(SELECT_PERSON_QUERY)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Float height = resultSet.getFloat("height");
        int weight = resultSet.getInt("weight");
        String passportID = resultSet.getString("passport_id");
        int locationID = resultSet.getInt("location_id");
        Location location = null;
        if (!resultSet.wasNull()) {
          location = getLocationById(locationID);
        }
        Person person = new Person(height, weight, passportID, location);
        person.setId(id);
        return person;
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при получении Person: " + e.getMessage());
    }
    return null;
  }

  public Location getLocationById(int id) {
    try (PreparedStatement statement = connection.prepareStatement(SELECT_LOCATION_QUERY)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Long lx = resultSet.getLong("lx");
        Long ly = resultSet.getLong("ly");
        Integer lz = resultSet.getInt("lz");
        Location location = new Location(lx, ly, lz);
        location.setId(id);
        return location;
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при получении Location: " + e.getMessage());
    }
    return null;
  }

  private Integer insertPerson(Person person) throws SQLException {
    Integer currentId = findPersonId(person);
    if (currentId != null) {
      person.setId(currentId);
      return currentId;
    }

    try (PreparedStatement statement = connection.prepareStatement(INSERT_PERSON_QUERY)) {
      preparePersonStatement(statement, person, 0);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Integer newId = resultSet.getInt("id");
        person.setId(newId);
        return newId;
      } else {
        logger.error("Возникла ошибка при вставке пассажира.");
        throw new SQLException("Возникла ошибка при вставке пассажира.");
      }
    }
  }

  private Integer insertLocation(Location location) throws SQLException {
    Integer currentId = findLocationID(location);
    if (currentId != null) {
      location.setId(currentId);
      return currentId;
    }

    try (PreparedStatement statement = connection.prepareStatement(INSERT_LOCATION_QUERY)) {
      prepareLocationStatement(statement, location, 0);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Integer newId = resultSet.getInt("id");
        location.setId(newId);
        return newId;
      } else {
        logger.error("Возникла ошибка при вставке местоположения.");
        throw new SQLException("Возникла ошибка при вставке местоположения.");
      }
    }
  }

  private Integer findPersonId(Person person) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(SELECT_PERSON_ID_QUERY)) {
      statement.setFloat(1, person.getHeight());
      statement.setInt(2, person.getWeight());
      statement.setString(3, person.getPassportID());
      if (person.getLocation() != null) {
        statement.setInt(4, person.getLocation().getId());
      } else {
        statement.setNull(4, Types.INTEGER);
      }
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt("id");
      }
    }
    return null;
  }

  private Integer findLocationID(Location location) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(SELECT_LOCATION_ID_QUERY)) {
      statement.setLong(1, location.getX());
      statement.setLong(2, location.getY());
      statement.setInt(3, location.getZ());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt("id");
      }
    }
    return null;
  }

  private Person checkPerson(Person person) throws SQLException {
    if (person != null
        && person.getId() != null
        && person.getId() != 0
        && person.getHeight() == null) {
      Person fullPerson = getPersonById(person.getId());
      if (fullPerson == null) {
        logger.warn("Пассажир с нужным id не найден.");
        throw new SQLException("Пассажир с нужным id не найден.");
      }
      return fullPerson;
    } else if (person != null && (person.getId() == null || person.getId() == 0)) {
      person.setId(insertPerson(person));
      return person;
    }
    return person;
  }

  private Location checkLocation(Location location) throws SQLException {
    if (location != null
        && location.getId() != null
        && location.getId() != 0
        && (location.getX() == null || location.getY() == null || location.getZ() == null)) {
      Location fullLocation = getLocationById(location.getId());
      if (fullLocation == null) {
        logger.warn("Местоположение с нужным id не найдено.");
        throw new SQLException("Местоположение с нужным id не найдено.");
      }
      return fullLocation;
    } else if (location != null && (location.getId() == null || location.getId() == 0)) {
      location.setId(insertLocation(location));
      return location;
    }
    return location;
  }

  @Override
  public Deque<Ticket> getCollection() {
    return new ArrayDeque<>(collection);
  }

  @Override
  public LocalDateTime getInitializationTime() {
    return initializationTime;
  }

  @Override
  public LocalDateTime getLastUpdateTime() {
    return lastUpdateTime;
  }

  @Override
  public void updateLastModifiedTime() {
    lastUpdateTime = DateTimeUtils.getCurrentTime();
  }

  @Override
  public int getCollectionSize() {
    return collection.size();
  }

  @Override
  public int clearCollection(String username) throws SQLException {
    int deletedTickets = 0;

    try (PreparedStatement statement = connection.prepareStatement(DELETE_TICKETS_QUERY)) {
      statement.setString(1, username);
      deletedTickets = statement.executeUpdate();

      collection.removeIf(ticket -> username.equals(ticket.getOwnerUsername()));
    } catch (SQLException e) {
      logger.error(
          "Возникла ошибка при удалении билетов, принадлежащих пользователю "
              + username
              + ": "
              + e.getMessage());
      throw new SQLException(
          "Возникла ошибка при удалении билетов, принадлежащих пользователю "
              + username
              + ": "
              + e.getMessage());
    }

    updateLastModifiedTime();
    return deletedTickets;
  }

  @Override
  public void addTicket(Ticket ticket) throws WrongArgumentException, SQLException {
    if (ticket == null) {
      throw new WrongArgumentException("Билет не может быть null.");
    }

    try (PreparedStatement statement = connection.prepareStatement(INSERT_TICKET_QUERY)) {
      Person person = ticket.getPerson();
      Location location;
      if (person != null) {
        location = person.getLocation();
        person.setLocation(checkLocation(location));
      }
      ticket.setPerson(checkPerson(person));
      prepareTicketStatement(statement, ticket, 0);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Integer id = resultSet.getInt("id");
        ticket.setId(id);
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при добавлении билета: " + e.getMessage());
      throw new SQLException("Возникла ошибка при добавлении билета: " + e.getMessage());
    }

    updateLastModifiedTime();
    collection.addLast(ticket);
    logger.info("Успешно добавлен билет с id=" + ticket.getId());
  }

  @Override
  public Ticket getById(int id) throws WrongArgumentException {
    Ticket ticket = collection.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    if (ticket == null) {
      throw new WrongArgumentException("Билета с таким id нет в коллекции.");
    }
    return ticket;
  }

  @Override
  public boolean updateTicket(int id, Ticket newTicket, String username)
      throws WrongArgumentException, SQLException {
    try (PreparedStatement checkStatement = connection.prepareStatement(SELECT_OWNER_QUERY)) {
      checkStatement.setInt(1, id);
      ResultSet resultSet = checkStatement.executeQuery();

      if (!resultSet.next()) {
        return false;
      }

      String owner = resultSet.getString("owner_username");
      if (!owner.equals(username)) {
        throw new WrongArgumentException("Невозможно изменить билет, который не принадлежит вам.");
      }

      Person person = newTicket.getPerson();
      Location location;
      if (person != null) {
        location = person.getLocation();
        person.setLocation(checkLocation(location));
      }
      newTicket.setPerson(checkPerson(person));

      try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TICKET_QUERY)) {
        updateStatement.setString(1, newTicket.getName());
        updateStatement.setFloat(2, newTicket.getCoordinates().getX());
        updateStatement.setLong(3, newTicket.getCoordinates().getY());
        updateStatement.setFloat(4, newTicket.getPrice());
        updateStatement.setString(5, newTicket.getType().toString());
        if (newTicket.getPerson() != null) {
          updateStatement.setInt(6, newTicket.getPerson().getId());
        } else {
          updateStatement.setNull(6, Types.INTEGER);
        }
        updateStatement.setInt(7, id);
        updateStatement.setString(8, username);

        int updatedRows = updateStatement.executeUpdate();

        if (updatedRows > 0) {
          Ticket oldTicket = getById(id);
          oldTicket.setName(newTicket.getName());
          oldTicket.setCoordinates(newTicket.getCoordinates());
          oldTicket.setPrice(newTicket.getPrice());
          oldTicket.setType(newTicket.getType());
          oldTicket.setPerson(newTicket.getPerson());
          updateLastModifiedTime();
          return true;
        } else {
          return false;
        }
      }
    } catch (SQLException e) {
      logger.error("Возникла ошибка при обновлении билета: " + e.getMessage());
      throw new SQLException("Возникла ошибка при обновлении билета: " + e.getMessage());
    }
  }

  @Override
  public boolean removeTicket(Ticket ticket, String username) throws RemoveException, SQLException {
    if (ticket == null) {
      throw new RemoveException("Удаляемый элемент не может быть null.");
    }

    if (!username.equals(ticket.getOwnerUsername())) {
      throw new RemoveException("Невозможно удалить билет, который не принадлежит вам.");
    }

    try (PreparedStatement statement = connection.prepareStatement(DELETE_TICKET_QUERY)) {
      statement.setInt(1, ticket.getId());
      statement.setString(2, ticket.getOwnerUsername());

      int removedRows = statement.executeUpdate();

      if (removedRows > 0) {
        updateLastModifiedTime();
        return collection.remove(ticket);
      }
      return false;
    } catch (SQLException e) {
      logger.error("Возникла ошибка при удалении билета: " + e.getMessage());
      throw new SQLException("Возникла ошибка при удалении билета: " + e.getMessage());
    }
  }

  @Override
  public Ticket removeHead(String username) throws RemoveException, SQLException {
    Ticket ticket = collection.getFirst();
    if (removeTicket(ticket, username)) {
      updateLastModifiedTime();
      return ticket;
    }
    return null;
  }

  @Override
  public float getAveragePrice() {
    if (collection.isEmpty()) {
      return (float) 0;
    }

    float sumPrice = 0;
    for (Ticket ticket : collection) {
      sumPrice += ticket.getPrice();
    }
    return sumPrice / getCollectionSize();
  }

  @Override
  public Ticket getMaxByDate() throws EmptyCollectionException {
    if (collection.isEmpty()) {
      throw new EmptyCollectionException("Невозможно найти максимальный элемент.");
    }

    return collection.stream().max(Ticket::compareToByDate).orElse(null);
  }

  @Override
  public List<Ticket> getFilteredByType(TicketType type) {
    return collection.stream().filter(t -> t.getType().equals(type)).toList();
  }

  @Override
  public Ticket getMaxTicket() {
    return collection.stream().max(Ticket::compareTo).orElse(null);
  }

  @Override
  public void removeLower(Ticket ticket, String username) throws RemoveException, SQLException {
    if (ticket == null) {
      throw new RemoveException("Не может быть элементов меньше null.");
    }
    for (Ticket t : collection) {
      if (t.compareTo(ticket) < 0) {
        if (removeTicket(t, username)) {
          updateLastModifiedTime();
        }
      }
    }
  }

  @Override
  public List<Ticket> getTicketsList() {
    return new ArrayList<>(collection);
  }
}
