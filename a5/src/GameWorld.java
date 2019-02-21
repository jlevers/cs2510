import javalib.worldimages.Posn;
import javalib.worldimages.WorldImage;
import tester.*;
import javalib.funworld.*;

import java.awt.Color;
import java.util.Random;
import javalib.worldimages.*;

// Represents an NBullets game
class GameWorld extends World {

  // Constants
  static final int WIDTH = 500;
  static final int HEIGHT = 300;
  static final double TICK_RATE = 1.0 / 28.0;
  static final int SHIP_SPAWN_FREQ = 28; // Ticks, not seconds

  int ticks;
  Random rand;
  int bulletsLeft;
  int shipsDown;
  ILo<IActor> ships;
  ILo<IActor> bullets;

  // The constructor that can set every field to a specific value
  GameWorld(int ticks, Random rand, int bulletsLeft, int shipsDown, ILo<IActor> ships,
      ILo<IActor> bullets) {
    this.ticks = ticks;
    this.rand = rand;
    this.bulletsLeft = bulletsLeft;
    this.shipsDown = shipsDown;
    this.ships = ships;
    this.bullets = bullets;
  }

  // Takes a specific Random object, so that a seed can be specified for testing
  GameWorld(int bulletsLeft, Random rand) {
    this(0, rand, bulletsLeft, 0, new MtLo<>(), new MtLo<>());
  }

  // The main constructor for the game
  GameWorld(int bulletsLeft) {
    this(bulletsLeft, new Random());
  }

  // Draws the current state of the GameWorld
  public WorldScene makeScene() {
    WorldScene empty = new WorldScene(GameWorld.WIDTH, GameWorld.HEIGHT);

    // Draws the Ships in the current List of Ships
    ILoDispF<IActor, WorldScene> drawShips = new FoldR<>(new DrawIActor(), empty);
    WorldScene ships = this.ships.visit(drawShips);

    // Draws the Bullets in the current List of Bullet
    ILoDispF<IActor, WorldScene> drawBullets = new FoldR<>(new DrawIActor(), ships);
    WorldScene bullets = this.bullets.visit(drawBullets);

    // Draws the counter
    Posn counter = new Posn(GameWorld.WIDTH / 4, GameWorld.HEIGHT / 8 * 7);
    WorldImage text = new TextImage(Utils.counterText(this.bulletsLeft, this.shipsDown),
        Color.BLACK);

    return bullets.placeImageXY(text, counter.x, counter.y);
  }

  // Performs a set of actions at the given tick speed
  public GameWorld onTick() {
    return this.spawn()
        .moveActors()
        .explodeBullets()
        .destroyShips()
        .removeOffscreen()
        .incTick();
  }

  // Checks if the world should end, and if it should, shows a final scene
  public WorldEnd worldEnds() {
    FoldR<IActor, Integer> bulletLen = new FoldR<>(new LengthRed<>(), 0);
    if (this.bullets.visit(bulletLen) == 0 && this.bulletsLeft == 0) {
      return new WorldEnd(true, this.makeAFinalScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // Draws the final scene
  WorldScene makeAFinalScene() {
    WorldImage endImg = new TextImage("You're out of bullets!", 24, Color.RED);
    return new WorldScene(GameWorld.WIDTH, GameWorld.HEIGHT).placeImageXY(endImg,
        GameWorld.WIDTH / 2, GameWorld.HEIGHT / 2);
  }

  // Spawns between Ship.SPAWN_MIN and Ship.SPAWN_MAX Ships
  GameWorld spawn() {
    if (this.ticks % GameWorld.SHIP_SPAWN_FREQ == 0) {
      int toSpawn = this.rand.nextInt(Ship.SHIP_SPAWN_MAX - Ship.SHIP_SPAWN_MIN)
          + Ship.SHIP_SPAWN_MIN;

      IFunc<Integer, IActor> buildShip = new BuildShip(this.rand);
      IFunc<Integer, ILo<IActor>> buildShips = new BuildList<>(buildShip);
      ILo<IActor> newShips = buildShips.call(toSpawn);
      ILoDispF<IActor, ILo<IActor>> append = new Append<>(newShips);

      return new GameWorld(0, this.rand, this.bulletsLeft, this.shipsDown, append.call(this.ships),
          this.bullets);
    }

    return this;
  }

  // Moves all of the Actors in this GameWorld
  GameWorld moveActors() {
    ILoDispF<IActor, ILo<IActor>> moveActors = new Map<>(new MoveIActor());
    return new GameWorld(this.ticks, this.rand, this.bulletsLeft, this.shipsDown,
        this.ships.visit(moveActors), this.bullets.visit(moveActors));
  }

  // Fires a bullet when the space key is pressed
  public GameWorld onKeyEvent(String key) {
    if (key.equals(" ") && this.bulletsLeft > 0) {
      return new GameWorld(this.ticks, this.rand, (this.bulletsLeft - 1), this.shipsDown,
          this.ships, new ConsLo<>(new Bullet(), this.bullets));
    }

    return this;
  }

  // Increments the tick count of the game
  GameWorld incTick() {
    return new GameWorld(this.ticks + 1, this.rand, this.bulletsLeft, this.shipsDown, this.ships,
        this.bullets);
  }

  // Explodes the bullets into more Bullets if they hit a Ship
  GameWorld explodeBullets() {
    ILoDispF<IActor, ILo<IActor>> foldToList = new FoldR<>(new CondExplodeBullet(this.ships),
        new MtLo<>());
    ILo<IActor> bulletLists = this.bullets.visit(foldToList);

    return new GameWorld(this.ticks, this.rand, this.bulletsLeft, this.shipsDown, this.ships,
        bulletLists);
  }

  // Removes Ships that were hit by Bullets
  GameWorld destroyShips() {
    ILoDispF<IActor, ILo<IActor>> filterTouching = new Filter<>(
        new ShipTouchingBullet(this.bullets));

    ILo<IActor> remainingShips = this.ships.visit(filterTouching);
    FoldR<IActor, Integer> shipLenFoldr = new FoldR<>(new LengthRed<>(), 0);
    int origLen = this.ships.visit(shipLenFoldr);
    int newLen = remainingShips.visit(shipLenFoldr);

    // Need to create a method that adds to Ships Destroyed
    return new GameWorld(this.ticks, this.rand, this.bulletsLeft,
        (origLen - newLen) + this.shipsDown, remainingShips, this.bullets);
  }

  // Removes offscreen IActors
  GameWorld removeOffscreen() {
    Filter<IActor> filterOffscreen = new Filter<>(new NotOffscreen());

    return new GameWorld(this.ticks, this.rand, this.bulletsLeft, this.shipsDown,
        this.ships.visit(filterOffscreen), this.bullets.visit(filterOffscreen));
  }
}

// Represents a function that checks if an IActor is still onscreen
class NotOffscreen implements IPred<IActor> {
  // Checks if the given IActor is on screen
  public Boolean call(IActor actor) {
    return !actor.offscreen();
  }
}

// Represents a function that moves a list of IActors
class MoveIActor implements IFunc<IActor, IActor> {
  // Moves the given IActor using its velocity
  public IActor call(IActor toMove) {
    return toMove.move();
  }
}

// Represents a function to map a Bullet to an ILo<Bullet>
class CondExplodeBullet implements IRed<IActor, ILo<IActor>> {
  ILo<IActor> ships;

  CondExplodeBullet(ILo<IActor> ships) {
    this.ships = ships;
  }

  // Folds a bullet to the current list of bullets, depending on whether or not it
  // was touching 1+ ships
  public ILo<IActor> red(IActor bullet, ILo<IActor> bulletListBase) {
    ILoDispF<IActor, Boolean> isHitBullet = new FoldR<>(new IActorHitIActor(bullet), false);

    ILo<IActor> explodedBullet = bullet.explode();
    Append<IActor> append = new Append<>(explodedBullet);

    if (this.ships.visit(isHitBullet)) {
      return append.call(bulletListBase);
    }
    else {
      return new ConsLo<>(bullet, bulletListBase);
    }
  }
}

// Represents a function that checks if a Bullet is touching a Ship
class IActorHitIActor implements IRed<IActor, Boolean> {
  IActor isTouchingThis;

  IActorHitIActor(IActor isTouchingThis) {
    this.isTouchingThis = isTouchingThis;
  }

  // Checks if this.isTouchingThis is touching this bullet, or if any IActor was
  // touching this
  // .isTouchingThis
  public Boolean red(IActor checkIfTouching, Boolean aBoolean) {
    return checkIfTouching.isTouching(this.isTouchingThis) || aBoolean;
  }
}

// Represents a predicate that checks if a Ship is touching any of a list of Bullets
class ShipTouchingBullet implements IPred<IActor> {
  ILo<IActor> bullets;

  ShipTouchingBullet(ILo<IActor> bullets) {
    this.bullets = bullets;
  }

  // Checks if the passed ship is touching any of the bullets in this.bullets
  public Boolean call(IActor ship) {
    ILoDispF<IActor, Boolean> isHitBullet = new FoldR<>(new IActorHitIActor(ship), false);
    return !bullets.visit(isHitBullet);
  }
}

// Draws an IActor, dispatching so that it can be drawn based on its subclass
class DrawIActor implements IRed<IActor, WorldScene> {

  // Creates a WorldImage with the given IActor overlaid on the base
  public WorldScene red(IActor that, WorldScene base) {
    IActorDispF<Posn> determinePosn = new DeterminePosn();

    IActorDispF<WorldImage> drawThat = new DrawThat();
    // WorldImage movedPinhole = base.movePinholeTo(that.accept(determinePosn));
    Posn thatPosn = (that.accept(determinePosn));

    return base.placeImageXY(that.accept(drawThat), thatPosn.x, thatPosn.y);
  }
}

// Represents a dispatch function for figuring out the Posn of an IActor
class DeterminePosn implements IActorDispF<Posn> {

  // Allows the given IActor to accept this function
  public Posn call(IActor x) {
    return x.accept(this);
  }

  // Determines the position of this ship
  public Posn forShip(Ship ship) {
    return ship.pos;
  }

  // Determines the position of this bullet
  public Posn forBullet(Bullet bullet) {
    return bullet.pos;
  }
}

// Draws some IActor on the WorldImage
class DrawThat implements IActorDispF<WorldImage> {

  // Allows the given IActor to call this function
  public WorldImage call(IActor x) {
    return x.accept(this);
  }

  // Draws the given Ship
  public WorldImage forShip(Ship ship) {
    return new CircleImage(ship.size, OutlineMode.SOLID, Ship.COLOR);
  }

  // Draws the given Bullet
  public WorldImage forBullet(Bullet bullet) {
    return new CircleImage(bullet.size, OutlineMode.SOLID, Bullet.COLOR);
  }
}

class ExamplesGameWorld {
  Posn b1Loc = new Posn(0, 0);
  Posn b1Vel = new Posn(3, 4);

  Bullet b1 = new Bullet(this.b1Vel, this.b1Loc, 0);
  Bullet b1Moved = new Bullet(this.b1Vel, this.b1Vel, 0);
  Bullet rightoff = new Bullet(this.b1Vel, new Posn(503, 100), 0);
  Bullet leftoff = new Bullet(this.b1Vel, new Posn(-3, 100), 0);
  Bullet topoff = new Bullet(this.b1Vel, new Posn(100, 303), 0);
  Bullet bottomoff = new Bullet(this.b1Vel, new Posn(100, -3), 0);

  Posn s1Loc = new Posn(4, 6);
  Posn s1Vel = new Posn(10, 0);
  Posn s1VelLeft = new Posn(-10, 0);
  Posn s1MovedLoc = new Posn(14, 6);

  Ship s1 = new Ship(this.s1Vel, this.s1Loc);
  Ship s1Moved = new Ship(this.s1Vel, this.s1MovedLoc);
  Ship rightoffs = new Ship(this.s1Vel, new Posn(507, 100));
  Ship leftoffs = new Ship(this.s1VelLeft, new Posn(-7, 100));
  Ship topoffs = new Ship(this.s1Vel, new Posn(100, 307));
  Ship bottomoffs = new Ship(this.s1Vel, new Posn(100, -7));

  ILo<IActor> mtBullets = new MtLo<>();
  ILo<IActor> mtShips = new MtLo<>();
  ILo<IActor> listBullets = new ConsLo<>(this.b1,
      new ConsLo<>(this.rightoff, new ConsLo<>(this.b1Moved, this.mtBullets)));
  ILo<IActor> listShips = new ConsLo<>(this.s1,
      new ConsLo<>(this.s1Moved, new ConsLo<>(this.leftoffs, this.mtShips)));

  Random random1 = new Random(1);

  GameWorld game1 = new GameWorld(0, this.random1, 10, 0, this.listShips, this.listBullets);
  Ship newShip1 = new Ship(new Posn(-2, 0), new Posn(506, 169));
  Ship newShip2 = new Ship(new Posn(-2, 0), new Posn(506, 104));
  ILo<IActor> appendedShips = new ConsLo<>(this.s1,
      new ConsLo<>(this.s1Moved, new ConsLo<>(this.leftoffs,
          new ConsLo<>(this.newShip1, new ConsLo<>(this.newShip2, this.mtShips)))));

  ILo<IActor> filteredShips = new ConsLo<>(this.s1, new ConsLo<>(this.s1Moved, this.mtShips));
  ILo<IActor> filteredBullets = new ConsLo<>(this.b1, new ConsLo<>(this.b1Moved, this.mtBullets));

  Ship s1MovedAgain = new Ship(this.s1Vel, new Posn(24, 6));
  Ship leftOffsMoved = new Ship(this.s1VelLeft, new Posn(-17, 100));
  ILo<IActor> movedShips = new ConsLo<>(this.s1Moved,
      new ConsLo<>(this.s1MovedAgain, new ConsLo<>(this.leftOffsMoved, this.mtShips)));

  Bullet b1MovedAgain = new Bullet(this.b1Vel, new Posn(6, 8), 0);
  Bullet rightOffMoved = new Bullet(this.b1Vel, new Posn(506, 104), 0);
  ILo<IActor> movedBullets = new ConsLo<>(this.b1Moved,
      new ConsLo<>(this.rightOffMoved, new ConsLo<>(this.b1MovedAgain, this.mtBullets)));

  GameWorld gameSpawn = new GameWorld(0, this.random1, 10, 0, this.appendedShips, this.listBullets);
  GameWorld mtGW = new GameWorld(0, this.random1, 10, 0, new MtLo<>(), new MtLo<>());

  IActor preExpShip = new Ship(new Posn(5, 0), new Posn(30, 40));
  IActor preExpBullet = new Bullet(new Posn(2, 4), new Posn(32, 44), 1);
  IActor exploded1 = new Bullet(new Posn(-1 * Bullet.SPEED, 0), new Posn(32, 44), 2);
  IActor exploded2 = new Bullet(new Posn(Bullet.SPEED, 0), new Posn(32, 44), 2);
  ILo<IActor> explodedList = new ConsLo<>(this.exploded1,
      new ConsLo<>(this.exploded2, new MtLo<>()));
  GameWorld gToExplode = new GameWorld(0, this.random1, 10, 0,
      new ConsLo<>(this.preExpShip, new MtLo<>()), new ConsLo<>(this.preExpBullet, new MtLo<>()));

  // Tests spawn method on given GameWorld
  public boolean testSpawn(Tester t) {
    return t.checkExpect(this.game1.spawn(), this.gameSpawn);
  }

  //Tests if the appropriate IActors get removed offscreen
  public boolean testRemoveOffscreen(Tester t) {
    return t.checkExpect(this.game1.removeOffscreen(),
        new GameWorld(0, this.random1, 10, 0, this.filteredShips, this.filteredBullets));
  }

  //Tests if all the IActors move by their respective velocities
  public boolean testMoveActors(Tester t) {
    return t.checkExpect(this.game1.moveActors(),
        new GameWorld(0, this.random1, 10, 0, this.movedShips, this.movedBullets));
  }

  //Tests if a new bullet is created given the correct key
  public boolean testOnKey(Tester t) {
    return t.checkExpect(this.game1.onKeyEvent(" "),
        new GameWorld(0, this.random1, 9, 0, this.listShips,
            new ConsLo<>(new Bullet(), this.listBullets)))
        && t.checkExpect(this.game1.onKeyEvent("A"), this.game1);
  }

  //Tests if 1 is added to the number of ticks in the current Game
  boolean testIncTick(Tester t) {
    return t.checkExpect(this.game1.incTick(),
        new GameWorld(1, this.random1, 10, 0, this.listShips, this.listBullets));
  }

  //Tests if the appropriate bullets explode and are appended to the list
  boolean testExplodeBullets(Tester t) {
    return t
        .checkExpect(this.gToExplode.explodeBullets(), new GameWorld(0, this.random1, 10, 0,
            new ConsLo<>(this.preExpShip, new MtLo<>()), this.explodedList))
        && t.checkExpect(this.mtGW.explodeBullets(), this.mtGW);
  }

  //Tests if the appropriate ships are destroyed
  boolean testDestroyShips(Tester t) {
    return t.checkExpect(this.gToExplode.destroyShips(),
        new GameWorld(0, this.random1, 10, 1, new MtLo<>(),
            new ConsLo<>(this.preExpBullet, new MtLo<>())))
        && t.checkExpect(this.mtGW.destroyShips(), this.mtGW);
  }

  //Tests if the given IActor is not offscreen
  public boolean testNotOffscreen(Tester t) {
    IPred<IActor> os = new NotOffscreen();

    return t.checkExpect(os.call(this.rightoff), false)
        && t.checkExpect(os.call(this.leftoff), false) && t.checkExpect(os.call(this.topoff), false)
        && t.checkExpect(os.call(this.bottomoff), false)
        && t.checkExpect(os.call(this.rightoffs), false)
        && t.checkExpect(os.call(this.leftoffs), false)
        && t.checkExpect(os.call(this.topoffs), false)
        && t.checkExpect(os.call(this.bottomoffs), false) && t.checkExpect(os.call(this.b1), true)
        && t.checkExpect(os.call(this.s1), true);
  }

  //Tests if the given IActor moves
  public boolean testMoveIActor(Tester t) {
    IFunc<IActor, IActor> ma = new MoveIActor();

    return t.checkExpect(ma.call(this.b1), this.b1Moved)
        && t.checkExpect(ma.call(this.s1), this.s1Moved);
  }
  
  //Tests if the makeScene function draws all IActors and Counter
  public boolean testMakeScene(Tester t) {
   IActor ship1 = new Ship(new Posn(10,0), new Posn(250,100));
   IActor bullet1 = new Bullet(new Posn(5, 2), new Posn(400, 250), 1);
   GameWorld drawg = new GameWorld(0, this.random1, 10, 0,
     new ConsLo<IActor>(ship1, new MtLo<IActor>()),
     new ConsLo<IActor>(bullet1, new MtLo<IActor>()));
   
   WorldScene empty = new WorldScene(GameWorld.WIDTH, GameWorld.HEIGHT);
   
   WorldImage ship = new CircleImage(6, OutlineMode.SOLID, Ship.COLOR);
   WorldScene drawnShip = empty.placeImageXY(ship, 250, 100);
   
   WorldImage bullet = new CircleImage(4 , OutlineMode.SOLID, Bullet.COLOR);
   WorldScene drawnBullet = drawnShip.placeImageXY(bullet, 400, 250);
   
   WorldImage text = new TextImage("Bullets Left: 10; Ships Down: 0", 13, Color.BLACK);
   WorldScene drawnText = 
       drawnBullet.placeImageXY(text, GameWorld.WIDTH / 4, GameWorld.HEIGHT / 8 * 7);
   
   return t.checkExpect(drawg.makeScene(), drawnText);
 }
  
  //Tests if the position of the IActor is returned
  public boolean testDeterminePosn(Tester t) {
    IActorDispF<Posn> dp = new DeterminePosn();

    return t.checkExpect(this.b1.accept(dp), new Posn(0, 0))
        && t.checkExpect(this.s1.accept(dp), new Posn(4, 6));
  }

  //Tests if the appropriate image is drawn for the given IActor
  public boolean testDrawThat(Tester t) {
    IActorDispF<WorldImage> dt = new DrawThat();

    return t.checkExpect(this.b1.accept(dt),
        new CircleImage(this.b1.size, OutlineMode.SOLID, Bullet.COLOR))
        && t.checkExpect(this.s1.accept(dt),
            new CircleImage(this.s1.size, OutlineMode.SOLID, Ship.COLOR));
  }
  
  //Tests the bigBang function
  boolean testBigBang(Tester t) {
    GameWorld gw = new GameWorld(10);

    return gw.bigBang(GameWorld.WIDTH, GameWorld.HEIGHT, GameWorld.TICK_RATE);
  }
  
  //Tests if the given IActor explodes, and if it forms the correct bullets
  public boolean testCondExplodeBullet(Tester t) {
    CondExplodeBullet ceb = new CondExplodeBullet(new ConsLo<>(this.preExpShip, new MtLo<>()));
    return t.checkExpect(ceb.red(this.preExpBullet, new MtLo<>()), this.explodedList)
        && t.checkExpect(ceb.red(this.b1, new MtLo<>()), new ConsLo<>(this.b1, new MtLo<>()));
  }

  //Tests if the given IActor hit any IActors in a list
  public boolean testIActorHitIActor(Tester t) {
    IActorHitIActor iahia = new IActorHitIActor(this.b1);
    return t.checkExpect(iahia.red(this.s1, false), true)
        && t.checkExpect(iahia.red(this.exploded1, false), false)
        && t.checkExpect(iahia.red(this.exploded1, true), true);
  }
  
  //Tests if a Ship is touching any Bullet in a list of Bullets
  public boolean testShipTouchingBullet(Tester t) {
    ShipTouchingBullet stb = new ShipTouchingBullet(this.listBullets);
    return t.checkExpect(stb.call(this.s1), false)
        && t.checkExpect(stb.call(this.preExpShip), true);
  }
}