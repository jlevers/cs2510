import tester.*;

interface IPicture {
  // Returns the width of the Picture
  int getWidth();
  
  // Counts the number of shapes in the Picture
  int countShapes();
  
  // Gets the number of operations used to create this Picture
  int comboDepth();
  
  // Produces the mirror image of this Picture
  IPicture mirror();
  
  // Produces the recipe to make this Picture at the given depth
  String pictureRecipe(int depth);
}

class Shape implements IPicture {
  String kind;
  int size;
  
  Shape(String kind, int size) {
    this.kind = kind;
    this.size = size;
  }
  
  /* Template:
   * Fields:
   * this.kind ... String
   * this.size ... int
   * 
   * Methods:
   * this.getWidth() ... int
   * this.countShapes() ... int
   * this.comboDepth() ... int
   * this.mirror() ... IPicture
   * this.pictureRecipe(int) ... String
   * 
   * Methods of fields:
   */
  
  // Returns the width of the Shape
  public int getWidth() {
    return this.size;
  }
  
  // Counts the number of shapes in this Shape (always 1)
  public int countShapes() {
    return 1;
  }
  
  // Counts the number of operations required to make this Shape (always 0)
  public int comboDepth() {
    return 0;
  }
  
  // Returns the mirror of this Shape, which is this
  public IPicture mirror() {
    return this;
  }
  
  // Gets the recipe for this picture
  public String pictureRecipe(int depth) {
    return this.kind;
  }
}

class Combo implements IPicture {
  String name;
  IOperation operation;
  
  Combo(String name, IOperation operation) {
    this.name = name;
    this.operation = operation;
  }
  
  /* Template:
   * Fields:
   * this.name ... String
   * this.operation ... String
   * 
   * Methods:
   * this.getWidth() ... int
   * this.countShapes() ... int
   * this.comboDepth() ... int
   * this.mirror() ... IPicture
   * this.pictureRecipe(int) ... String
   * 
   * Methods of fields:
   * this.operation.getWidth() ... int
   * this.operation.countShapes() ... int
   * this.operation.comboDepth() ... int
   * this.operation.mirror() ... IOperation
   * this.operation.operationRecipe(int) ... String
   */
  
  // Returns the width of this combination of shapes
  public int getWidth() {
    return this.operation.getWidth();
  }
  
  // Counts the number of shapes in this Combo
  public int countShapes() {
    return this.operation.countShapes();
  }
  
  // Counts the number of operations required to create this Combo
  public int comboDepth() {
    return this.operation.comboDepth();
  }
  
  // Gets the mirror image of this Combo
  public IPicture mirror() {
    return new Combo(this.name, this.operation.mirror());
  }
  
  // Gets the recipe for this Combo at the given depth
  public String pictureRecipe(int depth) {
    if (depth > 0) {
      return this.operation.operationRecipe(depth - 1);
    }
    
    return this.name;
  }
}

interface IOperation {
  // Returns the width of the result of the operation
  int getWidth();
  
  // Returns the number of Shapes after the Operation is completed
  int countShapes();
  
  // Returns the number of operations used to create this Operation's image(s)
  int comboDepth();
  
  // Produces the mirror image of the result of this Operation
  IOperation mirror();
  
  // Produces the recipe for this operation to the given depth
  String operationRecipe(int depth);
}

class Scale implements IOperation {
  IPicture picture;

  Scale(IPicture picture) {
    this.picture = picture;
  }
  /* Template:
   * Fields:
   * this.picture ... IPicture
   * 
   * Methods:
   * this.getWidth() ... int
   * this.countShapes() ... int
   * this.comboDepth() ... int
   * this.mirror() ... IOperation
   * this.operationRecipe(int) ... String
   * 
   * Methods of fields:
   * this.picture.getWidth() ... int
   * this.picture.countShapes() ... int
   * this.picture.comboDepth() ... int
   * this.picture.mirror() ... IPicture
   * this.picture.pictureRecipe(int) ... String
   */
  
  // Returns the width of the scaled picture
  public int getWidth() {
    return 2 * this.picture.getWidth();
  }
  
  // Returns the number of shapes in this.picture
  public int countShapes() {
    return this.picture.countShapes();
  }
  
  // Returns the the number of operations used to create this.picture (plus one)
  public int comboDepth() {
    return 1 + this.picture.comboDepth();
  }
  
  // Returns the mirror image of this.picture
  public IOperation mirror() {
    return new Scale(this.picture.mirror());
  }
  
  // Returns the operation recipe with the given depth
  public String operationRecipe(int depth) {
    return "scale(" + this.picture.pictureRecipe(depth) + ")";
  }
}

class Beside implements IOperation {
  IPicture picture1;
  IPicture picture2;
  
  Beside(IPicture picture1, IPicture picture2) {
    this.picture1 = picture1;
    this.picture2 = picture2;
  }
  
  /* Template:
   * Fields:
   * this.picture1 ... IPicture
   * this.picture2 ... IPicture
   * 
   * Methods:
   * this.getWidth() ... int
   * this.countShapes() ... int
   * this.comboDepth() ... int
   * this.mirror() ... IOperation
   * this.operationRecipe(int) ... String
   * 
   * Methods of fields:
   * this.picture1.getWidth() ... int
   * this.picture1.countShapes() ... int
   * this.picture1.comboDepth() ... int
   * this.picture1.mirror() ... IPicture
   * this.picture1.pictureRecipe(int) ... String
   * this.picture2.getWidth() ... int
   * this.picture2.countShapes() ... int
   * this.picture2.comboDepth() ... int
   * this.picture2.mirror() ... IPicture
   * this.picture2.pictureRecipe(int) ... String
   */
  
  // Returns the width of the two pictures beside each other
  public int getWidth() {
    return this.picture1.getWidth() + this.picture2.getWidth();
  }
  
  // Returns the number of shapes for each picture
  public int countShapes() {
    return this.picture1.countShapes() + this.picture2.countShapes();
  }
  
  // Returns the the number of operations used to create this.picture1 and this.picture2 (plus one)
  public int comboDepth() {
    return 1 + Math.max(this.picture1.comboDepth(), this.picture2.comboDepth());
  }
  
  // Returns the mirror image of this Beside operation
  public IOperation mirror() {
    return new Beside(this.picture2.mirror(), this.picture1.mirror());
  }
  
  // Returns the operation recipe for this operation to the given depth
  public String operationRecipe(int depth) {
    return "beside(" + this.picture1.pictureRecipe(depth) + ", "
        + this.picture2.pictureRecipe(depth) + ")";
  }
}

class Overlay implements IOperation {
  IPicture topPicture;
  IPicture bottomPicture;
  
  Overlay(IPicture topPicture, IPicture bottomPicture) {
    this.topPicture = topPicture;
    this.bottomPicture = bottomPicture;
  }  
  
  /* Template:
   * Fields:
   * this.topPicture ... IPicture
   * this.bottomPicture ... IPicture
   * 
   * Methods:
   * this.getWidth() ... int
   * this.countShapes() ... int
   * this.comboDepth() ... int
   * this.mirror() ... IOperation
   * this.operationRecipe(int) ... String
   * 
   * Methods of fields:
   * this.topPicture.getWidth() ... int
   * this.topPicture.countShapes() ... int
   * this.topPicture.comboDepth() ... int
   * this.topPicture.mirror() ... IPicture
   * this.topPicture.operationRecipe(int) ... String
   * this.bottomPicture.getWidth() ... int
   * this.bottomPicture.countShapes() ... int
   * this.bottomPicture.comboDepth() ... int
   * this.bottomPicture.mirror() ... IPicture
   * this.bottomPicture.operationRecipe(int) ... String
   */
  
  // Returns the width of the wider picture in the overlay
  public int getWidth() {
    return Math.max(this.topPicture.getWidth(), this.bottomPicture.getWidth());
  }
  
  // Returns the number of shapes in the overlay
  public int countShapes() {
    return this.topPicture.countShapes() + this.bottomPicture.countShapes();
  }
  
  // Returns the number of operations used to create this.topPicture and
  // this.bottomPicture (plus one)
  public int comboDepth() {
    return 1 + Math.max(this.topPicture.comboDepth(), this.bottomPicture.comboDepth());
  }
  
  // Returns the mirror image of this overlay operation
  public IOperation mirror() {
    return new Overlay(this.topPicture.mirror(), this.bottomPicture.mirror());
  }
  
  // Gets the operation recipe for this operation to the given depth
  public String operationRecipe(int depth) {
    return "overlay(" + this.topPicture.pictureRecipe(depth) + ", "
        + this.bottomPicture.pictureRecipe(depth) + ")";
  }
}

class ExamplesPicture {
  
  IPicture circle = new Shape("circle", 20);
  IPicture square = new Shape("square", 30);
  
  IOperation scaleCircle = new Scale(this.circle);
  IPicture bigCircle = new Combo("big circle", this.scaleCircle);
  
  IOperation overlaySquareOnCircle = new Overlay(this.square, this.bigCircle);
  IPicture squareOnCircle = new Combo("square on circle", this.overlaySquareOnCircle);
  
  IOperation besideDoubledSquareOnCircle = new Beside(this.squareOnCircle,this.squareOnCircle);
  IPicture doubledSquareOnCircle = new Combo("doubled square on circle",
      this.besideDoubledSquareOnCircle);
  
  
  IPicture diamond = new Shape("diamond", 15);
  IPicture rectangle = new Shape("rectangle", 30);
  
  IOperation overlayDiamondOnRect = new Overlay(this.diamond, this.rectangle);
  IPicture diamondOnRectangle = new Combo("diamond on rectangle", this.overlayDiamondOnRect);
  
  IOperation scaleDOR = new Scale(this.diamondOnRectangle);
  IPicture bigDiamondOnRectangle = new Combo("big diamond on rectangle", this.scaleDOR);
  
  IOperation doubleBDOR = new Beside(this.bigDiamondOnRectangle, this.bigDiamondOnRectangle);
  IPicture doubleDIamondOnRectangle = new Combo("double diamond on rectangle", this.doubleBDOR);
  
  IOperation besideDiamondRect = new Beside(this.diamond, this.rectangle);
  IPicture diamondRect = new Combo("diamond beside rectangle", this.besideDiamondRect);
  
  IOperation besideRectDiamond = new Beside(this.rectangle, this.diamond);
  IPicture rectDiamond = new Combo("rectangle beside diamond", this.besideRectDiamond);
  
  boolean testGetWidth(Tester t) {
    return t.checkExpect(this.circle.getWidth(), 20)
        && t.checkExpect(this.doubledSquareOnCircle.getWidth(), 80);
  }
  
  boolean testGetWidthOperations(Tester t) {
    return t.checkExpect(this.overlayDiamondOnRect.getWidth(), 30)
        && t.checkExpect(this.scaleCircle.getWidth(), 40)
        && t.checkExpect(this.besideDoubledSquareOnCircle.getWidth(), 80);
  }
  
  boolean testCountShapes(Tester t) {
    return t.checkExpect(this.circle.countShapes(), 1)
        && t.checkExpect(this.doubledSquareOnCircle.countShapes(), 4);
  }
  
  boolean testCountShapesOperation(Tester t) {
    return t.checkExpect(this.besideDoubledSquareOnCircle.countShapes(), 4)
        && t.checkExpect(this.overlayDiamondOnRect.countShapes(), 2)
        && t.checkExpect(this.scaleCircle.countShapes(), 1);
  }
  
  boolean testComboDepth(Tester t) {
    return t.checkExpect(this.circle.comboDepth(), 0)
        && t.checkExpect(this.doubledSquareOnCircle.comboDepth(), 3);
  }
  
  boolean testComboDepthOperation(Tester t) {
    return t.checkExpect(this.scaleCircle.comboDepth(), 1)
        && t.checkExpect(this.doubleBDOR.comboDepth(), 3)
        && t.checkExpect(this.overlaySquareOnCircle.comboDepth(), 2);
  }
  
  boolean testMirror(Tester t) {
    return t.checkExpect(this.circle.mirror(), this.circle)
        && t.checkExpect(this.besideRectDiamond.mirror(), this.besideDiamondRect);
  }
  
  boolean testMirrorOperation(Tester t) {
    return t.checkExpect(this.scaleCircle.mirror(), this.scaleCircle)
        && t.checkExpect(this.besideDiamondRect.mirror(), this.besideRectDiamond)
        && t.checkExpect(this.overlayDiamondOnRect.mirror(), this.overlayDiamondOnRect);
  }
  
  boolean testPictureRecipe(Tester t) {
    return t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(0), "doubled square on circle")
        && t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(2),
            "beside(overlay(square, big circle), overlay(square, big circle))")
        && t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(3),
            "beside(overlay(square, scale(circle)), overlay(square, scale(circle)))");
  }
  
  boolean testOperationRecipe(Tester t) {
    return t.checkExpect(this.scaleCircle.operationRecipe(1), "scale(circle)")
        && t.checkExpect(this.besideDiamondRect.operationRecipe(2), "beside(diamond, rectangle)")
        && t.checkExpect(this.overlayDiamondOnRect.operationRecipe(0),
            "overlay(diamond, rectangle)");
  }
}