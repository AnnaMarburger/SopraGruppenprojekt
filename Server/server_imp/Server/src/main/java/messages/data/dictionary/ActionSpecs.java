package messages.data.dictionary;

import java.util.Objects;
import shared_data.Position;

/**
 * This class represents specifications used for actions in the message format.
 */
public class ActionSpecs {

  public final Position target;

  public ActionSpecs(final Position target) {
    this.target = target;
  }

  @Override
  public String toString() {
    return "{\n"
        + "target: " + target + "\n"
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
    ActionSpecs that = (ActionSpecs) o;
    return Objects.equals(target, that.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(target);
  }
}
