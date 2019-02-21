import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;
import java.util.Random;

// Represents a Ship in the NBullets game
class Ship extends AActor {
  static final Color COLOR = Color.CYAN;
  static final int SIZE = 6;
  static final int SPEED = Bullet.SPEED / 2;  // px/tick
  static final int SHIP_SPAWN_MIN = 1;
  static final int SHIP_SPAWN_MAX = 3;
  static final int SHIP_SPEED = GameWorld.HEIGHT / 30;
  static final int SPAWN_TOP = GameWorld.HEIGHT - (GameWorld.HEIGHT / 7);
  static final int SPAWN_BOTTOM = GameWorld.HEIGHT / 7;

  public Ship(Posn vel, Posn pos) {
    super(vel, pos, Ship.SIZE, Ship.SPEED, Ship.COLOR);
  }

  // Moves this Ship to its new position based on its velocity
  public IActor move() {
    Posn newPos = new Posn(this.pos.x + this.vel.x, this.pos.y + this.vel.y);
    return new Ship(this.vel, newPos);
  }

  // Checks if this Ship is touching the given IActor
  public boolean isTouching(IActor that) {
    return that.isTouchingShip(this);
  }

  // Checks if this ship is touching the given Bullet
  public boolean isTouchingBullet(Bullet that) {
    int totalRads = this.size + that.size;
    return Utils.distance(this.pos, that.pos) <= totalRads;
  }

  // Dispatches a function for this Ship
  public <R> R accept(IActorDispF<R> disp) {
    return disp.forShip(this);
  }
}

// Represents a function passed to BuildList to build a list of ships
class BuildShip implements IFunc<Integer, IActor> {
  Random rand;

  BuildShip(Random rand) {
    this.rand = rand;
  }

  // Builds a ship
  public IActor call(Integer integer) {
    boolean chooseSide = rand.nextBoolean();
    int velX = chooseSide ? Ship.SPEED : -1 * Ship.SPEED;  // True = left, false = right
    int spawnHeight = rand.nextInt(Ship.SPAWN_TOP - Ship.SPAWN_BOTTOM) + Ship.SPAWN_BOTTOM;
    int spawnWidth = chooseSide ? -1 * Ship.SIZE : GameWorld.WIDTH + Ship.SIZE;
    return new Ship(new Posn(velX, 0), new Posn(spawnWidth, spawnHeight));
  }
}

// Tests Ship and BuildShip
class ExamplesShips {
  boolean testBuildShip(Tester t) {
    Random rand = new Random(1);
    BuildShip bs = new BuildShip(rand);
    return t.checkExpect(bs.call(1), new Ship(new Posn(2, 0), new Posn(-6, 70)));
  }
}