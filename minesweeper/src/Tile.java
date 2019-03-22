import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;
import tester.*;

// Represents a tile in Minesweeper
class Tile {
  boolean mine;
  boolean flagged;
  boolean visible;
  ArrayList<Tile> neighbors;

  static final WorldImage MINE_IMAGE = new CircleImage(Minesweeper.OBJECT_WIDTH, OutlineMode.SOLID,
          Color.BLACK);
  static final WorldImage OUTLINE_TILE = new RectangleImage(Minesweeper.TILE_WIDTH,
          Minesweeper.TILE_WIDTH, OutlineMode.OUTLINE, Color.BLACK);
  static final WorldImage HIDDEN_TILE = new OverlayImage(Tile.OUTLINE_TILE,
          new RectangleImage(Minesweeper.TILE_WIDTH,
          Minesweeper.TILE_WIDTH, OutlineMode.SOLID, Minesweeper.HIDDEN_TILE));
  static final WorldImage TILE_IMAGE = new OverlayImage(Tile.OUTLINE_TILE,
          new RectangleImage(Minesweeper.TILE_WIDTH,
          Minesweeper.TILE_WIDTH, OutlineMode.SOLID, Minesweeper.VISIBLE_TILE));
  static final WorldImage FLAG_IMAGE = new RectangleImage(Minesweeper.OBJECT_WIDTH,
          Minesweeper.OBJECT_WIDTH, OutlineMode.SOLID, Color.RED);

  Tile() {
    this(false);
  }

  Tile(boolean mine) {
    this(mine, new ArrayList<>());
  }

  Tile(boolean mine, ArrayList<Tile> neighbors) {
    this(mine, false, false, neighbors);
  }
  
  Tile(boolean mine, boolean flagged, boolean visible, ArrayList<Tile> neighbors){
    this.mine = mine;
    this.flagged = flagged;
    this.visible = visible;
    this.neighbors = neighbors;
  }

  // Updates this tile's neighbors
  void updateNeighbors() {
    for (Tile t : this.neighbors) {
      t.neighbors.add(this);
    }
  }

  // Calculates the number of mines surrounding this tile
  int countMines() {
    int mines = 0;
    for(Tile t : this.neighbors){
      if (t.mine) {
        mines += 1;
      }
    }
    return mines;
  }

  // Draws the given tile
  public WorldImage drawTile() {
       int minesNearby = this.countMines();
    WorldImage textImage = new TextImage(Integer.toString(minesNearby),
        Minesweeper.MINE_NUM_COLORS.get(minesNearby));

    if (!this.visible && this.flagged) {
      return new OverlayImage(Tile.FLAG_IMAGE, Tile.HIDDEN_TILE);
    } else {

      if (!this.visible && !this.flagged) {
        return Tile.HIDDEN_TILE;
      } else {
        return new OverlayImage(textImage, Tile.TILE_IMAGE);
//        if (this.visible && this.mine) {
//          return new OverlayImage(Tile.MINE_IMAGE, Tile.TILE_IMAGE);
//        } else {
//          return new OverlayImage(textImage, Tile.TILE_IMAGE);
//        }
      }

    }
  }
}

class ExamplesTile {
  Tile t0;
  Tile t1;
  Tile t2;
  Tile t3;
  Tile t4;
  Tile test;
  
  WorldImage t0img = new OverlayImage(new TextImage("0", Minesweeper.VISIBLE_TILE),
          Tile.TILE_IMAGE);
  WorldImage t1img = new OverlayImage(new TextImage("1" ,Color.BLUE), Tile.TILE_IMAGE);
  WorldImage t2img = new OverlayImage(Tile.FLAG_IMAGE, Tile.HIDDEN_TILE);
  WorldImage t3img = new OverlayImage(new TextImage("2", Color.GREEN), Tile.TILE_IMAGE);
  WorldImage t4img = new OverlayImage(Tile.MINE_IMAGE, Tile.TILE_IMAGE);
  
  void init() {
    t0 = new Tile(false, false, true, new ArrayList<>());
    t1 = new Tile(false, false, true, new ArrayList<>(Arrays.asList(t0)));
    t2 = new Tile(true, true, false, new ArrayList<>(Arrays.asList(t1)));
    t3 = new Tile(false, false, true, new ArrayList<>(Arrays.asList(t2)));
    t4 = new Tile(true, false, true, new ArrayList<>(Arrays.asList(t3)));
  }
  
  void connectTiles() {
    t1.updateNeighbors();
    t2.updateNeighbors();
    t3.updateNeighbors();
    t4.updateNeighbors();
  }
  
  void testUpdateNeighbors(Tester t) {
    init();
    t.checkExpect(this.t1.neighbors, new ArrayList<>(Arrays.asList(t0)));
    connectTiles();
    t.checkExpect(this.t1.neighbors, new ArrayList<>(Arrays.asList(t0, t2)));
  }
  
  void testCountMines(Tester t) {
    init();
    connectTiles();
    t.checkExpect(this.t0.countMines(), 0);
    t.checkExpect(this.t1.countMines(), 1);
    t.checkExpect(this.t3.countMines(), 2);
  }
  
  void testDrawTile(Tester t) {
    init();
    connectTiles();
    t.checkExpect(this.t0.drawTile(), this.t0img);
    t.checkExpect(this.t1.drawTile(), this.t1img);
    t.checkExpect(this.t2.drawTile(), this.t2img);
    t.checkExpect(this.t3.drawTile(), this.t3img);
//    t.checkExpect(this.t4.drawTile(), this.t4img);
  }
}
