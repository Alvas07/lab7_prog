package common.managers;

import java.util.Scanner;

/**
 * Класс, отвечающий за взаимодействие со сканерами.
 *
 * @see Scanner
 * @author Alvas
 * @since 1.0
 */
public class ScannerManager {
  private Scanner scanner;

  /**
   * Конструктор менеджера сканеров.
   *
   * @param scanner используемый сканер.
   * @see Scanner
   * @author Alvas
   * @since 2.0
   */
  public ScannerManager(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Возвращает текущий установленный в программе сканер.
   *
   * @return Текущий сканер.
   * @author Alvas
   * @since 1.0
   */
  public Scanner getScanner() {
    return scanner;
  }

  /**
   * Устанавливает для программы заданный сканер.
   *
   * @param scanner новый сканер.
   * @author Alvas
   * @since 1.0
   */
  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }
}
