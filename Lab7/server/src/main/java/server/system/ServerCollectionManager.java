package server.system;

import common.data.Ticket;
import common.data.TicketType;
import common.exceptions.EmptyCollectionException;
import common.exceptions.RemoveException;
import common.exceptions.WrongArgumentException;
import common.managers.CollectionManager;
import common.managers.FileManager;
import common.managers.IdManager;
import common.utils.DateTimeUtils;
import common.utils.Validator;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, отвечающий за взаимодействие с коллекцией элементов {@link Ticket}.
 *
 * <p>Для реализации используется коллекция {@link ArrayDeque}.
 *
 * @see Ticket
 * @see ArrayDeque
 * @author Alvas
 * @since 1.0
 */
public class ServerCollectionManager implements CollectionManager {
  private final ArrayDeque<Ticket> collection;
  private final LocalDateTime initializationTime;
  private LocalDateTime lastUpdateTime;
  private final IdManager idManager;

  /**
   * Конструктор менеджера коллекции.
   *
   * <p>Создает коллекцию, устанавливает время инициализации и последней модификации.
   *
   * <p>Время берет из системных параметров файла.
   *
   * @param fileManager файловый менеджер.
   * @param idManager менеджер {@code id}.
   * @see FileManager
   * @see IdManager
   * @author Alvas
   * @since 2.0
   */
  public ServerCollectionManager(FileManager fileManager, IdManager idManager) {
    this.collection = new ArrayDeque<>();
    this.initializationTime = fileManager.getFileCreationTime();
    this.lastUpdateTime = fileManager.getFileLastModifiedTime();
    this.idManager = idManager;
  }

  public IdManager getIdManager() {
    return idManager;
  }

  /**
   * Возвращает коллекцию элементов.
   *
   * @return Коллекция элементов.
   * @author Alvas
   * @since 1.0
   */
  public ArrayDeque<Ticket> getCollection() {
    return collection;
  }

  /**
   * Возвращает время инициализации коллекции элементов.
   *
   * @return Время инициализации.
   * @author Alvas
   * @since 1.0
   */
  public LocalDateTime getInitializationTime() {
    return initializationTime;
  }

  /**
   * Возвращает время последней модификации коллекции элементов.
   *
   * @return Время последней модификации.
   * @author Alvas
   * @since 1.0
   */
  public LocalDateTime getLastUpdateTime() {
    return lastUpdateTime;
  }

  /**
   * Обновляет время последней модификации коллекции элементов на текущее.
   *
   * @author Alvas
   * @since 1.0
   */
  public void updateLastModifiedTime() {
    lastUpdateTime = DateTimeUtils.getCurrentTime();
  }

  /**
   * Возвращает размер коллекции (количество элементов).
   *
   * @return Размер коллекции.
   * @author Alvas
   * @since 1.0
   */
  public int getCollectionSize() {
    return collection.size();
  }

  /**
   * Очищает коллекцию и обновляет время последней модификации.
   *
   * @see ServerCollectionManager#updateLastModifiedTime()
   * @author Alvas
   * @since 1.0
   */
  public void clearCollection() {
    collection.clear();
    updateLastModifiedTime();
  }

  /**
   * Добавляет элемент {@link Ticket} в коллекцию и обновляет время последней модификации.
   *
   * @param ticket элемент для добавления.
   * @see Ticket
   * @see IdManager
   * @see ServerCollectionManager#updateLastModifiedTime()
   * @throws WrongArgumentException если элемент уже содержится в коллекции или равен {@code null}.
   * @author Alvas
   * @since 1.0
   */
  public void addTicket(Ticket ticket) throws WrongArgumentException {
    if (ticket == null) {
      throw new WrongArgumentException("Билет не может быть null.");
    }
    if (collection.contains(ticket)) {
      throw new WrongArgumentException("Билет уже содержится в данной коллекции.");
    }
    collection.addLast(ticket);
    idManager.addId(ticket.getId());
    updateLastModifiedTime();
  }

  /**
   * Заполняет коллекцию всеми элементами {@link Ticket} из списка.
   *
   * @param tickets список элементов для добавления.
   * @see Ticket
   * @author Alvas
   * @since 2.0
   */
  public void fillCollection(List<Ticket> tickets) {
    for (Ticket ticket : tickets) {
      try {
        if (Validator.isValidTicket(ticket, idManager)) {
          addTicket(ticket);
        } else {
          System.out.println("Объект не прошел валидацию.");
        }
      } catch (WrongArgumentException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Возвращает элемент {@link Ticket} коллекции по заданному {@code id}.
   *
   * @param id уникальный идентификатор элемента.
   * @see Ticket
   * @return Элемент коллекции с заданным {@code id}.
   * @throws WrongArgumentException если элемента с заданным {@code id} нет в коллекции.
   * @author Alvas
   * @since 1.0
   */
  public Ticket getById(int id) throws WrongArgumentException {
    Ticket ticket = collection.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    if (ticket == null) {
      throw new WrongArgumentException("Билета с таким id нет в коллекции.");
    }
    return ticket;
  }

  /**
   * Обновляет элемент {@link Ticket} коллекции по заданному {@code id} и обновляет время последней
   * модификации.
   *
   * @param id уникальный идентификатор элемента.
   * @param newTicket новый элемент коллекции.
   * @see Ticket
   * @see ServerCollectionManager#updateLastModifiedTime()
   * @author Alvas
   * @since 1.0
   */
  public void updateTicket(int id, Ticket newTicket) {
    try {
      Ticket oldTicket = getById(id);
      oldTicket.setName(newTicket.getName());
      oldTicket.setCoordinates(newTicket.getCoordinates());
      oldTicket.setPrice(newTicket.getPrice());
      oldTicket.setCreationDate(newTicket.getCreationDate());
      oldTicket.setType(newTicket.getType());
      oldTicket.setPerson(newTicket.getPerson());
      updateLastModifiedTime();
    } catch (WrongArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Удаляет заданный элемент {@link Ticket} из коллекции и обновляет время последней модификации.
   *
   * @param ticket элемент для удаления.
   * @see Ticket
   * @see IdManager
   * @see ServerCollectionManager#updateLastModifiedTime()
   * @throws RemoveException если удаляемый элемент равен {@code null}.
   * @author Alvas
   * @since 1.0
   */
  public void removeTicket(Ticket ticket) throws RemoveException {
    if (ticket == null) {
      throw new RemoveException("Удаляемый элемент не может быть null.");
    }
    collection.remove(ticket);
    updateLastModifiedTime();
  }

  /**
   * Удаляет первый элемент {@link Ticket} коллекции, возвращает его и обновляет время последней
   * модификации.
   *
   * @return Первый элемент коллекции.
   * @see Ticket
   * @see IdManager
   * @see ServerCollectionManager#updateLastModifiedTime()
   * @throws RemoveException если коллекция пуста.
   * @author Alvas
   * @since 1.0
   */
  public Ticket removeHead() throws RemoveException {
    Ticket head = collection.poll();
    if (head == null) {
      throw new RemoveException("Удаляемый элемент не может быть null.");
    }
    updateLastModifiedTime();
    return head;
  }

  /**
   * Возвращает среднее значение поля {@code price} для всех элементов {@link Ticket} коллекции.
   *
   * @return Среднее значение поля {@code price}.
   * @see Ticket
   * @author Alvas
   * @since 1.0
   */
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

  /**
   * Возвращает максимальный элемент {@link Ticket} коллекции по полю {@code creationDate}.
   *
   * <p>Использует сравнение по полю {@code creationDate}.
   *
   * @return Элемент с максимальным значением поля {@code creationDate}.
   * @see Ticket
   * @see Ticket#compareToByDate(Ticket other)
   * @throws EmptyCollectionException если коллекция пуста.
   * @author Alvas
   * @since 1.0
   */
  public Ticket getMaxByDate() throws EmptyCollectionException {
    if (collection.isEmpty()) {
      throw new EmptyCollectionException("Невозможно найти максимальный элемент.");
    }

    return collection.stream().max(Ticket::compareToByDate).orElse(null);
  }

  /**
   * Возвращает список всех элементов {@link Ticket} коллекции с заданным значением {@link
   * TicketType}.
   *
   * @param type тип.
   * @return Список элементов с заданным типом.
   * @see Ticket
   * @see TicketType
   * @author Alvas
   * @since 1.0
   */
  public List<Ticket> getFilteredByType(TicketType type) {
    return collection.stream().filter(t -> t.getType().equals(type)).toList();
  }

  /**
   * Возвращает максимальный элемент {@link Ticket} из коллекции.
   *
   * <p>Использует сравнение по умолчанию.
   *
   * @return Максимальный элемент коллекции.
   * @see Ticket
   * @see Ticket#compareTo(Ticket other)
   * @author Alvas
   * @since 1.0
   */
  public Ticket getMaxTicket() {
    return collection.stream().max(Ticket::compareTo).orElse(null);
  }

  /**
   * Удаляет элемент {@link Ticket} из коллекции, если он меньше заданного, и обновляет время
   * последней модификации.
   *
   * <p>Использует сравнение по умолчанию.
   *
   * @param ticket элемент для сравнения.
   * @see Ticket
   * @see Ticket#compareTo(Ticket other)
   * @see ServerCollectionManager#updateLastModifiedTime()
   * @throws RemoveException если элемент для сравнения равен {@code null}.
   * @author Alvas
   * @since 1.0
   */
  public void removeLower(Ticket ticket) throws RemoveException {
    if (ticket == null) {
      throw new RemoveException("Не может быть элементов меньше null.");
    }
    collection.removeIf(t -> t.compareTo(ticket) < 0);
    updateLastModifiedTime();
  }

  /**
   * Возвращает список всех элементов {@link Ticket} коллекции.
   *
   * @return Список всех элементов коллекции.
   * @see Ticket
   * @author Alvas
   * @since 1.0
   */
  public List<Ticket> getTicketsList() {
    return new ArrayList<>(collection);
  }
}
