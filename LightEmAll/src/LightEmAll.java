import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
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

  static final ArrayList<ArrayList<Integer>> VECTORS = new ArrayList<>(Arrays.asList(
          new ArrayList<>(Arrays.asList(-1, 0)),
          new ArrayList<>(Arrays.asList(0, -1))));

  LightEmAll(int width, int height) {
    this.width = width;
    this.height = height;

    this.manualBoardInit();
    this.nodes = Utils.flatten(this.board);

    this.mst = new ArrayList<>();
    this.genEdges();
  }

  // EFFECT: manually populates the game grid with GamePieces
  void manualBoardInit() {
    GamePiece topVert = new GamePiece(0, 0, false, false, false, true, false);
    GamePiece midVert = new GamePiece(1, 0, false, false, true, true, false);
    GamePiece bottomVert = new GamePiece(0, this.height - 1, false, false, true, false, false);
    GamePiece horizontal = new GamePiece(1, this.height / 2, true, true, true, true, false);
    GamePiece leftEnd = new GamePiece(0, this.height / 2, false, true, true, true, false);
    GamePiece rightEnd = new GamePiece(this.width, this.height / 2, true, false, true, true, false);

    this.board = new ArrayList<>(this.height);

    // Iterate over each column in the game
    for (int i = 0; i < this.width; i++) {

      ArrayList<GamePiece> column = new ArrayList<>(this.width);
      // Add the top of the column
      column.add(topVert.clone(i, 0));

      // Add the middle part of each column
      for (int j = 1; j < this.height - 1; j++) {
        if (j == this.height / 2) {
          if (i == 0) {
            column.add(leftEnd.clone(i, j));
          } else if (i == this.width - 1) {
            column.add(rightEnd.clone(i, j));
          } else {
            column.add(horizontal.clone(i, j));
          }
        } else {
          column.add(midVert.clone(i, j));
        }
      }

      // Add the bottom of the column
      column.add(bottomVert.clone(i, this.height - 1));

      this.board.add(column);
    }

    int middleRow = this.height / 2;
    int middleCol = this.width / 2;
    GamePiece center = this.board.get(middleCol).get(middleRow);
    this.board.get(middleCol).set(middleRow, center.clone(middleRow, middleCol, true));
  }

  // EFFECT: generates the edges based on the GameBoard
  void genEdges() {
    for (GamePiece gp : this.nodes) {
      ArrayList<Integer> coords = gp.position();
      for (ArrayList<Integer> al : LightEmAll.VECTORS) {
        int nx = coords.get(0) + al.get(0);
        int ny = coords.get(1) + al.get(1);
        if (this.validCoords(nx, ny) && gp.connected(this.gamePieceAt(nx, ny))) {
          this.mst.add(new Edge(gp, this.gamePieceAt(nx, ny), 1));
          this.mst.add(new Edge(this.gamePieceAt(nx, ny), gp, 1));
        }
      }
    }
  }

  // Retrieves the GamePiece at the given coordinates on the game board
  GamePiece gamePieceAt(int x, int y) {
    return this.board.get(x).get(y);
  }

  // Checks if the given coordinates exist in this LightEmAll game
  boolean validCoords(int x, int y) {
    return x >= 0 && x < this.width && y >= 0 && y < this.height;
  }

  // EFFECT: updates the GamePiece with the position of the given GamePiece to be the given
  // GamePiece
  void modifyBoardAndNodes(GamePiece toAdd) {
    this.nodes.set(toAdd.row + (toAdd.col * this.height), toAdd);
    this.board.get(toAdd.col).set(toAdd.row, toAdd);
  }

  public WorldScene makeScene() {
    WorldScene base = new WorldScene(GamePiece.SIZE * this.width, GamePiece.SIZE * this.height);
    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        int posX = (GamePiece.SIZE / 2) + i * GamePiece.SIZE;
        int posY = (GamePiece.SIZE / 2) + j * GamePiece.SIZE;
        WorldImage gamePiece = this.board.get(i).get(j).drawPiece();
        base.placeImageXY(gamePiece, posX, posY);
      }
    }
    return base;
  }
}

class ExamplesLightEmAll {
  LightEmAll lea;
  ArrayList<GamePiece> col0;
  ArrayList<GamePiece> col1;
  ArrayList<GamePiece> col2;
  ArrayList<GamePiece> col3;
  ArrayList<GamePiece> col4;
  ArrayList<Edge> edges;
  int width = 5;
  int height = 5;
  ArrayList<ArrayList<GamePiece>> b1;
  
  LightEmAll small;
  
  GamePiece topVert;
  GamePiece midVert;
  GamePiece bottomVert;
  GamePiece horizontal;
  GamePiece leftEnd;
  GamePiece rightEnd;

  void init() {
    this.topVert = new GamePiece(0, 0, false, false, false, true, false);
    this.midVert = new GamePiece(1, 0, false, false, true, true, false);
    this.bottomVert = new GamePiece(0, this.height - 1, false, false, true, false, false);
    this.horizontal = new GamePiece(1, this.height / 2, true, true, true, true, false);
    this.leftEnd = new GamePiece(0, this.height / 2, false, true, true, true, false);
    this.rightEnd = new GamePiece(this.width - 1, this.height / 2, true, false, true, true, false);

    this.lea = new LightEmAll(this.width, this.height);

    this.col0 = new ArrayList<>(Arrays.asList(topVert.clone(0, 0), midVert.clone(0, 1),
            leftEnd, midVert.clone(0, 3), bottomVert.clone(0, 4)));
    this.col1 = new ArrayList<>(Arrays.asList(topVert.clone(1, 0), midVert.clone(1, 1),
            horizontal.clone(1, 2), midVert.clone(1, 3), bottomVert.clone(1, 4)));
    this.col2 = new ArrayList<>(Arrays.asList(topVert.clone(2, 0), midVert.clone(2, 1),
            horizontal.clone(2, 2, true), midVert.clone(2, 3), bottomVert.clone(2, 4)));
    this.col3 = new ArrayList<>(Arrays.asList(topVert.clone(3, 0), midVert.clone(3, 1),
            horizontal.clone(3, 2), midVert.clone(3, 3), bottomVert.clone(3, 4)));
    this.col4 = new ArrayList<>(Arrays.asList(topVert.clone(4, 0), midVert.clone(4, 1), rightEnd,
            midVert.clone(4, 3), bottomVert.clone(4, 4)));


    this.edges = new ArrayList<>(Arrays.asList(
            new Edge(this.col0.get(1), this.col0.get(0), 1),
            new Edge(this.col0.get(0), this.col0.get(1), 1),
            new Edge(this.col0.get(2), this.col0.get(1), 1),
            new Edge(this.col0.get(1), this.col0.get(2), 1),
            new Edge(this.col0.get(3), this.col0.get(2), 1),
            new Edge(this.col0.get(2), this.col0.get(3), 1),
            new Edge(this.col0.get(4), this.col0.get(3), 1),
            new Edge(this.col0.get(3), this.col0.get(4), 1),

            new Edge(this.col1.get(1), this.col1.get(0), 1),
            new Edge(this.col1.get(0), this.col1.get(1), 1),
            new Edge(this.col1.get(2), this.col1.get(1), 1),
            new Edge(this.col1.get(1), this.col1.get(2), 1),
            new Edge(this.col1.get(2), this.col0.get(2), 1),
            new Edge(this.col0.get(2), this.col1.get(2), 1),
            new Edge(this.col1.get(3), this.col1.get(2), 1),
            new Edge(this.col1.get(2), this.col1.get(3), 1),
            new Edge(this.col1.get(4), this.col1.get(3), 1),
            new Edge(this.col1.get(3), this.col1.get(4), 1),

            new Edge(this.col2.get(1), this.col2.get(0), 1),
            new Edge(this.col2.get(0), this.col2.get(1), 1),
            new Edge(this.col2.get(2), this.col2.get(1), 1),
            new Edge(this.col2.get(1), this.col2.get(2), 1),
            new Edge(this.col2.get(3), this.col2.get(2), 1),
            new Edge(this.col2.get(2), this.col2.get(3), 1),
            new Edge(this.col2.get(2), this.col1.get(2), 1),
            new Edge(this.col1.get(2), this.col2.get(2), 1),
            new Edge(this.col2.get(4), this.col2.get(3), 1),
            new Edge(this.col2.get(3), this.col2.get(4), 1),

            new Edge(this.col3.get(1), this.col3.get(0), 1),
            new Edge(this.col3.get(0), this.col3.get(1), 1),
            new Edge(this.col3.get(2), this.col3.get(1), 1),
            new Edge(this.col3.get(1), this.col3.get(2), 1),
            new Edge(this.col3.get(3), this.col3.get(2), 1),
            new Edge(this.col3.get(2), this.col3.get(3), 1),
            new Edge(this.col3.get(2), this.col2.get(2), 1),
            new Edge(this.col2.get(2), this.col3.get(2), 1),
            new Edge(this.col3.get(4), this.col3.get(3), 1),
            new Edge(this.col3.get(3), this.col3.get(4), 1),

            new Edge(this.col4.get(1), this.col4.get(0), 1),
            new Edge(this.col4.get(0), this.col4.get(1), 1),
            new Edge(this.col4.get(2), this.col4.get(1), 1),
            new Edge(this.col4.get(1), this.col4.get(2), 1),
            new Edge(this.col4.get(3), this.col4.get(2), 1),
            new Edge(this.col4.get(2), this.col4.get(3), 1),
            new Edge(this.col4.get(2), this.col3.get(2), 1),
            new Edge(this.col3.get(2), this.col4.get(2), 1),
            new Edge(this.col4.get(4), this.col4.get(3), 1),
            new Edge(this.col4.get(3), this.col4.get(4), 1)
    ));

    this.b1 = new ArrayList<>(Arrays.asList(this.col0, this.col1, this.col2, this.col3, this.col4));
    
    this.small = new LightEmAll(2, 3);
  }

  void testManualBoardInit(Tester t) {
    init();
    t.checkExpect(this.lea.board, this.b1);
  }

//  void testGenEdges(Tester t) {
//    init();
//    t.checkExpect(this.lea.mst, this.edges);
//  }
  
  void testMakeScene(Tester t) {
    init();
    
    WorldImage base = new FrameImage(
        new RectangleImage(GamePiece.SIZE, GamePiece.SIZE, OutlineMode.SOLID, Color.BLUE),
        Color.BLACK);
    WorldImage wireVert = 
        new RectangleImage(GamePiece.SIZE / 5, GamePiece.SIZE / 2, OutlineMode.SOLID, Color.GRAY);
    WorldImage wireHoz = 
        new RectangleImage(GamePiece.SIZE / 2, GamePiece.SIZE / 5, OutlineMode.SOLID, Color.GRAY);
    WorldImage ps = new CircleImage(GamePiece.SIZE / 5, OutlineMode.SOLID, Color.YELLOW);
    
    WorldImage g00img = 
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0 , 0, base);
    WorldImage g01img = 
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0 , 0,
            new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0 , 0,
                new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, wireHoz, 0 , 0, base)));
    WorldImage g02img = 
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0 , 0, base);
    WorldImage g10img = 
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0 , 0, base);
    WorldImage g11img = new OverlayImage(ps,
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, wireVert, 0 , 0,
            new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0 , 0,
                new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, wireHoz, 0 , 0, base))));
    WorldImage g12img = 
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, wireVert, 0 , 0, base);
    
    WorldScene smallBoard = new WorldScene(100, 150);
    
    smallBoard.placeImageXY(g00img, 25, 25);
    smallBoard.placeImageXY(g01img, 25, 75);
    smallBoard.placeImageXY(g02img, 25, 125);
    smallBoard.placeImageXY(g10img, 75, 25);
    smallBoard.placeImageXY(g11img, 75, 75);
    smallBoard.placeImageXY(g12img, 75, 125);
    
    t.checkExpect(this.small.makeScene(), smallBoard);
  }
  
  void testBigBang(Tester t) {
    init();
    this.lea.bigBang(250, 250);
  }
}