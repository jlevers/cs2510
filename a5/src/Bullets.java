import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

// move
// shoot
// explode

class Bullet {
  static final Color color = Color.BLUE;
  static final int speed = 20; // px/s

  Posn vel;
  Posn posn;
  int explosionNum;
  int size;

  public Bullet(Posn vel, Posn posn, int explosionNum) {
    this.vel = vel;
    this.posn = posn;
    this.explosionNum = explosionNum;
    this.size = explosionNum * 20;
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

  // Creates a list of Bullets created when this Bullet explodes
  ILo<Bullet> explode() {
    if (this.explosionNum > 0) {
      Bullet temp = new Bullet(this.vel, this.posn, this.explosionNum - 1);

      return new ConsLo<Bullet>(new Bullet(this.vel, this.posn, this.explosionNum + 1),
              temp.explode());
    } else {
      return new MtLo<Bullet>();
    }

  }
}