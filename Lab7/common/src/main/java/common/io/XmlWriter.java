package common.io;

import common.data.Coordinates;
import common.data.Location;
import common.data.Person;
import common.data.Ticket;
import common.exceptions.FileWriteException;
import common.utils.comparators.CoordinatesComparator;
import java.io.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Класс, отвечающий за преобразование объектов класса {@link Ticket} в формат XML и их запись в
 * файл.
 *
 * @see Ticket
 * @author Alvas
 * @since 1.0
 */
public class XmlWriter implements TicketWriter {
  /**
   * Преобразует каждый объект класса {@link Ticket} из списка в формат XML и записывает его в файл.
   *
   * <p>Для записи данных в файл использует класс {@link BufferedOutputStream}.
   *
   * <p>Перед записью сортирует все объекты в порядке возрастания {@code id}.
   *
   * @param fileName путь к файлу.
   * @param tickets список объектов класса {@link Ticket}.
   * @see Ticket
   * @see BufferedOutputStream
   * @throws FileWriteException если невозможно записать в файл.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public void writeTicketsToFile(String fileName, List<Ticket> tickets) throws FileWriteException {
    if (!canWrite(fileName)) {
      throw new FileWriteException("Невозможно записать в файл.");
    }

    tickets.sort(
        (ticket1, ticket2) ->
            new CoordinatesComparator()
                .compare(ticket1.getCoordinates(), ticket2.getCoordinates()));
    try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(fileName))) {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element rootElement = document.createElement("list");
      document.appendChild(rootElement);

      for (Ticket ticket : tickets) {
        Element ticketElement = document.createElement("ticket");
        writeTicket(document, ticketElement, ticket);
        rootElement.appendChild(ticketElement);
      }

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(writer);

      transformer.transform(source, result);
    } catch (Exception e) {
      throw new FileWriteException(e.getMessage());
    }
  }

  /**
   * Показывает возможность записи данных в файл.
   *
   * @param fileName путь к файлу.
   * @return {@code true} - если файл доступен для записи, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  @Override
  public boolean canWrite(String fileName) {
    File file = new File(fileName);
    if (!file.exists()) {
      System.out.println("Файл не найден.");
      return false;
    }

    if (!file.isFile()) {
      System.out.println("Указанный путь не является файлом.");
      return false;
    }

    if (!file.canWrite()) {
      System.out.println("Нет прав на запись в файл.");
      return false;
    }

    return true;
  }

  /**
   * Записывает объект класса {@link Ticket} в XML-дерево.
   *
   * @param document XML-дерево.
   * @param parentElement родительский XML-элемент.
   * @param ticket объект класса {@link Ticket}.
   * @see Ticket
   * @throws FileWriteException если происходит ошибка при сериализации данных.
   * @author Alvas
   * @since 2.0
   */
  private void writeTicket(Document document, Element parentElement, Ticket ticket)
      throws FileWriteException {
    try {
      Element idElement = document.createElement("id");
      idElement.appendChild(document.createTextNode(String.valueOf(ticket.getId())));
      parentElement.appendChild(idElement);

      Element nameElement = document.createElement("name");
      nameElement.appendChild(document.createTextNode(String.valueOf(ticket.getName())));
      parentElement.appendChild(nameElement);

      Element coordinatesElement = document.createElement("coordinates");
      writeCoordinates(document, coordinatesElement, ticket.getCoordinates());
      parentElement.appendChild(coordinatesElement);

      Element creationDateElement = document.createElement("creationDate");
      creationDateElement.appendChild(
          document.createTextNode(String.valueOf(ticket.getCreationDate())));
      parentElement.appendChild(creationDateElement);

      Element priceElement = document.createElement("price");
      priceElement.appendChild(document.createTextNode(String.valueOf(ticket.getPrice())));
      parentElement.appendChild(priceElement);

      Element typeElement = document.createElement("type");
      typeElement.appendChild(document.createTextNode(String.valueOf(ticket.getType())));
      parentElement.appendChild(typeElement);

      Element personElement = document.createElement("person");
      writePerson(document, personElement, ticket.getPerson());
      parentElement.appendChild(personElement);
    } catch (Exception e) {
      throw new FileWriteException(e.getMessage());
    }
  }

  /**
   * Записывает объект класса {@link Coordinates} в XML-дерево.
   *
   * @param document XML-дерево.
   * @param parentElement родительский XML-элемент.
   * @param coordinates объект класса {@link Coordinates}.
   * @see Coordinates
   * @throws FileWriteException если происходит ошибка при сериализации данных.
   * @author Alvas
   * @since 2.0
   */
  private void writeCoordinates(Document document, Element parentElement, Coordinates coordinates)
      throws FileWriteException {
    try {
      Element cxElement = document.createElement("cx");
      cxElement.appendChild(document.createTextNode(String.valueOf(coordinates.getX())));
      parentElement.appendChild(cxElement);

      Element cyElement = document.createElement("cy");
      cyElement.appendChild(document.createTextNode(String.valueOf(coordinates.getY())));
      parentElement.appendChild(cyElement);
    } catch (Exception e) {
      throw new FileWriteException(e.getMessage());
    }
  }

  /**
   * Записывает объект класса {@link Location} в XML-дерево.
   *
   * @param document XML-дерево.
   * @param parentElement родительский XML-элемент.
   * @param location объект класса {@link Location}.
   * @see Location
   * @throws FileWriteException если происходит ошибка при сериализации данных.
   * @author Alvas
   * @since 2.0
   */
  private void writeLocation(Document document, Element parentElement, Location location)
      throws FileWriteException {
    try {
      if (location == null) {
        parentElement.appendChild(document.createTextNode(""));
      } else {
        Element lxElement = document.createElement("lx");
        lxElement.appendChild(document.createTextNode(String.valueOf(location.getX())));
        parentElement.appendChild(lxElement);

        Element lyElement = document.createElement("ly");
        lyElement.appendChild(document.createTextNode(String.valueOf(location.getY())));
        parentElement.appendChild(lyElement);

        Element lzElement = document.createElement("lz");
        lzElement.appendChild(document.createTextNode(String.valueOf(location.getZ())));
        parentElement.appendChild(lzElement);
      }
    } catch (Exception e) {
      throw new FileWriteException(e.getMessage());
    }
  }

  /**
   * Записывает объект класса {@link Person} в XML-дерево.
   *
   * @param document XML-дерево.
   * @param parentElement родительский XML-элемент.
   * @param person объект класса {@link Person}.
   * @see Person
   * @throws FileWriteException если происходит ошибка при сериализации данных.
   * @author Alvas
   * @since 2.0
   */
  private void writePerson(Document document, Element parentElement, Person person)
      throws FileWriteException {
    try {
      if (person == null) {
        parentElement.appendChild(document.createTextNode(""));
      } else {
        Element heightElement = document.createElement("height");
        heightElement.appendChild(document.createTextNode(String.valueOf(person.getHeight())));
        parentElement.appendChild(heightElement);

        Element weightElement = document.createElement("weight");
        weightElement.appendChild(document.createTextNode(String.valueOf(person.getWeight())));
        parentElement.appendChild(weightElement);

        Element passportIDElement = document.createElement("passportID");
        if (person.getPassportID() == null) {
          passportIDElement.appendChild(document.createTextNode(""));
        } else {
          passportIDElement.appendChild(
              document.createTextNode(String.valueOf(person.getPassportID())));
        }
        parentElement.appendChild(passportIDElement);

        Element locationElement = document.createElement("location");
        writeLocation(document, locationElement, person.getLocation());
        parentElement.appendChild(locationElement);
      }
    } catch (Exception e) {
      throw new FileWriteException(e.getMessage());
    }
  }
}
