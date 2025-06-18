package common.managers;

import common.data.Ticket;
import common.exceptions.FileReadException;
import common.exceptions.FileWriteException;
import common.io.XmlReader;
import common.io.XmlWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Класс, отвечающий за взаимодействие с файлами.
 *
 * <p>Осуществляет работу с файлами формата XML с помощью классов {@link XmlReader} и {@link
 * XmlWriter}.
 *
 * @see XmlReader
 * @see XmlWriter
 * @author Alvas
 * @since 1.0
 */
public class FileManager {
  private final String fileName;
  private final XmlReader reader = new XmlReader();
  private final XmlWriter writer = new XmlWriter();

  /**
   * Конструктор файлового менеджера.
   *
   * @param fileName путь к файлу.
   * @see CollectionManager
   * @author Alvas
   * @since 2.0
   */
  public FileManager(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Возвращает путь до используемого файла.
   *
   * @return Путь до используемого файла.
   * @author Alvas
   * @since 2.0
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Возвращает время создания используемого файла.
   *
   * @return Время создания файла.
   * @author Alvas
   * @since 2.0
   */
  public LocalDateTime getFileCreationTime() {
    try {
      FileTime time = (FileTime) Files.getAttribute(Paths.get(fileName), "creationTime");
      return LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
    } catch (IOException e) {
      System.out.println("Невозможно прочитать время создания файла.");
      return null;
    }
  }

  /**
   * Возвращает время последней модификации используемого файла.
   *
   * @return Время последней модификации файла.
   * @author Alvas
   * @since 2.0
   */
  public LocalDateTime getFileLastModifiedTime() {
    try {
      FileTime time = Files.getLastModifiedTime(Paths.get(fileName));
      return LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
    } catch (IOException e) {
      System.out.println("Невозможно прочитать время последней модификации файла.");
      return null;
    }
  }

  /**
   * Показывает возможность чтения из файла.
   *
   * @return {@code true} - если файл доступен для чтения, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public boolean canRead() {
    return reader.canRead(fileName);
  }

  /**
   * Показывает возможность записи в файл.
   *
   * @return {@code true} - если файл доступен для записи, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public boolean canWrite() {
    return writer.canWrite(fileName);
  }

  /**
   * Заполняет коллекцию {@link CollectionManager} данными из файла формата XML с помощью {@link
   * XmlReader}.
   *
   * @throws FileReadException если файл невозможно прочитать.
   * @see CollectionManager
   * @see XmlReader
   * @author Alvas
   * @since 1.0
   */
  public void fillCollectionFromXml(CollectionManager collectionManager) throws FileReadException {
    List<Ticket> tickets = reader.readTickets(fileName);
    collectionManager.fillCollection(tickets);
  }

  /**
   * Сохраняет коллекцию {@link CollectionManager} в файл формата XML с помощью {@link XmlWriter}.
   *
   * @throws FileWriteException если в файл невозможно записать.
   * @see CollectionManager
   * @see XmlWriter
   * @author Alvas
   * @since 1.0
   */
  public void saveCollectionToXml(CollectionManager collectionManager) throws FileWriteException {
    writer.writeTicketsToFile(fileName, collectionManager.getTicketsList());
  }
}
