import tester.Tester;

import java.util.Comparator;

// Represents an edge between two GamePieces
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;

  Edge(GamePiece fromNode, GamePiece toNode, int weight) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.weight = weight;
  }
}

// Represents a function to compare edges by weight
class CompEdgeWeight implements Comparator<Edge> {
  // Compares the edges by weight
  public int compare(Edge edge1, Edge edge2) {
    return edge1.weight - edge2.weight;
  }
}

class ExamplesEdges {
  Comparator<Edge> comp = new CompEdgeWeight();
  GamePiece g1 = new GamePiece(3, 4, true, true, true, true, false);
  GamePiece g2 = new GamePiece(3, 5, true, true, true, true, false);
  Edge e1 = new Edge(this.g1, this.g2, 12);
  Edge e2 = new Edge(this.g1, this.g2, 53);

  void testCompEdgeWeight(Tester t) {
    t.checkExpect(this.comp.compare(this.e1, this.e2), -41);
    t.checkExpect(this.comp.compare(this.e2, this.e1), 41);
    t.checkExpect(this.comp.compare(this.e1, this.e1), 0);
  }
}