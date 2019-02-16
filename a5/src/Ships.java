import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

// spawn
// move
// remove?


class Ship {
  static final Color color = Color.GREEN;
  static final int size = 6;
  static final int speed = 10;  // px/s

  Posn vel;
  Posn position;

  Ship(Posn vel, Posn position) {
    this.vel = vel;
    this.position = position;
  }
}