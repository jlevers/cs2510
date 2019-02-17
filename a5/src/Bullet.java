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

  // Creates a bullet from another bullet
  Bullet(Posn vel, Posn pos, int explosionNum) {
    super(vel, pos, Utils.calcRad(explosionNum), Bullet.SPEED, Bullet.COLOR);
    this.explosionNum = explosionNum;
  }

  // Creates a bullet that the player shot
  Bullet() {
    super(new Posn(0, Bullet.SPEED), new Posn(GameWorld.WIDTH / 2, 0), 1, Bullet.SPEED,
            Bullet.COLOR);
  }

  /*
   * Template:
   * Fields:
   * this.explosionNum ... int
   *
   * Methods:
   * this.explode() ... ILo<Bullet>
   */

  // Creates a list of Bullets resulting from the explosion of this Bullet
  ILo<Bullet> explode() {
    IFunc<Integer, ILo<Posn>> buildBulletList =
            new BuildList<Posn>(new BulletDir(this.explosionNum));
    ILoDispF<Posn, ILo<Bullet>> bulletMap = new Map<>(new BulletGen(this));

    ILo<Posn> bulletVels = buildBulletList.call(this.explosionNum);
    return bulletMap.call(bulletVels);

  }

  // Makes a sub-Bullet of this Bullet (for use in a Map)
  Bullet genSubBullet(Posn newVel) {
    return new Bullet(newVel, this.pos, this.explosionNum + 1);
  }
}

// Represents a function that generates a Posn indicating the vectorized direction of a Bullet
class BulletDir implements IFunc<Integer, Posn> {
  int explosionNum;

  BulletDir(int explosionNum) {
    this.explosionNum = explosionNum;
  }

  /*
   * Template:
   * Fields:
   * this.explosionNum ... int
   *
   * Methods:
   * this.call(Integer) ... Posn
   */

  // Gets the direction that a Bullet of a certain explosion number should go in degrees
  public Posn call(Integer bulletNum) {
    int bulletAngle = bulletNum * (360 / (this.explosionNum + 1));
    double bulletAngleRad = Math.toRadians(bulletAngle);

    Posn bulletDir = new Posn((int) (Bullet.SPEED * Math.cos(bulletAngleRad)),
            (int) (Bullet.SPEED * Math.sin(bulletAngleRad)));

    return bulletDir;
  }
}

// Represents a function that creates a Bullet given a velocity Posn
class BulletGen implements IFunc<Posn, Bullet> {
  Bullet bullet;

  BulletGen(Bullet bullet) {
    this.bullet = bullet;
  }

  /*
   * Template:
   * Fields:
   * this.bullet ... Bullet
   *
   * Methods:
   * this.call(Posn) ... Bullet
   */

  // Gets the new Bullet using the Bullet.genSubBullet method
  public Bullet call(Posn posn) {
    return this.bullet.genSubBullet(posn);
  }
}

class ExamplesBullets {
  Posn origin = new Posn(0, 0);
  Posn vel = new Posn(2, 3);
  Bullet bullet = new Bullet(this.vel, this.origin, 0);
}