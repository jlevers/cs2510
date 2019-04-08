import tester.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javalib.worldimages.*;

class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;

  static int SIZE = 50;
  static Color BG = Color.darkGray;
  static Color WIRE_COLOR = Color.lightGray;
  static int WIRE_WIDTH = GamePiece.SIZE / 15;
  static int WIRE_LENGTH = GamePiece.SIZE / 2;
  static OutlineMode OUTLINE_MODE = OutlineMode.SOLID;
  static WorldImage POWER_STATION = new StarImage(GamePiece.SIZE / 2, 7, GamePiece.OUTLINE_MODE,
          Color.BLUE);

  GamePiece(int row, int col, boolean left, boolean right, boolean top, boolean bottom,
      boolean powerStation) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
  }

  // Gives an ArrayList<Integer> representing this GamePiece's position
  ArrayList<Integer> position() {
    return new ArrayList<>(Arrays.asList(this.row, this.col));
  }

  // EFFECT: makes this GamePiece have a power station if it doesn't, otherwise removes it
  void togglePowerStation() {
    this.powerStation = !this.powerStation;
  }

  // Checks if this GamePiece is connected to the given GamePiece
  boolean connected(GamePiece other) {
    return this.left && other.right || this.top && other.bottom;
  }

  // Returns an identical GamePiece to this, but with a different row and column
  GamePiece clone(int row, int col) {
    return new GamePiece(row, col, this.left, this.right, this.top, this.bottom, this.powerStation);
  }

  // Draws this GamePiece
  public WorldImage drawPiece() {
    WorldImage base = new FrameImage(
            new RectangleImage(GamePiece.SIZE, GamePiece.SIZE, GamePiece.OUTLINE_MODE,
                    GamePiece.BG), Color.BLACK);
    WorldImage wireVert =
            new RectangleImage(GamePiece.WIRE_WIDTH, GamePiece.WIRE_LENGTH, GamePiece.OUTLINE_MODE,
                    GamePiece.WIRE_COLOR);
    WorldImage wireHoriz =
            new RectangleImage(GamePiece.WIRE_LENGTH, GamePiece.WIRE_WIDTH, GamePiece.OUTLINE_MODE,
                    GamePiece.WIRE_COLOR);

    ArrayList<Boolean> sides = new ArrayList<>(Arrays.asList(
            this.left, this.right, this.top, this.bottom));
    ArrayList<AlignModeX> alignXWires = new ArrayList<>(Arrays.asList(
            AlignModeX.LEFT, AlignModeX.RIGHT, AlignModeX.CENTER, AlignModeX.CENTER));
    ArrayList<AlignModeY> alignYWires = new ArrayList<>(Arrays.asList(
            AlignModeY.MIDDLE, AlignModeY.MIDDLE, AlignModeY.TOP, AlignModeY.BOTTOM));

    // Adds left/right/top/bottom parts of the wiring if this is connected on those sides
    for (int i = 0; i < sides.size(); i++) {
      if (sides.get(i)) {
        WorldImage wire = i < 2 ? wireHoriz : wireVert;
        base = new OverlayOffsetAlign(alignXWires.get(i), alignYWires.get(i), wire, 0, 0, base);
      }
    }

    if (this.powerStation) {
      return new OverlayImage(GamePiece.POWER_STATION, base);
    }

    return base;
  }

  // EFFECT: rotates this GamePiece clockwise
  void rotate() {
    boolean oldLeft = this.left;
    boolean oldTop = this.top;
    boolean oldRight = this.right;

    this.left = this.bottom;
    this.top = oldLeft;
    this.right = oldTop;
    this.bottom = oldRight;
  }
  
}

class ExamplesGamePiece {
  GamePiece g1;
  GamePiece g2;
  GamePiece g3;

  void init() {
    this.g1 = new GamePiece(0, 0, true, true, false, true, false);
    this.g2 = new GamePiece(3, 4, false, true, true, false, true);
    this.g3 = new GamePiece(1, 0, true, false, false, false, false);
  }

  void testClone(Tester t) {
    init();
    t.checkExpect(this.g1.clone(2, 1), new GamePiece(2, 1, true, true, false, true, false));
    t.checkExpect(this.g2.clone(4, 4), new GamePiece(4, 4, false, true, true, false, true));
  }

  void testPosition(Tester t) {
    init();
    t.checkExpect(this.g1.position(), new ArrayList<>(Arrays.asList(0, 0)));
    t.checkExpect(this.g2.position(), new ArrayList<>(Arrays.asList(3, 4)));
  }

  void testTogglePowerStation(Tester t) {
    init();
    this.g2.togglePowerStation();
    t.checkExpect(this.g2.powerStation, false);
    this.g2.togglePowerStation();
    t.checkExpect(this.g2.powerStation, true);
  }

  void testConnected(Tester t) {
    init();
    t.checkExpect(this.g1.connected(this.g2), true);
    t.checkExpect(this.g1.connected(this.g3), false);
    t.checkExpect(this.g3.connected(this.g1), true);
  }

  void testDrawPiece(Tester t) {
    WorldImage base = new FrameImage(
        new RectangleImage(GamePiece.SIZE, GamePiece.SIZE, OutlineMode.SOLID, Color.DARK_GRAY),
        Color.BLACK);
    WorldImage wireVert = new RectangleImage(GamePiece.WIRE_WIDTH, GamePiece.WIRE_LENGTH,
        OutlineMode.SOLID, GamePiece.WIRE_COLOR);
    WorldImage wireHoz = new RectangleImage(GamePiece.WIRE_LENGTH, GamePiece.WIRE_WIDTH,
        OutlineMode.SOLID, GamePiece.WIRE_COLOR);

    init();
    t.checkExpect(this.g1.drawPiece(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0, 0,
            new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, wireHoz, 0, 0,
                new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, wireHoz, 0, 0, base))));

    t.checkExpect(this.g2.drawPiece(),
        new OverlayImage(GamePiece.POWER_STATION, 
            new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0,
            0, new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, wireHoz, 0, 0, base))));
  }

  void testRotate(Tester t) {
    init();
    this.g1.rotate();
    t.checkExpect(this.g1, new GamePiece(0, 0, true, false, true, true, false));
    this.g1.rotate();
    t.checkExpect(this.g1, new GamePiece(0, 0, true, true, true, false, false));
  }
}