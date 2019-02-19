import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

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
}