import javalib.worldimages.Posn;
import tester.*;
import javalib.funworld.*;
import java.awt.*;

// Represents a Bullet in the NBullets game
class Bullet extends AActor {
  static final Color COLOR = Color.PINK;
  static final int SPEED = 5;  // px/tick
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
   * this.genSubBullet(Posn) ... Bullet
   */

  // Creates a list of Bullets resulting from the explosion of this Bullet
  public ILo<IActor> explode() {
    IFunc<Integer, ILo<Posn>> buildVelList = new BuildList<>(new BulletDir(this.explosionNum));
    Map<Posn, IActor> bulletMap = new Map<>(new BulletGen(this));

    // this.explosionNum + 1 so that the BuildList makes a list of the correct length
    ILo<Posn> bulletVels = buildVelList.call(this.explosionNum + 1);
    return bulletMap.call(bulletVels);

  }

  // Makes a sub-Bullet of this Bullet (for use in a Map)
  public IActor genSubBullet(Posn newVel) {
    return new Bullet(newVel, this.pos, this.explosionNum + 1);
  }

  // Moves this Bullet to a new location based on its current velocity and location
  public IActor move() {
    Posn newPos = new Posn(this.pos.x + this.vel.x, this.pos.y + this.vel.y);
    return new Bullet(this.vel, newPos, this.explosionNum);
  }

  // Checks if this Bullet is touching the given IActor
  public boolean isTouching(IActor that) {
    return that.isTouchingBullet(this);
  }

  // Checks if this Bullet is touching the given Ship
  public boolean isTouchingShip(Ship that) {
    int totalRads = this.size + that.size;
    return Utils.distance(this.pos, that.pos) <= totalRads;
  }

  // Accepts a dispatch for this Bullet
  public <R> R accept(IActorDispF<R> disp) {
    return disp.forBullet(this);
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
class BulletGen implements IFunc<Posn, IActor> {
  IActor bullet;

  BulletGen(IActor bullet) {
    this.bullet = bullet;
  }

  /*
   * Template:
   * Fields:
   * this.bullet ... IActor
   *
   * Methods:
   * this.call(Posn) ... IActor
   *
   * Methods of Fields:
   * this.bullet.explode() ... ILo<IActor>
   * this.bullet.genSubBullet(Posn) ... IActor
   */

  // Gets the new Bullet using the Bullet.genSubBullet method
  public IActor call(Posn posn) {
    return this.bullet.genSubBullet(posn);
  }
}

class ExamplesBullets {
  Posn origin = new Posn(0, 0);
  Posn vel = new Posn(2, 3);
  Bullet bullet = new Bullet(this.vel, this.origin, 1);
  Bullet otherPosBullet = new Bullet(new Posn(2, 2), this.origin, 2);

  Posn p1 = new Posn(5, 0);
  Posn p2 = new Posn(-5, 0);
  Bullet explode1 = new Bullet(this.p2, this.origin, 2);
  Bullet explode2 = new Bullet(this.p1, this.origin, 2);
  ILo<Bullet> exploded = new ConsLo<>(this.explode1, new ConsLo<>(this.explode2, new MtLo<>()));

  boolean testBulletDir(Tester t) {
    IFunc<Integer, Posn> bulletDir = new BulletDir(1);

    return t.checkExpect(bulletDir.call(0), this.p1)
            && t.checkExpect(bulletDir.call(1), this.p2);
  }

  boolean testBulletGen(Tester t) {
    IFunc<Posn, IActor> bulletGen = new BulletGen(this.bullet);

    return t.checkExpect(bulletGen.call(this.p2), this.explode1)
            && t.checkExpect(bulletGen.call(this.p1), this.explode2);
  }

  // Tests Bullet.genSubBullet()
  boolean testGenSubBullet(Tester t) {
    return t.checkExpect(this.bullet.genSubBullet(new Posn(2, 2)), this.otherPosBullet);
  }

  // Tests Bullet.explode()
  boolean testExplode(Tester t) {
    return t.checkExpect(this.bullet.explode(), this.exploded);
  }
}