import java.awt.Color;
import java.util.ArrayList;

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

  //Draws the given tile
  public WorldImage drawTile() {
    WorldImage mineImage =
        new CircleImage(Minesweeper.OBJECT_WIDTH, OutlineMode.SOLID, Color.BLACK);
    
    WorldImage invisTile =
        new RectangleImage(Minesweeper.TILE_WIDTH, Minesweeper.TILE_WIDTH, OutlineMode.SOLID,
            Minesweeper.HIDDEN_TILE);
    
    WorldImage tileImage = 
        new RectangleImage(Minesweeper.TILE_WIDTH, Minesweeper.TILE_WIDTH, OutlineMode.SOLID,
        Minesweeper.VISIBLE_TILE);
    
    int minesNearby = this.countMines();
    WorldImage textImage = 
        new TextImage(Integer.toString(minesNearby), 
            Minesweeper.MINE_NUM_COLORS.get(minesNearby));
    
    WorldImage flagImage = new RectangleImage(Minesweeper.OBJECT_WIDTH, Minesweeper.OBJECT_WIDTH,
        OutlineMode.SOLID, Color.RED);
    
    if(!this.visible && this.flagged) {
      return new OverlayImage(flagImage, invisTile);
    } else {
      if(!this.visible && !this.flagged) {
        return invisTile;
      } else {
        if(this.visible && this.mine) {
          return new OverlayImage(mineImage, tileImage);
        } else {
          return new OverlayImage(textImage, tileImage);
        }
      }
    }
  }
}
