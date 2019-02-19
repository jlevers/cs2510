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
    return this.moveActors()
      .spawn()
      .explodeBullets()
      .destroyShips() //removes ships that were in contact with any Bullet
      .removeOffscreen();
  }

  // Spawns between Ship.SPAWN_MIN and Ship.SPAWN_MAX Ships
  GameWorld spawn() {
    // TODO: only spawn on each second (not each tick)
    int toSpawn = this.rand.nextInt(Ship.SHIP_SPAWN_MIN) + Ship.SHIP_SPAWN_MAX;
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
            new Filter<IActor>(new IsOffscreen(this.WIDTH, this.HEIGHT));
    
    return new GameWorld(this.rand, this.bulletsLeft, this.shipsDown,
            this.ships.visit(filterOffscreen), this.bullets.visit(filterOffscreen));
  }
}

class IsOffscreen implements IPred<IActor> {
  int width;
  int height;
  
  IsOffscreen(int width, int height) {
    this.width = width;
    this.height = height;
  }

  // Checks if the given IActor is offscreen
  public boolean apply(IActor actor) {
    return actor.offscreen();
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
  
}