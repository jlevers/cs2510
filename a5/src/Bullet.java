import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import tester.*;
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
    this(new Posn(0, -1 * Bullet.SPEED), new Posn(GameWorld.WIDTH / 2, GameWorld.HEIGHT), 1);
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
  Posn p1 = new Posn(5, 0);
  Posn p2 = new Posn(-5, 0);
  Bullet explode1 = new Bullet(this.p2, this.origin, 2);
  Bullet explode2 = new Bullet(this.p1, this.origin, 2);
  ILo<IActor> explodedList = new ConsLo<>(new Bullet(this.p2, this.origin, 2),
      new ConsLo<>(new Bullet(this.p1, this.origin, 2), new MtLo<>()));
  
  //Tests if BulletDir returns the appropriate Posn representing an exploded velocity
  boolean testBulletDir(Tester t) {
    IFunc<Integer, Posn> bulletDir = new BulletDir(1);

    return t.checkExpect(bulletDir.call(0), this.p1)
            && t.checkExpect(bulletDir.call(1), this.p2);
  }

  //Tests if BulletGen creates appropriate bullet given a velocity Posn
  boolean testBulletGen(Tester t) {
    IFunc<Posn, IActor> bulletGen = new BulletGen(this.bullet);

    return t.checkExpect(bulletGen.call(this.p2), this.explode1)
            && t.checkExpect(bulletGen.call(this.p1), this.explode2);
  }
  
  //Tests the accept
  boolean testAccept(Tester t) {
    return (t.checkExpect(this.bullet.accept(new DrawThat()), 
        new CircleImage(this.bullet.size, OutlineMode.SOLID, Bullet.COLOR)));
  }
  
  IActor ship1 = new Ship(new Posn(0,1), new Posn (0,6));
  IActor ship2 = new Ship(new Posn(0,1), new Posn (10, 10));
  Ship ship3 = new Ship(new Posn(0,1), new Posn (0,6));
  Ship ship4 = new Ship(new Posn(0,1), new Posn (10, 10));
  
  //Tests if the Bullet is touching the given IActor
  boolean testIsTouching(Tester t) {
    return t.checkExpect(this.bullet.isTouching(this.bullet), false)
        && t.checkExpect(this.bullet.isTouching(this.ship1), true)
        && t.checkExpect(this.bullet.isTouching(this.ship2), false);
        
  }
  
  //Tests if the Bullet is touching the given Ship
  boolean testIsTouchingShip(Tester t) {
    return t.checkExpect(this.bullet.isTouchingShip(this.ship3), true)
        && t.checkExpect(this.bullet.isTouchingShip(this.ship4), false); 
  }
  
  //Tests if the Bullet moves to the appropriate location
  boolean testMove(Tester t) {
    return t.checkExpect(this.bullet.move(), new Bullet(this.vel, this.vel, 1));
  }
  
  //Tests if the correct bullet is generated
  boolean testGenSubBullet(Tester t) {
    return t.checkExpect(this.bullet.genSubBullet(this.p1),
        new Bullet(this.p1, this.bullet.pos, 2));
  }
  
  //Tests if the bullet explodes into an appropriate list
  boolean testExplode(Tester t) {
    return t.checkExpect(this.bullet.explode(), this.explodedList);
  }
}