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
// check if things are touching



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
  ILo<Ship> ships;
  ILo<Bullet> bullets;

  // The constructor that can set every field to a specific value
  GameWorld(Random rand, int bulletsLeft, int shipsDown, ILo<Ship> ships, ILo<Bullet> bullets) {
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
  GameWorld onTick() {
    return this.moveActors
      .spawn()
      .explodeBullets()
      .destroyShips() //removes ships that were in contact with any Bullet
      .removeOffscreen();
  }

  // Spawns between Ship.SPAWN_MIN and Ship.SPAWN_MAX Ships
  GameWorld spawn() {
    int toSpawn = this.rand.nextInt(Ship.SHIP_SPAWN_MIN) + Ship.SHIP_SPAWN_MAX;
    ILo<Ship> newShips = new MtLo<>();
    for (int i = 0; i < toSpawn; i++) {

      boolean chooseSide = rand.nextBoolean();
      int velX = chooseSide ? Ship.SPEED : -1 * Ship.SPEED;  // True = left, false = right
      int spawnHeight = rand.nextInt(Ship.SPAWN_TOP - Ship.SPAWN_BOTTOM) + Ship.SPAWN_BOTTOM;
      int spawnWidth = chooseSide ? -1 * Ship.SIZE : GameWorld.WIDTH + Ship.SIZE;
      Ship newShip = new Ship(new Posn(velX, 0), new Posn(spawnWidth, spawnHeight));

      newShips = new ConsLo<>(newShip, newShips);
    }

    ILoDispF<Ship, ILo<Ship>> append = new Append<>(newShips);

    return new GameWorld(this.rand, this.bulletsLeft, this.shipsDown, append.call(this.ships),
            this.bullets);
  }
  
  //Moves all of the Actors in this Gameworld
  GameWorld moveActors() {
    ILoDispF<Ship,ILo<Ship>> moveShips = new Map<>(new MoveActor());
    ILoDispF<Bullet, ILo<Bullet>> moveBullets = new Map<>(new MoveActor());
    
    return new GameWorld(this.rand, this.bulletsLeft, this.shipsDown, 
                         this.ships.visit(moveShips), this.bullets.visit(moveBullets));
}
