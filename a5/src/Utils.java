import javalib.worldimages.Posn;
import tester.Tester;

// This class contains various utility methods to be used with the NBullets game
class Utils {
  /*
   * Template:
   * Fields:
   *
   * Methods:
   * this.calcRad(int) ... int
   * this.distance(Posn, Posn) ... boolean
   *
   * Methods of fields:
   */

  // Calculates the radius for a Bullet
  static int calcRad(int explosionNum) {
    int rad = Bullet.INIT_BULLET_RAD + (Bullet.BULLET_RAD_INC * explosionNum);
    return rad >= 10 ? 10 : rad;
  }

  // Gets the distance between two Posns
  static double distance(Posn a, Posn b) {
    return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
  }
 
  //Creates a string displaying the bullets left and ships down
  static String counterText(int bulletsLeft, int shipsDown) {
    return "Bullets Left: " + bulletsLeft  + "; Ships Down: " + shipsDown;
  }
  
}

class ExamplesUtils {
  boolean testCalcRad(Tester t) {
    return t.checkExpect(Utils.calcRad(3), 8)
            && t.checkExpect(Utils.calcRad(7), 10);
  }

  boolean testDistance(Tester t) {
    Posn a = new Posn(0, 0);
    Posn b = new Posn(3, 4);
    Posn c = new Posn(1, 2);

    return t.checkExpect(Utils.distance(a, b), 5.0)
            && t.checkInexact(Utils.distance(b, c), Math.sqrt(8), 0.001);
  }
  
  boolean testCounterTest(Tester t) {
    return t.checkExpect(Utils.counterText(4,5), 
        "Bullets Left: 4; Ships Down: 5");
  }
}
