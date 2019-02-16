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

  static final int WIDTH = 500;
  static final int HEIGHT = 500;

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