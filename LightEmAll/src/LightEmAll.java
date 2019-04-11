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
    ArrayList<ArrayList<GamePiece>> temp = generateFractalBoardHelp(0, 0, this.width - 1,
            this.height - 1);
    temp.get(temp.size() / 2).get(0).powerStation = true;
    this.board = temp;
    this.powerCol = temp.size() / 2;
    this.powerRow = 0;
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
    GamePiece powerStation = this.gamePieceAt(this.powerCol, this.powerRow);
    if (key.equals("right") && this.powerCol + 1 < this.width && powerStation.right) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerCol++;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    } else if (key.equals("left") && this.powerCol - 1 >= 0 && powerStation.left) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerCol--;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    } else if (key.equals("up") && this.powerRow - 1 >= 0 && powerStation.top) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerRow--;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    } else if (key.equals("down") && this.powerRow + 1 < this.height && powerStation.bottom) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerRow++;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
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

  ArrayList<ArrayList<GamePiece>> b1;
  LightEmAll lea;

  void init() {
    this.g1 = new GamePiece(0, 0, false, false, false, true, false);
    this.g2 = new GamePiece(0, 1, false, false, false, true, false);
    this.g3 = new GamePiece(0, 2, false, false, false, true, true);
    this.g4 = new GamePiece(0, 3, false, false, false, true, false);
    this.g5 = new GamePiece(1, 0, false, false, true, true, false);
    this.g6 = new GamePiece(1, 1, false, false, true, true, false);
    this.g7 = new GamePiece(1, 2, false, false, true, true, false);
    this.g8 = new GamePiece(1, 3, false, false, true, true, false);
    this.g9 = new GamePiece(2, 0, false, true, true, true, false);
    this.g10 = new GamePiece(2, 1, true, false, true, false, false);
    this.g11 = new GamePiece(2, 2, false, true, true, false, false);
    this.g12 = new GamePiece(2, 3, true, false, true, true, false);
    this.g13 = new GamePiece(3, 0, false, false, true, true, false);
    this.g14 = new GamePiece(3, 1, false, false, false, true, false);
    this.g15 = new GamePiece(3, 2, false, false, false, true, false);
    this.g16 = new GamePiece(3, 3, false, false, true, true, false);
    this.g17 = new GamePiece(4, 0, false, true, true, false, false);
    this.g18 = new GamePiece(4, 1, true, true, true, false, false);
    this.g19 = new GamePiece(4, 2, true, true, true, false, false);
    this.g20 = new GamePiece(4, 3, true, false, true, false, false);

    this.b1 = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(this.g1, this.g5, this.g9, this.g13, this.g17)),
            new ArrayList<>(Arrays.asList(this.g2, this.g6, this.g10, this.g14, this.g18)),
            new ArrayList<>(Arrays.asList(this.g3, this.g7, this.g11, this.g15, this.g19)),
            new ArrayList<>(Arrays.asList(this.g4, this.g8, this.g12, this.g16, this.g20))));

    this.lea = new LightEmAll(4, 5);
  }

  void testOnKeyEvent(Tester t) {
    init();
    t.checkExpect(this.lea.gamePieceAt(2, 0).powerStation, true);
    this.lea.onKeyEvent("down");
    t.checkExpect(this.lea.gamePieceAt(2, 1).powerStation, true);
    this.lea.onKeyEvent("down");
    t.checkExpect(this.lea.gamePieceAt(2, 2).powerStation, true);
    this.lea.onKeyEvent("right");
    t.checkExpect(this.lea.gamePieceAt(3, 2).powerStation, true);
    this.lea.onKeyEvent("left");
    t.checkExpect(this.lea.gamePieceAt(2, 2).powerStation, true);
    this.lea.onKeyEvent("left");
    t.checkExpect(this.lea.gamePieceAt(2, 1).powerStation, false);
    this.lea.onKeyEvent("a");
    t.checkExpect(this.lea.gamePieceAt(2, 2).powerStation, true);
  }

  void testValidCoords(Tester t) {
    init();
    t.checkExpect(this.lea.validCoords(2, 1), true);
    t.checkExpect(this.lea.validCoords(-1, 0), false);
    t.checkExpect(this.lea.validCoords(2, 4), true);
    t.checkExpect(this.lea.validCoords(5, 2), false);
  }

  void testValidDrawnCoords(Tester t) {
    init();
    t.checkExpect(this.lea.validDrawnCoords(new Posn(20, 53)), true);
    t.checkExpect(this.lea.validDrawnCoords(new Posn(0, 250)), true);
    t.checkExpect(this.lea.validDrawnCoords(new Posn(-5, 120)), false);
    t.checkExpect(this.lea.validDrawnCoords(new Posn(380, 70)), false);
  }

  void testGamePieceAt(Tester t) {
    init();
    t.checkExpect(this.lea.gamePieceAt(0, 0), this.g1);
    t.checkExpect(this.lea.gamePieceAt(2, 3), this.g15);
  }

  void testGamePieceAtDrawnPosn(Tester t) {
    init();
    t.checkExpect(this.lea.gamePieceAtDrawnPosn(new Posn(63, 18)), this.g2);
    t.checkExpect(this.lea.gamePieceAtDrawnPosn(new Posn(140, 223)), this.g19);
  }

  void testBigBang(Tester t) {
    init();
//    this.lea.generateFractalBoard();
//    this.lea.bigBang(250, 250);
    LightEmAll lea = new LightEmAll(4, 5);
    lea.bigBang(400, 400);
  }
}