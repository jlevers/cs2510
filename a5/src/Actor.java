import tester.*;
import javalib.funworld.*;
import java.awt.*;
import javalib.worldimages.Posn;

// Represents something that exists in the GameWorld
abstract class Actor {
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
   * this.move() ... GameWorld
   * this.spawn() ... GameWorld
   *
   * Methods of fields:
   */

  // Moves this Actor in the GameWorld
  abstract GameWorld move();
  // Spawns this actor in the GameWorld
  abstract GameWorld spawn();
}
