package shared_data;

/**
 * This class represents a simple generic tupel.
 * @param <T>
 * @param <K>
 */
public class Tupel<T,K> {

  public final T first;
  public final K second;


  public Tupel(T first, K second) {
    this.first = first;
    this.second = second;
  }
}
