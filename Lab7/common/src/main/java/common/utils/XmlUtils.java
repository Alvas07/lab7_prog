package common.utils;

import common.exceptions.ObjectCreationException;
import java.util.function.Function;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Класс, предоставляющий вспомогательные методы для работы с форматом XML.
 *
 * @author Alvas
 * @since 1.0
 */
public final class XmlUtils {
  /**
   * Возвращает значение типа {@link T} из указанного тега XML-элемента.
   *
   * @param element XML-элемент {@link Element}.
   * @param tagName имя XML-тега.
   * @param parser функция-парсер.
   * @return Значение типа {@link T}.
   * @param <T> Тип возвращаемого значения.
   * @see Element
   * @author Alvas
   * @since 2.0
   */
  public static <T> T getValue(Element element, String tagName, Function<String, T> parser) {
    NodeList nodeList = element.getElementsByTagName(tagName);
    String text = nodeList.item(0).getTextContent().trim();
    if (text.isEmpty()) {
      return null;
    }
    return parser.apply(text);
  }

  /**
   * Возвращает значение перечисления из указанного тега XML-элемента.
   *
   * @param element XML-элемент {@link Element}.
   * @param tagName имя XML-тега.
   * @param enumClass класс перечисления.
   * @return Перечисление.
   * @param <T> тип возвращаемого перечисления.
   * @see Element
   * @throws ObjectCreationException если формат входных данных типа некорректен.
   * @author Alvas
   * @since 2.0
   */
  public static <T extends Enum<T>> T getEnum(Element element, String tagName, Class<T> enumClass)
      throws ObjectCreationException {
    try {
      String text = getValue(element, tagName, s -> s);
      return Enum.valueOf(enumClass, text.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new ObjectCreationException("Некорректный формат типа.");
    }
  }
}
