import java.lang.reflect.Array;
import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.util.Arrays;
import javalib.worldimages.*;

class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;

  LightEmAll(int width, int height) {
    this.width = width;
    this.height = height;

    this.generateFractalBoard();
    this.nodes = Utils.flatten(this.board);
  }

  // EFFECT: generates a fractal board layout
  void generateFractalBoard() {
    this.board = generateFractalBoardHelp(0, 0, this.width - 1, this.height - 1);
  }

  // EFFECT: generates a fractal board layout recursively
  ArrayList<ArrayList<GamePiece>> generateFractalBoardHelp(int colLow, int rowLow, int colHigh,
                                     int rowHigh) {
     int colDiff = colHigh - colLow;
    int rowDiff = rowHigh - rowLow;
    if (colDiff == 1 && rowDiff == 1) {
      // Base case
      GamePiece tl = new GamePiece(rowLow, colLow, false, false, false, true, false);
      GamePiece bl = new GamePiece(rowLow, colHigh, false, true, true, false, false);
      GamePiece tr = new GamePiece(rowHigh, colLow, false, false, false, true, false);
      GamePiece br = new GamePiece(rowHigh, colHigh, true, false, true, false, false);

      return new ArrayList<>(Arrays.asList(
              new ArrayList<>(Arrays.asList(tl, bl)),
              new ArrayList<>(Arrays.asList(tr, br))));
    } else if (colDiff == 0 && rowDiff == 1) {
      GamePiece top = new GamePiece(rowLow, colLow, false, false, false, true, false);
      GamePiece bottom = new GamePiece(rowHigh, colLow, false, false, true, false, false);

      return new ArrayList<>(Arrays.asList(new ArrayList<>(Arrays.asList(top, bottom))));
    } else if (colDiff == 1 && rowDiff == 0) {
      GamePiece left = new GamePiece(rowLow, colLow, false, true, false, false, false);
      GamePiece right = new GamePiece(rowLow, colHigh, true, false, false, false, false);

      return new ArrayList<>(Arrays.asList(
              new ArrayList<>(Arrays.asList(left)), new ArrayList<>(Arrays.asList(right))
      ));
    } else if (colDiff == 0 && rowDiff == 0) {
      return new ArrayList<>(Arrays.asList(new ArrayList<>(
              Arrays.asList(new GamePiece(rowLow, colLow, false, false, false, false, false)))));
    } else if (colDiff == 2 && rowDiff == 1) {
      ArrayList<ArrayList<GamePiece>> rect = mergeHoriz(
              generateFractalBoardHelp(colLow, rowLow, colLow + 1, rowHigh),
              generateFractalBoardHelp(colHigh, rowLow, colHigh, rowHigh));
      rect.get(1).get(1).right = true;
      rect.get(2).get(1).left = true;
      return rect;
    } else {
      int colAvg = (colHigh + colLow) / 2;
      int rowAvg = (rowHigh + rowLow) / 2;
      return mergeFractals(
              generateFractalBoardHelp(colLow, rowLow, colAvg, rowAvg),
              generateFractalBoardHelp(colLow, rowAvg + 1, colAvg, rowHigh),
              generateFractalBoardHelp(colAvg + 1, rowLow, colHigh, rowAvg),
              generateFractalBoardHelp(colAvg + 1, rowAvg + 1, colHigh, rowHigh));
    }

  }

  // Merges together two fractal boards
  ArrayList<ArrayList<GamePiece>> mergeFractals(ArrayList<ArrayList<GamePiece>> tl,
                                                ArrayList<ArrayList<GamePiece>> bl,
                                                ArrayList<ArrayList<GamePiece>> tr,
                                                ArrayList<ArrayList<GamePiece>> br) {
    // Connect top left with bottom left
    tl.get(0).get(tl.get(0).size() - 1).bottom = true;
    bl.get(0).get(0).top = true;
    // Connect top right with bottom right
    tr.get(tr.size() - 1).get(tr.get(0).size() - 1).bottom = true;
    br.get(tr.size() - 1).get(0).top = true;
    // Connect bottom left with bottom right
    bl.get(bl.size() - 1).get(bl.get(0).size() - 1).right = true;
    br.get(0).get(br.get(0).size() - 1).left = true;

    ArrayList<ArrayList<GamePiece>> left = mergeVert(tl, bl);
    ArrayList<ArrayList<GamePiece>> right = mergeVert(tr, br);
    return mergeHoriz(left, right);
  }

  // Merges two boards horizontally
  ArrayList<ArrayList<GamePiece>> mergeHoriz(ArrayList<ArrayList<GamePiece>> left,
                                             ArrayList<ArrayList<GamePiece>> right) {
    // Initialize rows
    ArrayList<ArrayList<GamePiece>> joined = new ArrayList<>();
    for (int i = 0; i < left.size() + right.size(); i++) {
      joined.add(new ArrayList<>());
    }

    for (int i = 0; i < left.get(0).size(); i++) {
      for (int j = 0; j < left.size(); j++) {
        joined.get(j).add(left.get(j).get(i));
      }
      for (int j = left.size(); j < left.size() + right.size(); j++) {
        joined.get(j).add(right.get(j - left.size()).get(i));
      }
    }

    return joined;
  }

  // Merges two boards vertically
  ArrayList<ArrayList<GamePiece>> mergeVert(ArrayList<ArrayList<GamePiece>> top,
                                             ArrayList<ArrayList<GamePiece>> bottom) {
    ArrayList<ArrayList<GamePiece>> joined = new ArrayList<>();
    for (int i = 0; i < Math.min(top.size(), bottom.size()); i++) {
      ArrayList<GamePiece> temp = new ArrayList<>();
      temp.addAll(top.get(i));
      temp.addAll(bottom.get(i));
      joined.add(temp);
    }

    return joined;
  }

  // Retrieves the GamePiece at the given coordinates on the game board
  GamePiece gamePieceAt(int x, int y) {
    return this.board.get(x).get(y);
  }

  // Checks if the given Posn is within the bounds of the game window
  boolean validDrawnCoords(Posn p) {
    return p.x >= 0 && p.x <= this.width * GamePiece.SIZE && p.y >= 0
        && p.y <= this.height * GamePiece.SIZE;
  }

  // Checks if the given coordinates exist in this LightEmAll game
  boolean validCoords(int x, int y) {
    return x >= 0 && x < this.width && y >= 0 && y < this.height;
  }

  // Retrieves the tile drawn at the given Posn (pixel coordinates, not grid
  // coordinates)
  GamePiece gamePieceAtDrawnPosn(Posn p) {
    int x = p.x / GamePiece.SIZE;
    int y = p.y / GamePiece.SIZE;

    return this.gamePieceAt(x, y);
  }

  // Handles user mouse input
  public void onMouseClicked(Posn pos) {
    if (this.validDrawnCoords(pos)) {
      this.gamePieceAtDrawnPosn(pos).rotate();
    }
  }

  // Draws the current GameBoard
  public WorldScene makeScene() {
    WorldScene base = new WorldScene(GamePiece.SIZE * this.width, GamePiece.SIZE * this.height);
    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        int posX = (GamePiece.SIZE / 2) + i * GamePiece.SIZE;
        int posY = (GamePiece.SIZE / 2) + j * GamePiece.SIZE;
        WorldImage gamePiece = this.gamePieceAt(i, j).drawPiece();
        base.placeImageXY(gamePiece, posX, posY);
      }
    }
    return base;
  }

  // Moves the power station if the piece is connected in that direction
  public void onKeyEvent(String key) {

    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        GamePiece current = this.board.get(i).get(j);
        if (current.powerStation) {
          if (key.equals("right") && (i++ < this.width) && current.right) {
            this.board.get(i).get(j).powerStation = true;
            current.powerStation = false;
          }
          if (key.equals("left") && (i - 1 >= 0) && current.left) {
            this.board.get(i - 1).get(j).powerStation = true;
            current.powerStation = false;
          }
          if (key.equals("up") && (j - 1 >= 0) && current.top) {
            this.board.get(i).get(j - 1).powerStation = true;
            current.powerStation = false;
          }
          if (key.equals("down") && (j++ < this.height) && current.bottom) {
            this.board.get(i).get(j).powerStation = true;
            current.powerStation = false;
          }
        }
      }
    }
  }

}

class ExamplesLightEmAll {
  GamePiece g1;
  GamePiece g2;
  GamePiece g3;
  GamePiece g4;
  GamePiece g5;
  GamePiece g6;
  GamePiece g7;
  GamePiece g8;
  GamePiece g9;
  GamePiece g10;
  GamePiece g11;
  GamePiece g12;
  GamePiece g13;
  GamePiece g14;
  GamePiece g15;
  GamePiece g16;
  GamePiece g17;
  GamePiece g18;
  GamePiece g19;
  GamePiece g20;

  void init() {

  }

  void testBigBang(Tester t) {
    init();
//    this.lea.generateFractalBoard();
//    this.lea.bigBang(250, 250);
    LightEmAll lea = new LightEmAll(6, 7);
    lea.bigBang(400, 400);
  }
}