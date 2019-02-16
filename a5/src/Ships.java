import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

// spawn
// move
// remove?

class Ship extends Actor {
  static final Color COLOR = Color.GREEN;
  static final int SIZE = 6;
  static final int SPEED = 10;  // px/s

  public Ship(Posn vel, Posn pos) {
    super(vel, pos, this.SIZE, this.SPEED, this.COLOR);
  }

  GameWorld move() {
    return null;
  }

  GameWorld spawn() {
    return null;
  }
}