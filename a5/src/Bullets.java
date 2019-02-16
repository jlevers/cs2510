import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

class Bullet extends Actor {
  static final Color COLOR = Color.BLUE;
  static final int SPEED = 20; // px/s

  int explosionNum;

  public Bullet(Posn vel, Posn pos, int explosionNum) {
    super(vel, pos, explosionNum * 20, this.COLOR);
    this.explosionNum = explosionNum;
  }

  /*
   * Template:
   * Fields:
   * this.vel ... Posn
   * this.posn ... Posn
   * this.explosionNum ... int
   * this.size ... int
   *
   * Methods:
   * this.explode() ... ILo<Bullet>
   */

  @Override
  GameWorld move() {
    return null;
  }

  @Override
  GameWorld spawn() {
    return null;
  }

  // Creates a list of Bullets created when this Bullet explodes
  ILo<Bullet> explode() {
    if (this.explosionNum > 0) {
      Bullet temp = new Bullet(this.vel, this.pos, this.explosionNum - 1);

      return new ConsLo<Bullet>(new Bullet(this.vel, this.pos, this.explosionNum + 1),
              temp.explode());
    } else {
      return new MtLo<Bullet>();
    }

  }
}