import java.util.ArrayList;
import tester.*;

// Represents a tile in Minesweeper
class Tile {
  boolean mine;
  boolean flagged;
  boolean visible;
  ArrayList<Tile> neighbors;

  Tile() {
    this(false);
  }

  Tile(boolean mine) {
    this(mine, new ArrayList<>());
  }

  Tile(boolean mine, ArrayList<Tile> neighbors) {
    this.mine = mine;
    this.flagged = false;
    this.visible = false;
    this.neighbors = neighbors;
  }

  // Updates this tile's neighbors
  void updateNeighbors() {
    for (Tile t : this.neighbors) {
      t.neighbors.add(this);
    }
  }
}

class ExamplesTile {

}
