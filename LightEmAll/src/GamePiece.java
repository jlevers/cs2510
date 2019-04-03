import tester.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javalib.worldimages.AlignModeX;
import javalib.worldimages.AlignModeY;
import javalib.worldimages.CircleImage;
import javalib.worldimages.FrameImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.OverlayOffsetAlign;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

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

  // Checks if this GamePiece is connected to the given GamePiece
  boolean connected(GamePiece other) {
    return this.left && other.right || this.top && other.bottom;
  }

  // Returns an identical GamePiece to this, but with a different row and column
  GamePiece clone(int row, int col) {
    return new GamePiece(row, col, this.left, this.right, this.top, this.bottom, this.powerStation);
  }

  // Returns an identical GamePiece to this, but with new row, column, and
  // powerStation values
  GamePiece clone(int row, int col, boolean powerStation) {
    return new GamePiece(row, col, this.left, this.right, this.top, this.bottom, powerStation);
  }

  // Draws the given GamePiece depending on its connections
  public WorldImage drawPiece() {
    WorldImage base = new FrameImage(
        new RectangleImage(GamePiece.SIZE, GamePiece.SIZE, OutlineMode.SOLID, Color.DARK_GRAY),
        Color.BLACK);
    WorldImage wireVert = new RectangleImage(GamePiece.SIZE / 5, GamePiece.SIZE / 2,
        OutlineMode.SOLID, Color.GRAY);
    WorldImage wireHoz = new RectangleImage(GamePiece.SIZE / 2, GamePiece.SIZE / 5,
        OutlineMode.SOLID, Color.GRAY);
    WorldImage ps = new CircleImage(GamePiece.SIZE / 5, OutlineMode.SOLID, Color.YELLOW);

    if (this.left) {
      base = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, wireHoz, 0, 0, base);
    }

    if (this.right) {
      base = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, wireHoz, 0, 0, base);
    }

    if (this.top) {
      base = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0, 0, base);
    }

    if (this.bottom) {
      base = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0, 0, base);
    }

    if (this.powerStation) {
      base = new OverlayImage(ps, base);
    }
    return base;
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
    t.checkExpect(this.g2.clone(4, 4, false), new GamePiece(4, 4, false, true, true, false, false));
  }

  void testPosition(Tester t) {
    init();
    t.checkExpect(this.g1.position(), new ArrayList<>(Arrays.asList(0, 0)));
    t.checkExpect(this.g2.position(), new ArrayList<>(Arrays.asList(3, 4)));
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
    WorldImage wireVert = new RectangleImage(GamePiece.SIZE / 5, GamePiece.SIZE / 2,
        OutlineMode.SOLID, Color.GRAY);
    WorldImage wireHoz = new RectangleImage(GamePiece.SIZE / 2, GamePiece.SIZE / 5,
        OutlineMode.SOLID, Color.GRAY);
    WorldImage ps = new CircleImage(GamePiece.SIZE / 5, OutlineMode.SOLID, Color.YELLOW);

    init();
    t.checkExpect(this.g1.drawPiece(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0, 0,
            new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, wireHoz, 0, 0,
                new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, wireHoz, 0, 0, base))));

    t.checkExpect(this.g2.drawPiece(),
        new OverlayImage(ps, new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0,
            0, new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, wireHoz, 0, 0, base))));
  }
}