package server.gameobjects.gameboard.dijkstra;

import shared_data.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MapAlgorithms {

  private MapAlgorithms() {
  }

  public static List<Position> shortestPath(Position start, Position end, char[][] charMap) {
    List<Position> positions = new ArrayList<>();
    charMap[end.y][end.x] = 'D';
    Node n = MapAlgorithms.pathExists(charMap, start);
    if (n == null) {
      return positions;
    }
    while (n.prefNode != null) {
      positions.add(n.position);
      n = n.prefNode;
    }
    return positions;
  }


  private static List<Node> addNeighbors(Node poped, char[][] matrix) {
    List<Node> posList = new ArrayList<>();
    for (int yy = -1; yy <= 1; yy++) {
      for (int xx = -1; xx <= 1; xx++) {
        if (xx == 0 && yy == 0) {
          continue; // You are not neighbor to yourself
        }
        if (isTraversable(poped.position.x + xx, poped.position.y + yy, matrix)) {
          posList.add(
              new Node(poped.position.x + xx, poped.position.y + yy, poped.distanceFromSource + 1,
                  poped));
        }
      }
    }
    return posList;
  }

  private static boolean isTraversable(int x, int y, char[][] matrix) {
    return (isOnMap(x, y, matrix) && matrix[y][x] != '0');
  }

  private static boolean isOnMap(int x, int y, char[][] matrix) {
    return x >= 0 && y >= 0 && x < matrix[0].length && y < matrix.length;
  }

  public static List<Node> getRadiusWithMinDistance(int vIndex, int hIndex, Position start,
      int radius, int minDistance) {
    char[][] matrix = createCharMap(vIndex, hIndex);
    List<Node> nodes = new ArrayList<>();
    var source = new Node(start.x, start.y, 0, null);
    Queue<Node> queue = new LinkedList<>();

    queue.add(source);
    while (!queue.isEmpty()) {
      Node poped = queue.poll();
      if (matrix[poped.position.y][poped.position.x] == '0') {
        continue;
      }

      if (poped.distanceFromSource >= radius) {
        break;
      }

      matrix[poped.position.y][poped.position.x] = '0';
      List<Node> neighbourList = addNeighbors(poped, matrix);
      queue.addAll(neighbourList);
      if (poped.distanceFromSource >= minDistance) {
        nodes.addAll(neighbourList);
      }
    }
    return nodes;
  }

  private static char[][] createCharMap(int vIndex, int hIndex) {
    var map = new char[vIndex][hIndex];
    for (var y = 0; y < vIndex; y++) {
      for (var x = 0; x < hIndex; x++) {
        map[y][x] = '1';
      }
    }
    return map;
  }

  private static Node pathExists(char[][] matrix, Position start) {
    var source = new Node(start.x, start.y, 0, null);
    Queue<Node> queue = new LinkedList<>();

    queue.add(source);
    matrix[source.position.y][source.position.x] = '1';
    while (!queue.isEmpty()) {
      Node poped = queue.poll();
      if (matrix[poped.position.y][poped.position.x] == '0') {
        continue;
      }
      if (matrix[poped.position.y][poped.position.x] == 'D') {
        return poped;
      } else {
        matrix[poped.position.y][poped.position.x] = '0';
        List<Node> neighbourList = addNeighbors(poped, matrix);
        queue.addAll(neighbourList);
      }
    }
    return null;
  }

}
