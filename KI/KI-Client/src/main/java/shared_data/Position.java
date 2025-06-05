package shared_data;

import java.util.Objects;

import static shared_data.CONST.*;

/**
 * This class represents a simple Position with an x, and a y coordinate.
 */
public class Position {

  public int x;
  public int y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Position(Position pos) {
    this.x = pos.x;
    this.y = pos.y;
  }

  /**
   * This method randomly increases or decreases the value of the x or y by 1.
   */
  public void moveOneRandom() {
    switch (random.nextInt(8)) {
      case 0 -> x++;
      case 1 -> y++;
      case 2 -> x--;
      case 3 -> y--;
      case 4 -> {
        x++;
        y++;
      }
      case 5 -> {
        x++;
        y--;
      }
      case 6 -> {
        x--;
        y++;
      }
      case 7 -> {
        x--;
        y--;
      }
      default -> logger.severe(SHOULD_NOT_REACH_HERE);
    }
  }

  /**
   * This method copies the given position to x and y.
   *
   * @param position position that shall be copied
   */
  public void copyPosition(Position position) {
    this.x = position.x;
    this.y = position.y;
  }

  @Override
  public String toString() {
    return "{\n"
        + "\"x\": " + "\"" + x + "\"" + ",\n"
        + "\"y\": " + "\"" + y + "\"" + "\n"
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var position = (Position) o;
    return x == position.x && y == position.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}

