import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

// spawn
// move
// remove?

class Ship extends Actor {
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

  Ship spawn() {

  }
}