import tester.*;
import javalib.funworld.*;

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


  static int shipsDown = 0;
  int bulletsLeft;

  GameWorld(int bulletsLeft) {
    this.bulletsLeft = bulletsLeft;
  }

  // Draws the current state of the GameWorld
  public WorldScene makeScene() {
    return new WorldScene(this.WIDTH, this.HEIGHT);
  }
}