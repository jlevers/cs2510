import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.util.Random;

// keyhandler
// draw
//  - draw score
//  - draw bullets
//  - draw ships
// ontick equivalent
// worldend



// Represents an NBullets game
class GameWorld extends World {

  // Constants
  static final int WIDTH = 500;
  static final int HEIGHT = 300;
  static final double TICK_RATE = 1.0 / 28.0;
  static final int SHIP_SPAWN_FREQ = 28;  // Ticks, not seconds

  Random rand;
  int bulletsLeft;
  int shipsDown;
  ILo<IActor> ships;
  ILo<IActor> bullets;

  // The constructor that can set every field to a specific value
  GameWorld(Random rand, int bulletsLeft, int shipsDown, ILo<IActor> ships, ILo<IActor> bullets) {
    this.rand = rand;
    this.bulletsLeft = bulletsLeft;
    this.shipsDown = shipsDown;
    this.ships = ships;
    this.bullets = bullets;
  }

  // Takes a specific Random object, so that a seed can be specified for testing
  GameWorld(int bulletsLeft, Random rand) {
    this(rand, bulletsLeft, 0, new MtLo<>(), new MtLo<>());
  }

  // The main constructor for the game
  GameWorld(int bulletsLeft) {
    this(bulletsLeft, new Random());
  }

  // Draws the current state of the GameWorld
  public WorldScene makeScene() {
    return new WorldScene(this.WIDTH, this.HEIGHT);
  }
  
  //Performs a set of actions at the given tick speed
  public GameWorld onTick() {
    return this.spawn()
      .moveActors()
      .explodeBullets()
      .destroyShips() //removes ships that were in contact with any Bullet
      .removeOffscreen();
  }

  // Spawns between Ship.SPAWN_MIN and Ship.SPAWN_MAX Ships
  GameWorld spawn() {
    // TODO: only spawn on each second (not each tick)
    int toSpawn = this.rand.nextInt(Ship.SHIP_SPAWN_MAX - Ship.SHIP_SPAWN_MIN)
        + Ship.SHIP_SPAWN_MIN;
    ILo<IActor> newShips = new MtLo<>();

    for (int i = 0; i < toSpawn; i++) {
      boolean chooseSide = rand.nextBoolean();
      int velX = chooseSide ? Ship.SPEED : -1 * Ship.SPEED;  // True = left, false = right
      int spawnHeight = rand.nextInt(Ship.SPAWN_TOP - Ship.SPAWN_BOTTOM) + Ship.SPAWN_BOTTOM;
      int spawnWidth = chooseSide ? -1 * Ship.SIZE : GameWorld.WIDTH + Ship.SIZE;
      Ship newShip = new Ship(new Posn(velX, 0), new Posn(spawnWidth, spawnHeight));

      newShips = new ConsLo<>(newShip, newShips);
    }

    ILoDispF<IActor, ILo<IActor>> append = new Append<>(newShips);

    return new GameWorld(this.rand, this.bulletsLeft, this.shipsDown, append.call(this.ships),
            this.bullets);
  }
  
  // Moves all of the Actors in this Gameworld
  GameWorld moveActors() {
    ILoDispF<IActor, ILo<IActor>> moveActors = new Map<>(new MoveIActor());
    return new GameWorld(this.rand, this.bulletsLeft, this.shipsDown, 
                         this.ships.visit(moveActors), this.bullets.visit(moveActors));
  }

  // Fires a bullet when the space key is pressed
  GameWorld onKey(String key) {
    if (key.equals(" ")) {
      return new GameWorld(this.rand, (this.bulletsLeft - 1), this.shipsDown, this.ships,
                           new ConsLo<IActor>(new Bullet(), this.bullets));
    }
    return this;
  }

  // Explodes the bullets into more Bullets if they hit a Ship
  GameWorld explodeBullets() {
    // TODO: FIX THIS
    return this;
  }

  // Removes Ships that were hit by Bullets
  GameWorld destroyShips() {
    // TODO: ADD
    return this;
  }

  // Removes offscreen IActors
  GameWorld removeOffscreen() {
    ILoDispF<IActor,ILo<IActor>> filterOffscreen =
            new Filter<IActor>(new NotOffscreen());
    
    return new GameWorld(this.rand, this.bulletsLeft, this.shipsDown,
            this.ships.visit(filterOffscreen), this.bullets.visit(filterOffscreen));
  }
}

class NotOffscreen implements IPred<IActor> {

  // Checks if the given IActor is on screen
  public boolean apply(IActor actor) {
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


class ExamplesGameWorld {
  Posn b1Loc = new Posn(0, 0);
  Posn b1Vel = new Posn(3, 4);

  Bullet b1 = new Bullet(this.b1Vel, this.b1Loc, 0);
  Bullet b1Moved = new Bullet(this.b1Vel, this.b1Vel, 0);
  Bullet rightoff = new Bullet(this.b1Vel, new Posn(503, 100), 0);
  Bullet leftoff = new Bullet(this.b1Vel, new Posn(-3, 100), 0);
  Bullet topoff = new Bullet(this.b1Vel, new Posn (100, 303), 0);
  Bullet bottomoff = new Bullet(this.b1Vel, new Posn(100, -3), 0);

  Posn s1Loc = new Posn(4, 6);
  Posn s1Vel = new Posn(10, 0);
  Posn s1VelLeft = new Posn(-10, 0);
  Posn s1MovedLoc = new Posn(14, 6);

  Ship s1 = new Ship(this.s1Vel, this.s1Loc);
  Ship s1Moved = new Ship(this.s1Vel, this.s1MovedLoc);
  Ship rightoffs = new Ship(this.s1Vel, new Posn(507, 100));
  Ship leftoffs = new Ship(this.s1VelLeft, new Posn(-7, 100));
  Ship topoffs = new Ship(this.s1Vel, new Posn (100, 307));
  Ship bottomoffs = new Ship(this.s1Vel, new Posn(100, -7));
  
  ILo<IActor> mtBullets = new MtLo<>();
  ILo<IActor> mtShips = new MtLo<>();
  ILo<IActor> listBullets = new ConsLo<>(this.b1, new ConsLo<>(this.rightoff, 
      new ConsLo<>(this.b1Moved, this.mtBullets)));
  ILo<IActor> listShips = new ConsLo<>(this.s1, new ConsLo<>(this.s1Moved,
      new ConsLo<>(this.leftoffs, this.mtShips)));
  
  Random random1 = new Random(1);
  
  GameWorld game1 = new GameWorld(this.random1, 10, 0, this.listShips, this.listBullets);
  Ship newShip1 = new Ship(new Posn(-2, 0), new Posn (506, 104));
  Ship newShip2 = new Ship(new Posn(-2, 0), new Posn(506, 169));
  ILo<IActor> appendedShips = new ConsLo<>(this.s1, new ConsLo<>(this.s1Moved,
      new ConsLo<>(this.leftoffs, new ConsLo<>(this.newShip1,
          new ConsLo<>(this.newShip2,this.mtShips)))));
  
  ILo<IActor> filteredShips = new ConsLo<>(this.s1, new ConsLo<>(this.s1Moved,this.mtShips));
  ILo<IActor> filteredBullets = new ConsLo<>(this.b1, new ConsLo<>(this.b1Moved, this.mtBullets));
  
  IActor s1MovedAgain = new Ship(this.s1Vel, new Posn(24,6)); 
  IActor leftOffsMoved = new Ship(this.s1VelLeft, new Posn(-17,100));
  ILo<IActor> movedShips = new ConsLo<>(this.s1Moved, new ConsLo<>(this.s1MovedAgain,
      new ConsLo<>(this.leftOffsMoved,this.mtShips)));
  
  IActor b1MovedAgain = new Bullet(this.b1Vel, new Posn(6, 8), 0);
  IActor rightOffMoved = new Bullet(this.b1Vel, new Posn(506, 104), 0);
  ILo<IActor> movedBullets = new ConsLo<>(this.b1Moved, new ConsLo<>(this.rightOffMoved,
      new ConsLo<>(this.b1MovedAgain, this.mtBullets)));
  
  GameWorld gameSpawn = new GameWorld(this.random1, 10, 0,
      this.appendedShips, this.listBullets);
  
  //Tests spawn method on given GameWorld
  public boolean testSpawn(Tester t) {
    return t.checkExpect(this.game1.spawn(), this.gameSpawn );
  }
  
  public boolean testRemoveOffscreen(Tester t) {
    return t.checkExpect(this.game1.removeOffscreen(), 
        new GameWorld(this.random1, 10, 0, this.filteredShips, this.filteredBullets));
  }
  
  public boolean testMoveActors(Tester t) {
    return t.checkExpect(this.game1.moveActors(), 
        new GameWorld(this.random1, 10, 0, this.movedShips, this.movedBullets));
  }
  
  public boolean testOnKey(Tester t) {
    return t.checkExpect(this.game1.onKey(" "), 
        new GameWorld(this.random1, 9, 0, this.listShips,
            new ConsLo<IActor>(new Bullet(), this.listBullets)))
        && t.checkExpect(this.game1.onKey("A"), this.game1);
  }
  
  public boolean testNotOffscreen(Tester t) {
    IPred<IActor> os = new NotOffscreen();
    
    return t.checkExpect(os.apply(this.rightoff), false)
        && t.checkExpect(os.apply(this.leftoff), false)
        && t.checkExpect(os.apply(this.topoff), false)
        && t.checkExpect(os.apply(this.bottomoff), false)
        && t.checkExpect(os.apply(this.rightoffs), false)
        && t.checkExpect(os.apply(this.leftoffs), false)
        && t.checkExpect(os.apply(this.topoffs), false)
        && t.checkExpect(os.apply(this.bottomoffs), false)
        && t.checkExpect(os.apply(this.b1), true)
        && t.checkExpect(os.apply(this.s1), true);
  }
  
  public boolean testMoveIActor(Tester t) {
    IFunc<IActor, IActor> ma = new MoveIActor();
    
    return t.checkExpect(ma.call(this.b1), this.b1Moved)
        && t.checkExpect(ma.call(this.s1), this.s1Moved);
  }
}