package messages.data.dictionary;

import java.util.List;
import shared_data.Position;

/**
 * This class represents specifications used for movements in the message format.
 */
public class MovementSpecs {

  public final List<Position> path;

  public MovementSpecs(final List<Position> path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return "{\n"
        + "path: " + path + "\n"
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
    MovementSpecs that = (MovementSpecs) o;
    return path.equals(that.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  public Position getLast(){
    return this.path.get(path.size()-1);
  }
}
