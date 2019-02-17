import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

class Bullet extends Actor {
  static final Color COLOR = Color.PINK;
  static final int SPEED = 8;  // px/tick
  static final int INIT_BULLET_RAD = 2;  // pixels
  static final int BULLET_RAD_INC = 2;  // pixels

  int explosionNum;

  public Bullet(Posn vel, Posn pos, int explosionNum) {
    super(vel, pos, Utils.calcRad(explosionNum), Bullet.SPEED, Bullet.COLOR);
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

class ExamplesBullets {
  Posn origin = new Posn(0, 0);
  Posn vel = new Posn(2, 3);
  Bullet bullet = new Bullet(this.vel, this.origin, 0);
}