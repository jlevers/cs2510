import tester.*;
import java.awt.*;

import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;

interface IActor {
  // Moves this IActor based on its position and velocity
  IActor move();

  // Checks if this IActor is touching the given IActor
  boolean isTouching(IActor that);

  // Checks if this is touching the given Ship
  boolean isTouchingShip(Ship that);

  // Checks if this is touching the given Bullet
  boolean isTouchingBullet(Bullet that);

  // Checks if this IActor is offscreen
  boolean offscreen();

  // Allows a dispatched function to be processed on this IActor
  <R> R accept(IActorDispF<R> disp);

  // Explodes this IActor, if possible
  ILo<IActor> explode();

  // Generates a sub-bullet from this IActor (only applicable in the case of
  // Bullet, but needs to be
  // implemented in all IActors for dispatch reasons
  IActor genSubBullet(Posn p);
}

// Represents something that exists in the GameWorld
abstract class AActor implements IActor {
  Posn vel;
  Posn pos;
  int size;
  int speed;
  Color color;

  AActor(Posn vel, Posn pos, int size, int speed, Color color) {
    this.vel = vel;
    this.pos = pos;
    this.size = size;
    this.speed = speed;
    this.color = color;
  }

  // Moves this Actor in the GameWorld
  public abstract IActor move();

  // Checks if this Actor is touching the given Actor, taking into account their
  // sizes
  public abstract boolean isTouching(IActor that);

  // Checks if this AActor is touching the given Ship
  public boolean isTouchingShip(Ship that) {
    return false;
  }

  // Checks if this AActor is touching the given Bullet
  public boolean isTouchingBullet(Bullet that) {
    return false;
  }

  // Checks if this AActor is offscreen
  public boolean offscreen() {
    return this.pos.x - this.size > GameWorld.WIDTH || this.pos.x + this.size < 0
        || this.pos.y - this.size > GameWorld.HEIGHT || this.pos.y + this.size < 0;
  }

  // Accepts a dispatch
  public abstract <R> R accept(IActorDispF<R> disp);

  // Default explode method -- just returns a list of this
  public ILo<IActor> explode() {
    return new ConsLo<>(this, new MtLo<>());
  }

  public IActor genSubBullet(Posn p) {
    return new Bullet();
  }
}

class ExamplesIActors {
  Posn b1Loc = new Posn(0, 0);
  Posn b1Vel = new Posn(3, 4);

  Bullet b1 = new Bullet(this.b1Vel, this.b1Loc, 0);
  Bullet b1Moved = new Bullet(this.b1Vel, this.b1Vel, 0);
  Bullet rightoff = new Bullet(this.b1Vel, new Posn(503, 100), 0);
  Bullet leftoff = new Bullet(this.b1Vel, new Posn(-3, 100), 0);
  Bullet topoff = new Bullet(this.b1Vel, new Posn(100, 303), 0);
  Bullet bottomoff = new Bullet(this.b1Vel, new Posn(100, -3), 0);

  Posn s1Loc = new Posn(4, 6);
  Posn s1Vel = new Posn(10, 0);
  Posn s1MovedLoc = new Posn(14, 6);

  Ship s1 = new Ship(this.s1Vel, this.s1Loc);
  Ship s1Moved = new Ship(this.s1Vel, this.s1MovedLoc);
  Ship rightoffs = new Ship(this.b1Vel, new Posn(509, 100));
  Ship leftoffs = new Ship(this.b1Vel, new Posn(-9, 100));
  Ship topoffs = new Ship(this.b1Vel, new Posn(100, 309));
  Ship bottomoffs = new Ship(this.b1Vel, new Posn(100, -9));

  Posn origin = new Posn(0, 0);
  Posn vel = new Posn(2, 3);
  Bullet bullet = new Bullet(this.vel, this.origin, 1);
  Bullet otherPosBullet = new Bullet(new Posn(2, 2), this.origin, 2);

  Posn p1 = new Posn(5, 0);
  Posn p2 = new Posn(-5, 0);
  Bullet explode1 = new Bullet(this.p2, this.origin, 2);
  Bullet explode2 = new Bullet(this.p1, this.origin, 2);
  ILo<Bullet> exploded = new ConsLo<>(this.explode1, new ConsLo<>(this.explode2, new MtLo<>()));

  // Tests IActor.move()
  boolean testMove(Tester t) {
    return t.checkExpect(this.b1.move(), this.b1Moved)
        && t.checkExpect(this.s1.move(), this.s1Moved);
  }

  // Tests IActor.isTouching()
  boolean testIsTouching(Tester t) {
    return t.checkExpect(this.b1.isTouching(this.s1), true)
        && t.checkExpect(this.b1.isTouching(this.b1), false)
        && t.checkExpect(this.b1.isTouching(this.s1Moved), false)
        && t.checkExpect(this.s1.isTouching(this.b1), true)
        && t.checkExpect(this.s1.isTouching(this.s1), false)
        && t.checkExpect(this.s1Moved.isTouching(this.b1), false);
  }

  // Tests IActor.isTouchingBullet()
  boolean testIsTouchingBullet(Tester t) {
    return t.checkExpect(this.b1.isTouchingBullet(this.b1Moved), false)
        && t.checkExpect(this.s1Moved.isTouchingBullet(this.b1), false)
        && t.checkExpect(this.s1.isTouchingBullet(this.b1), true);
  }

  // Tests IActor.isTouchingShip()
  boolean testIsTouchingShip(Tester t) {
    return t.checkExpect(this.b1.isTouchingShip(this.s1), true)
        && t.checkExpect(this.b1.isTouchingShip(this.s1Moved), false)
        && t.checkExpect(this.s1.isTouchingShip(this.s1), false);
  }

  // Tests whether the given Actor is offscreen
  boolean testIsOffscreen(Tester t) {
    return t.checkExpect(this.rightoff.offscreen(), true)
        && t.checkExpect(this.leftoff.offscreen(), true)
        && t.checkExpect(this.topoff.offscreen(), true)
        && t.checkExpect(this.bottomoff.offscreen(), true)
        && t.checkExpect(this.rightoffs.offscreen(), true)
        && t.checkExpect(this.leftoffs.offscreen(), true)
        && t.checkExpect(this.topoffs.offscreen(), true)
        && t.checkExpect(this.bottomoffs.offscreen(), true)
        && t.checkExpect(this.b1.offscreen(), false) && t.checkExpect(this.s1.offscreen(), false);
  }

  // Tests Bullet.genSubBullet()
  boolean testGenSubBullet(Tester t) {
    return t.checkExpect(this.bullet.genSubBullet(new Posn(2, 2)), this.otherPosBullet)
        && t.checkExpect(this.s1.genSubBullet(new Posn(2, 2)), new Bullet());
  }

  // Tests Bullet.explode()
  boolean testExplode(Tester t) {
    return t.checkExpect(this.bullet.explode(), this.exploded)
        && t.checkExpect(this.s1.explode(), new ConsLo<>(this.s1, new MtLo<>()));
  }

  // Tests if an IActor accepts the given DispF
  boolean testAccept(Tester t) {
    return t.checkExpect(this.b1.accept(new DrawThat()),
        new CircleImage(this.b1.size, OutlineMode.SOLID, Bullet.COLOR))
        && t.checkExpect(this.s1.accept(new DrawThat()),
            new CircleImage(Ship.SIZE, OutlineMode.SOLID, Ship.COLOR));
  }
}
