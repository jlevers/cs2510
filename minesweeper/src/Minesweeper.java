import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.Random;
import javalib.worldimages.*;

// Represents a game of Minesweeper
class Minesweeper extends World {

  // Constants
  static final Color VISIBLE_TILE = Color.DARK_GRAY;
  static final Color HIDDEN_TILE = Color.CYAN;
  static final ArrayList<Color> MINE_NUM_COLORS = new ArrayList<>(Arrays.asList(
          Minesweeper.VISIBLE_TILE, Color.BLUE, Color.GREEN, Color.RED, Color.PINK, Color.RED,
          Color.CYAN, Color.BLACK, Color.MAGENTA));
  static final ArrayList<ArrayList<Integer>> VECTORS = new ArrayList<>(Arrays.asList(
          new ArrayList<>(Arrays.asList(-1, -1)),
          new ArrayList<>(Arrays.asList(-1, 0)),
          new ArrayList<>(Arrays.asList(-1, 1)),
          new ArrayList<>(Arrays.asList(0, -1))));
  public static final int TILE_WIDTH = 64;
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

    if (minesLeft != 0) {
      this.addMines(minesLeft);
    }
  }

  // Checks if the given coordinates exist in this Minesweeper grid
  boolean validCoords(int x, int y) {
    return x >= 0 && x < this.width
            && y >= 0 && y < this.height;
  }

  // Checks if the given Posn is within the bounds of the game window
  boolean validDrawnCoords(Posn p) {
    return p.x >= 0 && p.x <= this.windowWidth
            && p.y >= 0 && p.y <= this.windowHeight;
  }

  // Retrieves the tile at the given coordinates in the grid
  Tile tileAt(int x, int y) {
    return this.grid.get(x).get(y);
  }

  // Retrieves the tile drawn at the given Posn (pixel coordinates, not grid coordinates)
  Tile tileAtDrawnPosn(Posn p) {
    int y = p.x / Minesweeper.TILE_WIDTH;
    int x = p.y / Minesweeper.TILE_WIDTH;

    return this.tileAt(x, y);
  }

  // EFFECT: toggles the flag of the Tile at the given Posn
  void toggleFlag(Posn p) {
    if (this.validDrawnCoords(p)) {
      Tile t = this.tileAtDrawnPosn(p);
      t.toggleFlag();
    }
  }

  // EFFECT: reveals the clicked Tile, whether it's a mine or not
  void onTileClick(Posn p) {
    if (this.validDrawnCoords(p)) {
      Tile t = this.tileAtDrawnPosn(p);
      if (!t.isMine()) {
        t.flood();
      } else {
        t.setVisible();
      }
    }
  }

  // Draws the current state of the game
  public WorldScene makeScene() {
    WorldScene drawn = new WorldScene(this.windowWidth, this.windowHeight);
    
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        int x = (j * Minesweeper.TILE_WIDTH) + (Minesweeper.TILE_WIDTH / 2);
        int y = (i * Minesweeper.TILE_WIDTH) + (Minesweeper.TILE_WIDTH / 2);
        WorldImage drawnTile = this.tileAt(i, j).drawTile();
        
        drawn.placeImageXY(drawnTile, x, y);
      }
    }
    return drawn;  
  }

  // Handles user mouse input
  public void onMouseClicked(Posn pos, String buttonName) {
    if (buttonName.equals("RightButton")) {
      this.toggleFlag(pos);
    } else if (buttonName.equals("LeftButton")) {
      this.onTileClick(pos);
    }
  }

  // Checks if the world should end
  public WorldEnd worldEnds() {
    boolean mineClicked = false;
    int hidden = 0;

    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        Tile t = this.tileAt(i, j);
        if (t.exploded()) {
          return this.lost();
        }

        if (!t.visible) {
          hidden += 1;
        }
      }
    }

    if (hidden == this.numMines) {
      return this.win();
    }

    return new WorldEnd(false, this.makeScene());
  }

  // Returns a screen when a player has revealed all number tiles
  WorldEnd win() {
    WorldImage text = new TextImage("You win!", Color.BLUE);
    WorldScene textscene = new WorldScene(this.windowWidth, this.windowHeight);
    textscene.placeImageXY(text, this.windowWidth / 2, this.windowHeight / 2);
    
    return new WorldEnd(true, textscene);
  }

  // Returns a WorldScene for when the player hits a mine
  // I'll make this actually return the drawn mines later
  WorldEnd lost() {
    WorldImage text = new TextImage("You Lose!", Color.RED);
    WorldScene textscene = new WorldScene(this.windowWidth, this.windowHeight);
    textscene.placeImageXY(text, this.windowWidth / 2, this.windowHeight / 2);
    
    return new WorldEnd(true, textscene);
  }
}

class ExamplesMinesweeper {
  Minesweeper small;
  Minesweeper shown;
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
    this.shown = new Minesweeper(new Random(1), 4, 4, 7);
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

  void testValidDrawnCoords(Tester t) {
    init();
    t.checkExpect(this.small.validDrawnCoords(new Posn(20, 53)), true);
    t.checkExpect(this.small.validDrawnCoords(new Posn(0, 64)), true);
    t.checkExpect(this.small.validDrawnCoords(new Posn(-5, 20)), false);
    t.checkExpect(this.small.validDrawnCoords(new Posn(30, 70)), false);
  }

  void testTileAt(Tester t) {
    init();
    t.checkExpect(this.small.tileAt(0, 0), this.c00);
    t.checkExpect(this.small.tileAt(3, 2), this.c32);
  }

  void testTileAtDrawnPosn(Tester t) {
    init();
    t.checkExpect(this.small.tileAtDrawnPosn(new Posn(63, 18)), this.c13);
    t.checkExpect(this.small.tileAtDrawnPosn(new Posn(43, 8)), this.c02);
  }

  void testMakeScene(Tester t) {
    init();
    this.small.tileAt(2, 2).visible = true;
    this.small.tileAt(1, 0).flagged = true;

    WorldScene gameImage = new WorldScene(64, 64);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 8, 8);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 24, 8);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 40, 8);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 56, 8);
    gameImage.placeImageXY(new OverlayImage(Tile.FLAG_IMAGE, Tile.HIDDEN_TILE), 8, 24);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 24, 24);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 40, 24);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 56, 24);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 8, 40);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 24, 40);
    gameImage.placeImageXY(new OverlayImage(new TextImage("4", Color.PINK),
            Tile.TILE_IMAGE), 40, 40);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 56, 40);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 8, 56);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 24, 56);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 40, 56);
    gameImage.placeImageXY(Tile.HIDDEN_TILE, 56, 56);

    t.checkExpect(this.small.makeScene(), gameImage);
  }

  void testBigBang(Tester t) {
    init();
    this.small.bigBang(this.small.windowWidth, this.small.windowHeight, 1);
  }
}
