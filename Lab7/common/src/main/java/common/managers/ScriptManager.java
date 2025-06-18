package common.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Класс, отвечающий за управление выполнением скриптов.
 *
 * <p>Предоставляет вспомогательные методы для контроля процесса выполнения команды
 * "execute_script".
 *
 * <p>Хранит все запущенные скрипты и сканеры для них в соответствующих коллекциях типа {@link
 * Stack}, а также флаг {@code fileMode}, показывающий, находится ли программа в режиме выполнения
 * скрипта.
 *
 * @see Stack
 * @author Alvas
 * @since 1.0
 */
public class ScriptManager {
  private final Stack<String> fileNames;
  private final Stack<Scanner> scanners;
  private boolean fileMode;
  private final ScannerManager scannerManager;

  /**
   * Конструктор менеджера выполнения скриптов.
   *
   * <p>По умолчанию создает два пустых {@link Stack} для хранения всех запущенных скриптов и
   * сканеров для них, а также устанавливает флаг {@code fileMode} в значение {@code false}.
   *
   * @param scannerManager менеджер сканеров
   * @see ScannerManager
   * @author Alvas
   * @since 2.0
   */
  public ScriptManager(ScannerManager scannerManager) {
    this.fileNames = new Stack<>();
    this.scanners = new Stack<>();
    this.fileMode = false;
    this.scannerManager = scannerManager;
  }

  public ScannerManager getScannerManager() {
    return scannerManager;
  }

  /**
   * Показывает, является ли скрипт рекурсивным.
   *
   * @param fileName путь к файлу со скриптом.
   * @return {@code true} - если в скрипте возникает рекурсия, {@code false} - если нет.
   * @author Alvas
   * @since 1.0
   */
  public boolean isRecursive(String fileName) {
    return fileNames.contains(new File(fileName).getAbsolutePath());
  }

  /**
   * Добавляет путь к файлу с новым скриптом в {@link Stack}.
   *
   * <p>Также добавляет в другой {@link Stack} соответствующий этому скрипту сканер.
   *
   * @param fileName путь к файлу со скриптом.
   * @throws FileNotFoundException если файл со скриптом не найден.
   * @see Stack
   * @author Alvas
   * @since 1.0
   */
  public void addPath(String fileName) throws FileNotFoundException {
    fileNames.push(new File(fileName).getAbsolutePath());
    scanners.push(new Scanner(new File(fileName)));
  }

  /**
   * Удаляет последний путь к файлу со скриптом из {@link Stack}.
   *
   * <p>Также удаляет из другого {@link Stack} соответствующий этому скрипту сканер.
   *
   * @see Stack
   * @author Alvas
   * @since 1.0
   */
  public void removePath() {
    fileNames.pop();
    scanners.pop();
  }

  /**
   * Возвращает последний запущенный из скрипта сканер.
   *
   * @return Последний запущенный сканер.
   * @author Alvas
   * @since 1.0
   */
  public Scanner getLastScanner() {
    return scanners.lastElement();
  }

  /**
   * Возвращает все запущенные сканеры в виде {@link Stack}.
   *
   * @return Все запущенные сканеры.
   * @see Stack
   * @author Alvas
   * @since 1.0
   */
  public Stack<Scanner> getAllScanners() {
    return scanners;
  }

  /**
   * Показывает, находится ли программа в режиме выполнения скрипта.
   *
   * @return {@code true} - если программа в данный момент выполняет скрипт, {@code false} - если
   *     нет.
   * @author Alvas
   * @since 1.0
   */
  public boolean getFileMode() {
    return fileMode;
  }

  /**
   * Переводит программу в режим выполнения скрипта.
   *
   * @author Alvas
   * @since 1.0
   */
  public void activateFileMode() {
    fileMode = true;
  }

  /**
   * Выводит программу из режима выполнения скрипта.
   *
   * @author Alvas
   * @since 1.0
   */
  public void deactivateFileMode() {
    fileMode = false;
  }

  /**
   * Выполняет проверку возможности чтения из текущего скрипта.
   *
   * <p>Если скрипт заканчивается, то переходит на предыдущий запущенный вплоть до ручного
   * пользовательского ввода.
   *
   * @author Alvas
   * @since 2.0
   */
  public void scriptCheck() {
    if (fileMode) {
      try {
        scannerManager.setScanner(getLastScanner());
        if (!scannerManager.getScanner().hasNextLine()) {
          throw new NoSuchElementException();
        }
      } catch (NoSuchElementException e) {
        removePath();
        if (getAllScanners().isEmpty()) {
          scannerManager.setScanner(new Scanner(System.in));
          deactivateFileMode();
        } else {
          scannerManager.setScanner(getLastScanner());
        }
      }
    } else {
      scannerManager.setScanner(new Scanner(System.in));
      deactivateFileMode();
    }
  }
}
