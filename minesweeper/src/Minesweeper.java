import java.lang.reflect.Array;
import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.Random;
import javalib.worldimages.*;

// Represents a game of Minesweeper
class Minesweeper extends World {
  static final Color VISIBLE_TILE = Color.DARK_GRAY;
  static final Color HIDDEN_TILE = Color.CYAN;
  static final ArrayList<Color> MINE_NUM_COLORS = new ArrayList<>(
          Arrays.asList(Minesweeper.VISIBLE_TILE, Color.BLUE, Color.GREEN, Color.RED, Color.PINK));

  Random rand;
  int width;
  int height;
  int numMines;
  ArrayList<ArrayList<Tile>> grid = new ArrayList<>(this.width);

  Minesweeper(Random rand, int width, int height, int numMines) {
    this.rand = rand;
    this.width = width;
    this.height = height;
    this.numMines = numMines;

    this.initGrid();
    this.addMines();
  }

  Minesweeper(int width, int height, int numMines) {
    this(new Random(), width, height, numMines);
  }

  // EFFECT: fills in the grid based on the random seed for this game
  void initGrid() {
    for (int i = 0; i < this.height; i++) {
      this.grid.add(new ArrayList<>());
      ArrayList<Tile> temp = this.grid.get(i);

      for (int j = 0; j < this.width; j++) {
        Tile tile = new Tile();
        temp.add(tile);
        this.updateNeighbors(tile, i, j);
      }
    }
  }

  // EFFECT: randomly adds mines to the grid
  void addMines() {
    int minesLeft = this.numMines;

    for (ArrayList<Tile> al : this.grid) {
      for (Tile t : al) {
        if (minesLeft > 0 && rand.nextBoolean()) {
          t.mine = true;
          minesLeft -= 1;
        }
      }
    }
  }

  // EFFECT: updates the neighbors of the tiles up and to the left of the one at the given
  // coordinates
  void updateNeighbors(Tile t, int x, int y) {
    if (x - 1 >= 0) {
      if (y - 1 >= 0) {
        t.neighbors.add(this.grid.get(x - 1).get(y - 1));
      }

      t.neighbors.add(this.grid.get(x - 1).get(y));

      if (y + 1 < this.width) {
        t.neighbors.add(this.grid.get(x - 1).get(y + 1));
      }
    }

    if (y - 1 >= 0) {
      t.neighbors.add(this.grid.get(x).get(y - 1));
    }

    t.updateNeighbors();
  }

  // Draws the current state of the game
  public WorldScene makeScene() {
    return null;
  }
}

class ExamplesMinesweeper {
  Minesweeper small;
  ArrayList<ArrayList<Tile>> smallGrid;
  Tile c00;
  Tile c01;
  Tile c02;
  Tile c03;
  Tile c10;
  Tile c11;
  Tile c12;
  Tile c13;
  Tile c20;
  Tile c21;
  Tile c22;
  Tile c23;
  Tile c30;
  Tile c31;
  Tile c32;
  Tile c33;


  void init() {
    this.small = new Minesweeper(new Random(1), 4, 4, 7);
    this.c00 = new Tile(true);
    this.c01 = new Tile(false);
    this.c02 = new Tile(false);
    this.c03 = new Tile(false);
    this.c10 = new Tile(false);
    this.c11 = new Tile(false);
    this.c12 = new Tile(false);
    this.c13 = new Tile(true);
    this.c20 = new Tile(true);
    this.c21 = new Tile(true);
    this.c22 = new Tile(false);
    this.c23 = new Tile(false);
    this.c30 = new Tile(true);
    this.c31 = new Tile(false);
    this.c32 = new Tile(true);
    this.c33 = new Tile(true);

    this.c00.neighbors = new ArrayList<>(Arrays.asList(this.c01, this.c10, this.c11));
    this.c01.neighbors = new ArrayList<>(Arrays.asList(this.c00, this.c02, this.c10,
            this.c11, this.c12));
    this.c02.neighbors = new ArrayList<>(Arrays.asList(this.c01, this.c03, this.c11,
            this.c12, this.c13));
    this.c03.neighbors = new ArrayList<>(Arrays.asList(this.c02, this.c12, this.c13));
    this.c10.neighbors = new ArrayList<>(Arrays.asList(this.c00, this.c01, this.c11,
            this.c20, this.c21));
    this.c11.neighbors = new ArrayList<>(Arrays.asList(this.c00, this.c01, this.c02,
            this.c10, this.c12, this.c20, this.c21, this.c22));
    this.c12.neighbors = new ArrayList<>(Arrays.asList(this.c01, this.c02, this.c03, this.c11,
            this.c13, this.c21, this.c22, this.c23));
    this.c13.neighbors = new ArrayList<>(Arrays.asList(this.c02, this.c03, this.c12,
            this.c22, this.c23));
    this.c20.neighbors = new ArrayList<>(Arrays.asList(this.c10, this.c11, this.c21,
            this.c30, this.c31));
    this.c21.neighbors = new ArrayList<>(Arrays.asList(this.c10, this.c11, this.c12, this.c20,
            this.c22, this.c30, this.c31, this.c32));
    this.c22.neighbors = new ArrayList<>(Arrays.asList(this.c11, this.c12, this.c13, this.c21,
            this.c23, this.c31, this.c32, this.c33));
    this.c23.neighbors = new ArrayList<>(Arrays.asList(this.c12, this.c13, this.c22,
            this.c32, this.c33));
    this.c30.neighbors = new ArrayList<>(Arrays.asList(this.c20, this.c21, this.c31));
    this.c31.neighbors = new ArrayList<>(Arrays.asList(this.c20, this.c21, this.c22, this.c30,
            this.c32));
    this.c32.neighbors = new ArrayList<>(Arrays.asList(this.c21, this.c22, this.c23, this.c31,
            this.c33));
    this.c33.neighbors = new ArrayList<>(Arrays.asList(this.c22, this.c23, this.c32));

    this.smallGrid = new ArrayList<>(Arrays.asList(
        new ArrayList<>(Arrays.asList(this.c00, this.c01, this.c02, this.c03)),
        new ArrayList<>(Arrays.asList(this.c10, this.c11, this.c12, this.c13)),
        new ArrayList<>(Arrays.asList(this.c20, this.c21, this.c22, this.c23)),
        new ArrayList<>(Arrays.asList(this.c30, this.c31, this.c32, this.c33))
    ));
  }

  void testInit(Tester t) {
    init();
    t.checkExpect(this.small.grid, this.smallGrid);
  }
//        true
//        false
//        false
//        false
//        false
//        false
//        false
//        true
//        true
//        true
//        false
//        false
//        true
//        false
//        true
//        true
}

// Represents
//class Posn {
//  int x;
//  int y;
//
//  Posn(int x, int y) {
//    this.x = x;
//    this.y = y;
//  }
//
//  // Checks if this Posn is within the bounds of the Minesweeper grid
//  boolean validPosn() {
//    return this.x >= 0 && this.x < this.width
//            && this.y >= 0 && this.y < this.height;
//  }
//
//  // Validates the given list of Posns
//  // EFFECT: removes invalid Posns
//  void validate(ArrayList<Posn> toValidate) {
//    for(int i = toValidate.size() - 1; i >= 0; i--) {
//      if (!toValidate.get(i).validPosn()) {
//        toValidate.remove(i);
//      }
//    }
//  }
//
//  // Generates all Posns up and to the left of this one, within the bounds of the Minesweeper grid
//  ArrayList<Posn> genUpLeftPosns() {
//    ArrayList<Posn> generated = new ArrayList<>(Arrays.asList(new Posn(this.x - 1, this.y - 1),
//            new Posn(this.x, this.y - 1), new Posn(this.x + 1, this.y - 1),
//            new Posn(this.x - 1, this.y)));
//    this.validate(generated);
//
//    return generated;
//  }
//}