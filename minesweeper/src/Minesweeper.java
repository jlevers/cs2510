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
  static final ArrayList<ArrayList<Integer>> VECTORS = new ArrayList<>(Arrays.asList(
          new ArrayList<>(Arrays.asList(-1, -1)),
          new ArrayList<>(Arrays.asList(-1, 0)),
          new ArrayList<>(Arrays.asList(-1, 1)),
          new ArrayList<>(Arrays.asList(0, -1))));
  public static final int TILE_WIDTH = 16;
  public static final int OBJECT_WIDTH = TILE_WIDTH / 2;

  Random rand;
  int width;
  int height;
  int numMines;
  ArrayList<ArrayList<Tile>> grid = new ArrayList<>(this.width);
  int windowHeight;
  int windowWidth;

  Minesweeper(Random rand, int width, int height, int numMines) {
    this.rand = rand;
    this.width = width;
    this.height = height;
    this.numMines = numMines;
    this.windowHeight = this.height * Minesweeper.TILE_WIDTH;
    this.windowWidth = this.width * Minesweeper.TILE_WIDTH; 

    this.initGrid();
    this.addMines(this.numMines);
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

  // EFFECT: updates the neighbors of the tiles up and to the left of the one at the given
  // coordinates
  void updateNeighbors(Tile t, int x, int y) {
    for (ArrayList<Integer> al : Minesweeper.VECTORS) {
      int nx = x + al.get(0);
      int ny = y + al.get(1);
      if (validCoords(nx, ny)) {
        t.neighbors.add(this.tileAt(nx, ny));
      }
    }

    t.updateNeighbors();
  }

  // EFFECT: randomly adds mines to the grid
  void addMines(int remaining) {
    int minesLeft = remaining;

    for (ArrayList<Tile> al : this.grid) {
      for (Tile t : al) {
        if (minesLeft > 0 && rand.nextBoolean() && !t.mine) {
          t.mine = true;
          minesLeft -= 1;
        }
      }
    }
    if(minesLeft != 0) {
      this.addMines(minesLeft);
    }
  }

  // Checks if the given coordinates exist in this Minesweeper grid
  boolean validCoords(int x, int y) {
    return x >= 0 && x < this.width
            && y >= 0 && y < this.height;
  }

  // Retrieves the tile at the given coordinates in the grid
  Tile tileAt(int x, int y) {
    return this.grid.get(x).get(y);
  }

  // Draws the current state of the game
  public WorldScene makeScene() {
    WorldScene drawn = new WorldScene(this.windowWidth, this.windowHeight);
    
    for(int i = 0; i < this.height; i++) {
      for(int j = 0; j < this.width; j++ ) {
        int x = (j * Minesweeper.TILE_WIDTH) + (Minesweeper.TILE_WIDTH / 2);
        int y = (j * Minesweeper.TILE_WIDTH) + (Minesweeper.TILE_WIDTH / 2);
        WorldImage drawnTile = this.grid.get(i).get(j).drawTile();
        
        drawn.placeImageXY(drawnTile, x, y);
      }
    }
    return drawn;
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

  // This also tests this.updateNeighbors() and this.addMines() because we can't run initGrid()
  // without those, and we can't run either of those without running initGrid()
  void testInitGrid(Tester t) {
    init();
    t.checkExpect(this.small.grid, this.smallGrid);
  }

  void testValidCoords(Tester t) {
    init();
    t.checkExpect(this.small.validCoords(2, 1), true);
    t.checkExpect(this.small.validCoords(-1, 0), false);
    t.checkExpect(this.small.validCoords(3, 3), true);
    t.checkExpect(this.small.validCoords(4, 2), false);
  }

  void testTileAt(Tester t) {
    init();
    t.checkExpect(this.small.tileAt(0, 0), this.c00);
    t.checkExpect(this.small.tileAt(3, 2), this.c32);
  }

  void testUpdateNeighborsTile(Tester t) {
    init();
    this.c00.updateNeighbors();
    t.checkExpect(this.c01.neighbors.get(5), this.c00);
    t.checkExpect(this.c10.neighbors.get(5), this.c00);
    t.checkExpect(this.c11.neighbors.get(8), this.c00);
  }

  void testCountMines(Tester t) {
    init();
    t.checkExpect(this.c00.countMines(), 0);
    t.checkExpect(this.c21.countMines(), 3);
  }
}
