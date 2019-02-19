import tester.*;
import javalib.funworld.*;
import java.awt.*;
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

  /*
   * Template:
   * Fields:
   * this.vel ... Posn
   * this.pos ... Posn
   * this.size ... int
   * this.speed ... int
   * this.color ... Color
   *
   * Methods:
   * this.move() ... IActor
   * this.isTouching(IActor) ... boolean
   * this.isTouchingShip(Ship) ... boolean
   * this.isTouchingBullet(Bullet) ... boolean
   * this.offscreen() ... boolean
   */

  // Moves this Actor in the GameWorld
  public abstract IActor move();

  // Checks if this Actor is touching the given Actor, taking into account their sizes
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
}

class ExamplesIActors {
  Posn b1Loc = new Posn(0, 0);
  Posn b1Vel = new Posn(3, 4);

  Bullet b1 = new Bullet(this.b1Vel, this.b1Loc, 0);
  Bullet b1Moved = new Bullet(this.b1Vel, this.b1Vel, 0);

  Posn s1Loc = new Posn(4, 6);
  Posn s1Vel = new Posn(10, 0);
  Posn s1MovedLoc = new Posn(14, 6);

  Ship s1 = new Ship(this.s1Vel, this.s1Loc);
  Ship s1Moved = new Ship(this.s1Vel, this.s1MovedLoc);

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
}
