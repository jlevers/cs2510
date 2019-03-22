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
    WorldImage mineImage = new CircleImage(Minesweeper.OBJECT_WIDTH, OutlineMode.SOLID,
        Color.BLACK);

    WorldImage outlineTile = new RectangleImage(Minesweeper.TILE_WIDTH, Minesweeper.TILE_WIDTH,
        OutlineMode.OUTLINE, Color.BLACK);

    WorldImage invisTile = new OverlayImage(outlineTile, new RectangleImage(Minesweeper.TILE_WIDTH,
        Minesweeper.TILE_WIDTH, OutlineMode.SOLID, Minesweeper.HIDDEN_TILE));

    WorldImage tileImage = new OverlayImage(outlineTile, new RectangleImage(Minesweeper.TILE_WIDTH,
        Minesweeper.TILE_WIDTH, OutlineMode.SOLID, Minesweeper.VISIBLE_TILE));

    int minesNearby = this.countMines();
    WorldImage textImage = new TextImage(Integer.toString(minesNearby),
        Minesweeper.MINE_NUM_COLORS.get(minesNearby));

    WorldImage flagImage = new RectangleImage(Minesweeper.OBJECT_WIDTH, Minesweeper.OBJECT_WIDTH,
        OutlineMode.SOLID, Color.RED);

    if (!this.visible && this.flagged) {
      return new OverlayImage(flagImage, invisTile);
    }
    else {
      if (!this.visible && !this.flagged) {
        return invisTile;
      }
      else {
        if (this.visible && this.mine) {
          return new OverlayImage(mineImage, tileImage);
        }
        else {
          return new OverlayImage(textImage, tileImage);
        }
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
  
  //Images of tile properties
  WorldImage outlineTile = new RectangleImage(Minesweeper.TILE_WIDTH, Minesweeper.TILE_WIDTH,
      OutlineMode.OUTLINE, Color.BLACK);
  WorldImage visTile = new OverlayImage(outlineTile, new RectangleImage(Minesweeper.TILE_WIDTH,
      Minesweeper.TILE_WIDTH, OutlineMode.SOLID, Minesweeper.VISIBLE_TILE));
  WorldImage invisTile = new OverlayImage(outlineTile, new RectangleImage(Minesweeper.TILE_WIDTH,
      Minesweeper.TILE_WIDTH, OutlineMode.SOLID, Minesweeper.HIDDEN_TILE));
  WorldImage mineImage = new CircleImage(Minesweeper.OBJECT_WIDTH, OutlineMode.SOLID,
      Color.BLACK);
  WorldImage flagImage = new RectangleImage(Minesweeper.OBJECT_WIDTH, Minesweeper.OBJECT_WIDTH,
      OutlineMode.SOLID, Color.RED);
  
  WorldImage t0img = new OverlayImage(new TextImage("0", Minesweeper.VISIBLE_TILE),this.visTile);
  WorldImage t1img = new OverlayImage(new TextImage("1" ,Color.BLUE),this.visTile);
  WorldImage t2img = new OverlayImage(flagImage, invisTile);
  WorldImage t3img = new OverlayImage(new TextImage("2", Color.GREEN),this.visTile);
  WorldImage t4img = new OverlayImage(mineImage, visTile);
  
      
 
  
  void init() {
    t0 = new Tile(false, false, true, new ArrayList<Tile>());
    t1 = new Tile(false, false, true, new ArrayList<Tile>(Arrays.asList(t0)));
    t2 = new Tile(true, true, false, new ArrayList<Tile>(Arrays.asList(t1)));
    t3 = new Tile(false, false, true, new ArrayList<Tile>(Arrays.asList(t2)));
    t4 = new Tile(true, false, true, new ArrayList<Tile>(Arrays.asList(t3)));
  }
  
  void connectTiles() {
    t1.updateNeighbors();
    t2.updateNeighbors();
    t3.updateNeighbors();
    t4.updateNeighbors();
  }
  
  void testUpdateNeighbors(Tester t) {
    init();
    t.checkExpect(this.t1.neighbors, new ArrayList<Tile>(Arrays.asList(t0)));
    connectTiles();
    t.checkExpect(this.t1.neighbors, new ArrayList<Tile>(Arrays.asList(t0, t2)));
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
    t.checkExpect(this.t4.drawTile(), this.t4img);
  }
}
