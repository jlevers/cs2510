import java.awt.*;
import java.util.*;

import tester.*;
import javalib.impworld.*;

import javalib.worldimages.*;

class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
    // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;
  Random rand;

  static ArrayList<Posn> VECTORS = new ArrayList<>(Arrays.asList(
          new Posn(-1, 0), new Posn(0, -1), new Posn(0, 1), new Posn(1, 0)));
  static ArrayList<String> DIRS = new ArrayList<>(Arrays.asList("left", "up", "down", "right"));
  static ArrayList<String> OPPODIRS = new ArrayList<>(Arrays.asList("right", "down", "up", "left"));

  LightEmAll(int width, int height) {
    this(width, height, new Random());
  }

  LightEmAll(int width, int height, Random rand) {
    this.width = width;
    this.height = height;
    this.rand = rand;

    this.blankBoard();
    this.powerCol = 0;
    this.powerRow = 0;
    this.nodes = Utils.flatten(this.board);

    this.findMST();
    this.radius = this.getDiameter();
    this.randomize();
  }

  // EFFECT: initializes the board to to have no connections
  void blankBoard() {
    this.board = new ArrayList<>();

    for (int i = 0; i < this.width; i++) {
      ArrayList<GamePiece> col = new ArrayList<>();

      for (int j = 0; j < this.height; j++) {
        col.add(new GamePiece(j, i, false, false, false, false, false));
      }

      this.board.add(col);
    }
    this.gamePieceAt(0, 0).powerStation = true;
  }

  // EFFECT: finds the minimum spanning tree for a board of this size
  void findMST() {
    HashMap<GamePiece, GamePiece> reps = new HashMap<>();
    for (GamePiece gp : this.nodes) {
      reps.put(gp, gp);
    }
    ArrayList<Edge> treeEdges = new ArrayList<>();
    ArrayList<Edge> worklist = this.genEdges();

    while(treeEdges.size() < this.nodes.size() - 1) {
      Edge current = worklist.remove(0);
      GamePiece from = current.fromNode;
      GamePiece to = current.toNode;

      if (!this.topRep(reps.get(to), reps).sameGamePiece(this.topRep(reps.get(from), reps))) {
        treeEdges.add(current);
        this.union(to, from , reps);
        to.connectTo(from);
      }
    }
  }
  
  // EFFECT: Sets the top representatives for each node
  void union(GamePiece to, GamePiece from, HashMap<GamePiece,GamePiece> reps) {
    if (to.sameGamePiece(reps.get(to))) {
      reps.put(to, from);
    } else {
      this.union(reps.get(to), from, reps);
    }
  }

  // Finds the top level representative for the given GamePiece in the MST
  GamePiece topRep(GamePiece gp, HashMap<GamePiece, GamePiece> reps) {
    if (gp.sameGamePiece(reps.get(gp))) {
      return gp;
    }

    return this.topRep(reps.get(gp), reps);
  }

  // Finds all the possible edges on this board
  ArrayList<Edge> genEdges() {
    ArrayList<Edge> edges = new ArrayList<>();

    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        for (int k = 0; k < VECTORS.size(); k++) {
          Posn p = VECTORS.get(k);
          int nx = p.x + i;
          int ny = p.y + j;
          if (this.validCoords(nx, ny)) {
            edges.add(new Edge(this.board.get(i).get(j), this.board.get(nx).get(ny),
                  this.rand.nextInt(40)));
          }
        }
      }
    }

    Collections.sort(edges, new CompEdgeWeight());
    return edges;
  }

  // EFFECT: randomly rotates all GamePieces on the board
  void randomize() {
    for (GamePiece gp : this.nodes) {
      for (int i = 0; i < this.rand.nextInt(4); i++) {
        gp.rotate();
      }
    }
  }

  // Checks if every GamePiece has been lit, and if so, ends the game
  public WorldEnd worldEnds() {
    WorldImage endText = new TextImage("Success!", 16, Color.RED);WorldScene endScene =
            this.makeScene();
    boolean allLit = false;
    for (GamePiece gp : this.nodes) {
      allLit = gp.lit;
      if (!allLit) break;
    }

    if (allLit) {
      endScene.placeImageXY(endText, this.width * GamePiece.SIZE / 2,
              this.height * GamePiece.SIZE / 2);
      return new WorldEnd(true, endScene);
    } else {
      return new WorldEnd(false, endScene);
    }
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
    GamePiece powerStation = this.gamePieceAt(powerCol, powerRow);
    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        int posX = (GamePiece.SIZE / 2) + i * GamePiece.SIZE;
        int posY = (GamePiece.SIZE / 2) + j * GamePiece.SIZE;
        GamePiece gp = this.gamePieceAt(i, j);
        int distance = this.depthBetween(powerStation, gp, new ArrayList<>());
        Color color;
        if (distance > -1 && distance <= radius) {
          color = GamePiece.LIT_COLOR;
          gp.lit = true;
        } else {
          color = GamePiece.WIRE_COLOR;
          gp.lit = false;
        }
        WorldImage gamePiece = gp.drawPiece(color);
        base.placeImageXY(gamePiece, posX, posY);
      }
    }
    return base;
  }

  // Moves the power station if the piece is connected in that direction
  public void onKeyEvent(String key) {
    GamePiece powerStation = this.gamePieceAt(this.powerCol, this.powerRow);
    if (key.equals("right") && this.powerCol + 1 < this.width && powerStation.right
            && this.gamePieceAt(this.powerCol + 1, this.powerRow).left) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerCol++;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    } else if (key.equals("left") && this.powerCol - 1 >= 0 && powerStation.left
            && this.gamePieceAt(this.powerCol - 1, this.powerRow).right) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerCol--;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    } else if (key.equals("up") && this.powerRow - 1 >= 0 && powerStation.top
            && this.gamePieceAt(this.powerCol, this.powerRow - 1).bottom) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerRow--;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    } else if (key.equals("down") && this.powerRow + 1 < this.height && powerStation.bottom
            && this.gamePieceAt(this.powerCol, this.powerRow + 1).top) {
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = false;
      this.powerRow++;
      this.gamePieceAt(this.powerCol, this.powerRow).powerStation = true;
    }
  }

  // Gets the diameter of the graph
  int getDiameter() {
    GamePiece first = this.bfs(this.gamePieceAt(0, 0));
    int diameter = this.depthBetween(first, this.bfs(first), new ArrayList<>());
    return diameter / 2 + 1;
  }

  // Performs a breadth-first search on this LightEmAll's nodes, returns depth of the
  // matching node
  int depthBetween(GamePiece start, GamePiece end, ArrayList<GamePiece> visited) {

    GamePiece current = start;
    ArrayList<GamePiece> neighbors = this.getConnectedNeighbors(current, visited);
    visited.add(current);

    if (start.sameGamePiece(end)) {
      return 0;
    } else if (neighbors.size() == 0) {
      return Integer.MIN_VALUE;
    }

    ArrayList<Integer> depths = new ArrayList<>();
    for (GamePiece gp : neighbors) {
      if (gp.sameGamePiece(end)) {
        return 1;
      }

      depths.add(1 + depthBetween(gp, end, visited));
    }

    return Utils.maxALInt(depths);
  }


  // Performs a breadth-first search on this LightEmAll's nodes, returns the deepest node
  GamePiece bfs(GamePiece start) {
    Queue<GamePiece> queue = new Queue<>(new ArrayList<>(Arrays.asList(start)));
    ArrayList<GamePiece> visited = new ArrayList<>();
    GamePiece current = start;

    while (queue.size() > 0) {
      current = queue.pop();
      visited.add(current);
      queue.pushAll(this.getConnectedNeighbors(current, visited));
    }
    return current;
  }

  // Gets an ArrayList of connected neighbors of the given GamePiece
  ArrayList<GamePiece> getConnectedNeighbors(GamePiece gp, ArrayList<GamePiece> visited) {
    ArrayList<GamePiece> neighbors = new ArrayList<>();

    for (int i = 0; i < LightEmAll.VECTORS.size(); i++) {
      Posn p = LightEmAll.VECTORS.get(i);
      int x = gp.col + p.x;
      int y = gp.row + p.y;

      if (this.validCoords(x, y) && gp.getDirFromKeypress(LightEmAll.DIRS.get(i))
              && this.gamePieceAt(x, y).getDirFromKeypress(LightEmAll.OPPODIRS.get(i))
              && !visited.contains(this.gamePieceAt(x, y))) {
        neighbors.add(this.gamePieceAt(x, y));
      }
    }
    return neighbors;
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
  GamePiece allFalse;

  ArrayList<ArrayList<GamePiece>> b1;
  ArrayList<ArrayList<GamePiece>> blank;
  HashMap<GamePiece, GamePiece> hm;
  LightEmAll lea;
  Random rand;

  void init() {
    this.g1 = new GamePiece(0, 0, false, false, false, true, true);
    this.g5 = new GamePiece(1, 0, false, true, true, false, false);
    this.g9 = new GamePiece(2, 0, false, true, false, true, false);
    this.g13 = new GamePiece(3, 0, true, true, false, true, false);
    this.g17 = new GamePiece(4, 0, false, true, false, false, false);

    this.g2 = new GamePiece(0, 1, false, false, true, false, false);
    this.g6 = new GamePiece(1, 1, true, true, true, true, false);
    this.g10 = new GamePiece(2, 1, true, false, true, false, false);
    this.g14 = new GamePiece(3, 1, true, false, false, false, false);
    this.g18 = new GamePiece(4, 1, true, false, false, false, false);

    this.g3 = new GamePiece(0, 2, true, false, false, true, false);
    this.g7 = new GamePiece(1, 2, true, true, true, false, false);
    this.g11 = new GamePiece(2, 2, false, false, true, true, false);
    this.g15 = new GamePiece(3, 2, false, false, true, true, false);
    this.g19 = new GamePiece(4, 2, true, true, false, true, false);

    this.g4 = new GamePiece(0, 3, true, false, true, false, false);
    this.g8 = new GamePiece(1, 3, false, false, true, true, false);
    this.g12 = new GamePiece(2, 3, false, false, false, true, false);
    this.g16 = new GamePiece(3, 3, false, false, true, false, false);
    this.g20 = new GamePiece(4, 3, false, true, true, false, false);

    this.allFalse = new GamePiece(0, 0, false, false, false, false, false);

    this.b1 = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(this.g1, this.g5, this.g9, this.g13, this.g17)),
            new ArrayList<>(Arrays.asList(this.g2, this.g6, this.g10, this.g14, this.g18)),
            new ArrayList<>(Arrays.asList(this.g3, this.g7, this.g11, this.g15, this.g19)),
            new ArrayList<>(Arrays.asList(this.g4, this.g8, this.g12, this.g16, this.g20))));

    this.blank = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(
                    this.allFalse, this.allFalse.clone(1, 0), this.allFalse.clone(2, 0))),
            new ArrayList<>(Arrays.asList(
                    this.allFalse.clone(0, 1), this.allFalse.clone(1, 1),
                    this.allFalse.clone(2, 1))),
            new ArrayList<>(Arrays.asList(
                    this.allFalse.clone(0, 2), this.allFalse.clone(1, 2),
                    this.allFalse.clone(2, 2)))));
    this.blank.get(0).get(0).powerStation = true;
    
    this.rand = new Random(1);

    this.lea = new LightEmAll(4, 5, this.rand);
    this.hm = new HashMap<>();
    for (GamePiece gp : this.lea.nodes) {
      this.hm.put(gp, gp);
    }
  }

  void testOnKeyEvent(Tester t) {
    init();
    t.checkExpect(this.lea.gamePieceAt(0, 0).powerStation, true);
    this.lea.onKeyEvent("down");
    t.checkExpect(this.lea.gamePieceAt(0, 1).powerStation, true);
    this.lea.onKeyEvent("right");
    t.checkExpect(this.lea.gamePieceAt(1, 1).powerStation, true);
    this.lea.onKeyEvent("left");
    t.checkExpect(this.lea.gamePieceAt(0, 1).powerStation, true);
    this.lea.onKeyEvent("up");
    t.checkExpect(this.lea.gamePieceAt(0, 0).powerStation, true);
    this.lea.onKeyEvent("right");
    t.checkExpect(this.lea.gamePieceAt(0, 0).powerStation, true);
    t.checkExpect(this.lea.gamePieceAt(1, 0).powerStation, false);
    this.lea.onKeyEvent("a");
    t.checkExpect(this.lea.gamePieceAt(0, 0).powerStation, true);
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
    t.checkExpect(this.lea.gamePieceAt(1, 0), this.g2);
    t.checkExpect(this.lea.gamePieceAt(2, 0), this.g3);
    t.checkExpect(this.lea.gamePieceAt(3, 0), this.g4);
    t.checkExpect(this.lea.gamePieceAt(0, 1), this.g5);
    t.checkExpect(this.lea.gamePieceAt(1, 1), this.g6);
    t.checkExpect(this.lea.gamePieceAt(2, 1), this.g7);
    t.checkExpect(this.lea.gamePieceAt(3, 1), this.g8);
    t.checkExpect(this.lea.gamePieceAt(0, 2), this.g9);
    t.checkExpect(this.lea.gamePieceAt(1, 2), this.g10);
    t.checkExpect(this.lea.gamePieceAt(2, 2), this.g11);
    t.checkExpect(this.lea.gamePieceAt(3, 2), this.g12);
    t.checkExpect(this.lea.gamePieceAt(0, 3), this.g13);
    t.checkExpect(this.lea.gamePieceAt(1, 3), this.g14);
    t.checkExpect(this.lea.gamePieceAt(2, 3), this.g15);
    t.checkExpect(this.lea.gamePieceAt(3, 3), this.g16);
    t.checkExpect(this.lea.gamePieceAt(0, 4), this.g17);
    t.checkExpect(this.lea.gamePieceAt(1, 4), this.g18);
    t.checkExpect(this.lea.gamePieceAt(2, 4), this.g19);
    t.checkExpect(this.lea.gamePieceAt(3, 4), this.g20);
  }

  void testGamePieceAtDrawnPosn(Tester t) {
    init();
    t.checkExpect(this.lea.gamePieceAtDrawnPosn(new Posn(63, 18)), this.g2);
    t.checkExpect(this.lea.gamePieceAtDrawnPosn(new Posn(135, 245)), this.g19);
  }
  
  //Tests the radius directly, as getDiameter() is called while all pieces are connected
  void testGetDiameter(Tester t) {
    init();
    t.checkExpect(this.lea.radius, 6);
  }

  void testGenEdges(Tester t) {
    LightEmAll forGen = new LightEmAll(2, 2, new Random(1));
    forGen.blankBoard();
    GamePiece g1 = forGen.gamePieceAt(0, 0);
    GamePiece g2 = forGen.gamePieceAt(0, 1);
    GamePiece g3 = forGen.gamePieceAt(1, 0);
    GamePiece g4 = forGen.gamePieceAt(1, 1);
    ArrayList<Edge> edges = new ArrayList<>(Arrays.asList(
            new Edge(g2, g4, 12), new Edge(g2, g1, 16), new Edge(g3, g4, 19), new Edge(g1, g3, 29),
            new Edge(g3, g1, 30), new Edge(g4, g2, 34), new Edge(g1, g2, 36), new Edge(g4, g3,
                    39)));
    t.checkExpect(forGen.genEdges(), edges);
  }

  void testTopRep(Tester t) {
    init();
    this.hm.put(this.lea.gamePieceAt(0, 0), this.lea.gamePieceAt(0, 1));
    this.hm.put(this.lea.gamePieceAt(3, 2), this.lea.gamePieceAt(0, 0));
    t.checkExpect(this.lea.topRep(this.lea.gamePieceAt(0, 0), this.hm), this.lea.gamePieceAt(0, 1));
    t.checkExpect(this.lea.topRep(this.lea.gamePieceAt(3, 2), this.hm), this.lea.gamePieceAt(0, 1));
  }

  void testBlankBoard(Tester t) {
    init();
    LightEmAll smaller = new LightEmAll(3, 3);
    smaller.blankBoard();
    t.checkExpect(smaller.board, this.blank);
  }

  void testBFS(Tester t) {
    init();
    this.lea.blankBoard();
    this.lea.powerCol = 0;
    this.lea.powerRow = 0;
    this.lea.nodes = Utils.flatten(this.lea.board);
    this.lea.findMST();
    t.checkExpect(this.lea.bfs(this.lea.gamePieceAt(1, 4)), this.lea.gamePieceAt(2, 0));
  }

  void testFindMST(Tester t) {
    init();
    this.lea.rand = new Random(1);
    this.lea.blankBoard();
    this.lea.powerCol = 0;
    this.lea.powerRow = 0;
    this.lea.nodes = Utils.flatten(this.lea.board);
    this.lea.findMST();
    this.lea.randomize();
    t.checkExpect(this.lea.board, this.b1);
  }

  void testDepthBetween(Tester t) {
    init();
    t.checkExpect(this.lea.depthBetween(this.g1, this.g3, new ArrayList<>()), 4);
    t.checkExpect(this.lea.depthBetween(this.g1, this.g7, new ArrayList<>()), 3);
  }

  void testBigBang(Tester t) {
    init();
    this.lea.bigBang(200, 250, (1.0 / 28.0));
  }
}