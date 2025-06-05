package server.gameobjects.gameboard.dijkstra;

import shared_data.Position;


public class Node {

  public final Position position;
  public final int distanceFromSource;

  public Node(int x, int y, int dis, Node node) {
    this.position = new Position(x, y);
    this.prefNode = node;
    this.distanceFromSource = dis;
  }

  public final Node prefNode;

  @Override
  public String toString() {
    return "Node{" +
        "position=" + position +
        ", distanceFromSource=" + distanceFromSource +
        '}';
  }
}
