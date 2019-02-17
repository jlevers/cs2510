import tester.*;
import javalib.funworld.*;
import java.awt.*;
import javalib.worldimages.Posn;

// Represents something that exists in the GameWorld
class Actor {
  Posn vel;
  Posn pos;
  int size;
  int speed;
  Color color;

  Actor(Posn vel, Posn pos, int size, int speed, Color color) {
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
   * this.move() ... Actor
   * this.touching(Actor) ... boolean
   *
   * Methods of fields:
   */

  // Moves this Actor in the GameWorld
  Actor move() {
    Posn newPos = new Posn(this.pos.x + this.vel.x, this.pos.y + this.vel.y);
    return new Actor(this.vel, newPos, this.size, this.speed, this.color);
  }

  // Checks if this Actor is touching the given Actor, taking into account their sizes
  boolean touching(Actor that) {
    int totalRads = this.size + that.size;
    if (Utils.distance(this.pos, that.pos) <= totalRads) {
      return true;
    }
    return false;
  }
}
