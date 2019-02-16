import tester.*;
import javalib.worldimages.*;
import javalib.worldcanvas.*;
import javalib.funworld.*;
import java.awt.Color;

interface ITree {
  // Draws this tree
  WorldImage draw();

  // Checks if any of the stems or leaves are drooping
  boolean isDrooping();

  // Combines this tree with another tree, rotated to the given angles and at the given lengths
  ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
                     ITree otherTree);

  // Adjusts the angles of non-root trees from combine()
  ITree combineHelper(double theta);

  // Gets the width of the tree
  double getWidth();

  // Gets the width of the left side of the tree
  double getWidthHelperLeft();

  // Gets the width of the right side of the tree
  double getWidthHelperRight();
}

class Leaf implements ITree {
  int size; // represents the radius of the leaf
  Color color; // the color to draw it

  Leaf(int size, Color color) {
    this.size = size;
    this.color = color;
  }

  /*
   * Template:
   * Fields:
   * this.size ... int
   * this.color ... Color
   *
   * Methods:
   * this.draw() ... WorldImage
   * this.isDrooping() ... boolean
   * this.combine(int, int, double, double, ITree) ... ITree
   * this.combineHelper(double) ... ITree
   * this.getWidth() ... double
   * this.getWidthHelper() ... double
   *
   * Methods of fields:
   */

  // Draws this Leaf
  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, this.color);
  }

  // Leaves are never drooping
  public boolean isDrooping() {
    return false;
  }

  // Combines this Leaf with the rest of the tree
  public ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
                            ITree otherTree) {

    ITree stemLeft = new Stem(leftLength, leftTheta, this.combineHelper(-1 * (90 - leftTheta)));
    ITree stemRight = new Stem(rightLength, rightTheta,
            otherTree.combineHelper(-1 * (90 - rightTheta)));
    return new Branch(leftLength, rightLength, leftTheta, rightTheta, stemLeft, stemRight);
  }

  // Rotates this the specified amount -- since Leaves are circles, it's the same post-rotation
  public ITree combineHelper(double theta) {
    return this;
  }

  // Gets the width of this leaf (the diameter)
  public double getWidth() {
    return this.size * 2;
  }

  // Gets the width of this leaf, given that it's on the left
  public double getWidthHelperLeft() {
    return -1 * this.size;
  }

  // Gets the width of this leaf, given that it's on the right
  public double getWidthHelperRight() {
    return this.size;
  }
}

class Stem implements ITree {
  // How long this stick is
  int length;
  // The angle (in degrees) of this stem, relative to the +x axis
  double theta;
  // The rest of the tree
  ITree tree;

  Stem(int length, double theta, ITree tree) {
    this.length = length;
    this.theta = theta % 360;
    this.tree = tree;
  }

  /*
   * Template:
   * Fields:
   * this.length ... int
   * this.theta ... double
   * this.tree ... ITree
   *
   * Methods:
   * this.draw() ... WorldImage
   * this.isDrooping() ... boolean
   * this.combine(int, int, double, double, ITree) ... ITree
   * this.combineHelper(double) ... ITree
   * this.getWidth() ... double
   * this.getWidthHelper() ... double
   *
   * Methods of fields:
   * this.tree.draw() ... WorldImage
   * this.tree.isDrooping() ... boolean
   * this.tree.combine(int, int, double, double, ITree) ... ITree
   * this.tree.combineHelper(double) ... ITree
   * this.tree.getWidth() ... double
   * this.tree.getWidthHelper() ... double
   */

  // Draws this Stem
  public WorldImage draw() {
    Posn endpoint = new Posn(length, 0);
    Posn origin = new Posn(-1 * (length / 2), 0);

    double inRadians = Math.toRadians(180 - theta);
    int newX = (int) (length * Math.cos(inRadians));
    int newY = (int) (length * Math.sin(inRadians));

    WorldImage origLine = new LineImage(endpoint, Color.BLACK).movePinholeTo(origin);
    WorldImage rotateLine = new RotateImage(origLine, 180 - this.theta);
    WorldImage overlay = new OverlayImage(this.tree.draw(), rotateLine);
    return overlay.movePinhole(newX, newY);
  }

  // Checks if this Stem is drooping
  public boolean isDrooping() {
    return 180 - this.theta < 0 || this.tree.isDrooping();
  }

  // Combines this Stem with otherTree, with their subtrees aligned to the same angle
  public ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
                       ITree otherTree) {
    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
            this.tree.combineHelper(-1 * (90 - leftTheta)),
            otherTree.combineHelper(-1 * (90 - rightTheta)));
  }

  // Adjusts up-tree angles to be consistent with the lower "combined" Stem
  public ITree combineHelper(double theta) {
    return new Stem(this.length, this.theta + theta, this.tree.combineHelper(theta));
  }

  // Gets the width of this Stem
  public double getWidth() {
    double thisWidth = this.length * Math.cos(Math.toRadians(this.theta));
    return thisWidth + (this.tree.getWidthHelperRight() - this.tree.getWidthHelperLeft());
  }

  // Gets the width of this Stem, going left all the way up
  public double getWidthHelperLeft() {
    return (this.length * Math.cos(Math.toRadians(this.theta))) + this.tree.getWidthHelperLeft();
  }

  // Gets the width of this Stem, going right all the way up
  public double getWidthHelperRight() {
    return (this.length * Math.cos(Math.toRadians(this.theta))) + this.tree.getWidthHelperRight();
  }
}

class Branch implements ITree {
  // How long the left and right branches are
  int leftLength;
  int rightLength;
  // The angle (in degrees) of the two branches, relative to the +x axis,
  double leftTheta;
  double rightTheta;
  // The remaining parts of the tree
  ITree left;
  ITree right;

  Branch(int leftLength, int rightLength, double leftTheta, double rightTheta, ITree left,
         ITree right) {
    this.leftLength = leftLength;
    this.rightLength = rightLength;
    this.leftTheta = leftTheta % 360;
    this.rightTheta = rightTheta % 360;
    this.left = left;
    this.right = right;
  }

  /*
   * Template:
   * Fields:
   * this.leftLength ... int
   * this.rightLength ... int
   * this.leftTheta ... double
   * this.rightTheta ... double
   * this.left ... ITree
   * this.right ... ITree
   *
   * Methods:
   * this.draw() ... WorldImage
   * this.isDrooping() ... boolean
   * this.combine(int, int, double, double, ITree) ... ITree
   * this.combineHelper(double) ... ITree
   * this.getWidth() ... double
   * this.getWidthHelper() ... double
   * this.getWidthHelperLeft() ... double
   * this.getWidthHelperRight() ... double
   *
   * Methods of fields:
   * this.left.draw() ... WorldImage
   * this.right.draw() ... WorldImage
   * this.left.isDrooping() ... boolean
   * this.right.isDrooping() ... boolean
   * this.left.combine(int, int, double, double, ITree) ... ITree
   * this.right.combine(int, int, double, double, ITree) ... ITree
   * this.left.combineHelper(double) ... ITree
   * this.right.combineHelper(double) ... ITree
   * this.left.getWidth() ... double
   * this.right.getWidth() ... double
   * this.left.getWidthHelperLeft() ... double
   * this.left.getWidthHelperRight() ... double
   * this.right.getWidthHelperLeft() ... double
   * this.right.getWidthHelperRight() ... double
   */

  // Draws this Branch
  public WorldImage draw() {
    ITree leftStem = new Stem(leftLength, leftTheta, this.left);
    ITree rightStem = new Stem(rightLength, rightTheta, this.right);

    WorldImage left = leftStem.draw();
    WorldImage right = rightStem.draw();

    // Overlay left and right parts of branch
    return new OverlayImage(left, right);
  }

  // Checks if this Branch is drooping, or any of its subtrees
  public boolean isDrooping() {
    return 180 - this.leftTheta < 0 || 180 - this.rightTheta < 0
            || this.left.isDrooping() || this.right.isDrooping();
  }

  // Combines this Branch with otherTree, with their subtrees aligned to the same angle
  public ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
                       ITree otherTree) {
    ITree thisBranch = new Branch(this.leftLength, this.rightLength, this.leftTheta,
            this.rightTheta, this.left.combineHelper(90 - leftTheta),
            this.right.combineHelper(90 - leftTheta));

    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
            thisBranch.combineHelper(-1 * (90 - leftTheta)),
            otherTree.combineHelper(-1 * (90 - rightTheta)));
  }

  // Adjusts up-tree angles to be consistent with the lower "combined" Branch
  public ITree combineHelper(double theta) {
    return new Branch(this.leftLength, this.rightLength, this.leftTheta + theta,
            this.rightTheta + theta, this.left.combineHelper(theta),
            this.right.combineHelper(theta));
  }

  // Gets the width of this Branch
  public double getWidth() {
    double thisLeftWidth, thisRightWidth;
    thisLeftWidth = this.leftLength * Math.cos(Math.toRadians(this.leftTheta));
    thisRightWidth = this.rightLength * Math.cos(Math.toRadians(this.rightTheta));

    double leftOfLeft, rightOfLeft, leftOfRight, rightOfRight;
    leftOfLeft = thisLeftWidth + this.left.getWidthHelperLeft();
    rightOfLeft = thisLeftWidth + this.left.getWidthHelperRight();
    leftOfRight = thisRightWidth + this.right.getWidthHelperLeft();
    rightOfRight = thisRightWidth + this.right.getWidthHelperRight();

    // If one side of the tree goes past the other side on their adjacent sides
    if (rightOfLeft > rightOfRight || leftOfRight < leftOfLeft) {
      double allLeft = rightOfLeft - leftOfLeft;
      double allRight = rightOfRight - leftOfRight;

      return Math.max(allLeft, allRight);
    }

    return rightOfRight - leftOfLeft;
  }

  // Gets the width of the left side of this Branch
  public double getWidthHelperLeft() {
    double thisWidth = this.leftLength * Math.cos(Math.toRadians(this.leftTheta));
    return thisWidth + this.left.getWidthHelperLeft();
  }

  // Gets the width of the right side of this Branch
  public double getWidthHelperRight() {
    double thisWidth = this.rightLength * Math.cos(Math.toRadians(this.rightTheta));
    return thisWidth + this.right.getWidthHelperRight();
  }
}

class ExamplesITrees {

  // Leaves
  ITree redLeaf = new Leaf(10, Color.RED);
  ITree blueLeaf = new Leaf(15, Color.BLUE);
  WorldImage circleRed = new CircleImage(10, OutlineMode.SOLID, Color.RED);
  WorldImage circleBlue = new CircleImage(15, OutlineMode.SOLID, Color.BLUE);

  // Stems
  ITree stem1 = new Stem(60, 30, this.redLeaf);
  ITree droopStem = new Stem(60, 270, this.redLeaf);
  WorldImage stemImage = new OverlayImage(this.circleRed,
          new RotateImage(new LineImage(new Posn(60, 0), Color.BLACK), 150));
  WorldImage branchLeft = new OverlayImage(this.circleRed,
          new RotateImage(new LineImage(new Posn(30, 0), Color.BLACK), 135));
  WorldImage branchRight = new OverlayImage(this.circleBlue,
          new RotateImage(new LineImage(new Posn(30, 0), Color.BLACK), 40));

  // Branches
  ITree branch1 = new Branch(30, 30, 280, 40, this.stem1, this.blueLeaf);
  WorldImage branchImage = new OverlayImage(this.branchLeft, this.branchRight);

  ITree TREE1 = new Branch(30, 30, 135, 40, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE));
  ITree TREE2 = new Branch(30, 30, 115, 65, new Leaf(15, Color.GREEN), new Leaf(8, Color.ORANGE));
  ITree full = new Branch(40, 50, 150, 30, TREE1, TREE2);
  ITree combined = TREE1.combine(40, 50, 150, 30, TREE2);

  ITree longBranchLeft = new Branch(200, 30, 170, 65, this.redLeaf, this.blueLeaf);
  ITree longLeftTree = new Branch (30, 30, 150, 40, TREE1, longBranchLeft);

  boolean testIsDrooping(Tester t) {
    return t.checkExpect(this.full.isDrooping(), false)
            && t.checkExpect(this.branch1.isDrooping(), true)
            && t.checkExpect(this.stem1.isDrooping(), false)
            && t.checkExpect(this.droopStem.isDrooping(), true)
            && t.checkExpect(this.redLeaf.isDrooping(), false);
  }

  boolean testCombine(Tester t) {
    return t.checkExpect(this.TREE1.combine(40, 50, 150, 30, this.TREE2),
            new Branch(40, 50, 150, 30,
                    new Branch(30, 30, 195, 100, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE)),
                    new Branch(30, 30, 55, 5, new Leaf(15, Color.GREEN),
                            new Leaf(8, Color.ORANGE))));
  }

  boolean testCombineHelper(Tester t) {
    return t.checkExpect(this.TREE1.combineHelper(60),
            new Branch(30, 30, 195, 100, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE)))
            && t.checkExpect(this.stem1.combineHelper(25), new Stem(60, 55, this.redLeaf))
            && t.checkExpect(this.redLeaf.combineHelper(20), this.redLeaf);
  }

  boolean testGetWidth(Tester t) {
    return t.checkExpect(this.redLeaf.getWidth(), 20.0)
            && t.checkInexact(this.stem1.getWidth(), 71.96, 0.001)
            && t.checkInexact(this.full.getWidth(), 129.83, 0.001);
  }

  boolean testGetWidthHelperLeft(Tester t) {
    return t.checkExpect(this.redLeaf.getWidthHelperLeft(), -10.0)
            && t.checkInexact(this.stem1.getWidthHelperLeft(), 41.96, 0.001)
            && t.checkInexact(this.TREE1.getWidthHelperLeft(), -31.21, 0.001);
  }

  boolean testGetWidthHelperRight(Tester t) {
    return t.checkExpect(this.blueLeaf.getWidthHelperRight(), 15.0)
            && t.checkInexact(this.stem1.getWidthHelperRight(), 61.96, 0.001)
            && t.checkInexact(this.longBranchLeft.getWidthHelperRight(), 27.67, 0.001);
  }

  boolean testDrawTree(Tester t) {
    WorldCanvas c = new WorldCanvas(500, 500);
    WorldScene s = new WorldScene(500, 500);
    return c.drawScene(s.placeImageXY(this.longLeftTree.draw(), 250, 250))
            && c.show();
  } 
}