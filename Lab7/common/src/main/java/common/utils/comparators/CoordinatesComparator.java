package common.utils.comparators;

import common.data.Coordinates;
import java.util.Comparator;

public class CoordinatesComparator implements Comparator<Coordinates> {
  @Override
  public int compare(Coordinates coordinates1, Coordinates coordinates2) {
    int coordinatesXCompare = Float.compare(coordinates1.getX(), coordinates2.getX());
    if (coordinatesXCompare != 0) return coordinatesXCompare;

    return Long.compare(coordinates1.getY(), coordinates2.getY());
  }
}
