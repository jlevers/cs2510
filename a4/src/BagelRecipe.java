import tester.*;

// Represents a perfectly proportioned recipe for making bagels (in ounces)
class BagelRecipe {
  double flour;
  double water;
  double yeast;
  double salt;
  double malt;

  // Creates a BagelRecipe with measurements in ounces, enforcing that the amounts of flour and
  // water are equal, the amounts of yeast and malt are equal, and that the total weight of the
  // yeast and salt are 1/20th the weight of the water.
  BagelRecipe(double flour, double water, double yeast, double salt, double malt) {
    if (flour < 0 || water < 0 || yeast < 0 || salt < 0 || malt < 0) {
      throw new IllegalArgumentException("All arguments must be greater than 0.");
    } else if (Math.abs(flour - water) > 0.001) {
      throw new IllegalArgumentException("The amount of flour must be equal to the"
              + " amount of water.");
    } else if (Math.abs(yeast - malt) > 0.001) {
      throw new IllegalArgumentException("The amount of yeast must be equal to the "
              + "amount of malt.");
    } else if (Math.abs((salt + yeast) * 20 - flour) > 0.001) {
      throw new IllegalArgumentException("The combined weight of the salt and yeast must be equal"
              + " to 1/20th the weight of the flour.");
    }

    this.flour = flour;
    this.water = water;
    this.yeast = yeast;
    this.salt = salt;
    this.malt = malt;
  }

  // Creates a perfectly proportioned BagelRecipe given only the amounts of flour and yeast
  BagelRecipe(double flour, double yeast) {
    this(flour, flour, yeast, (flour / 20) - yeast, yeast);
  }

  // Creates a BagelRecipe given flour (cups), yeast (tsp), and salt (tsp) as volumes rather than
  // weights
  BagelRecipe(double flour, double yeast, double salt) {
    this(flour * 4.25, flour * 4.25, (yeast / 48) * 5, (salt / 48) * 10, (yeast / 48) * 5);
  }

  /*
   * Template:
   * Fields:
   * this.flour ... double
   * this.water ... double
   * this.yeast ... double
   * this.salt ... double
   * this.malt ... double
   *
   * Methods:
   * this.sameRecipe(BagelRecipe) ... boolean
   * this.compDouble(double, double) ... boolean
   *
   * Methods of fields:
   */

  // Checks if this BagelRecipe is the same as the given BagelRecipe
  boolean sameRecipe(BagelRecipe other) {
    return this.compDouble(this.flour, other.flour)
            && this.compDouble(this.water, other.water)
            && this.compDouble(this.yeast, other.yeast)
            && this.compDouble(this.malt, other.malt)
            && this.compDouble(this.salt, other.salt);
  }

  // Compares two doubles and checks if the difference between them is less than 0.001
  boolean compDouble(double fst, double snd) {
    return Math.abs(fst - snd) < 0.001;
  }
}

// Tests the BagelRecipe class above
class ExamplesBagelRecipe {
  BagelRecipe normal = new BagelRecipe(10.0, 10.0, 0.25, 0.25, 0.25);
  BagelRecipe theSame = new BagelRecipe(10.0, 10.0, 0.25, 0.25, 0.25);
  BagelRecipe volume = new BagelRecipe(2.0, 2.04, 1.02);

  IllegalArgumentException flourWater = new IllegalArgumentException("The amount of flour must be"
          + " equal to the amount of water.");
  IllegalArgumentException yeastMalt = new IllegalArgumentException("The amount of yeast must"
          + " be equal to the amount of malt.");
  IllegalArgumentException saltYeastFlour = new IllegalArgumentException("The combined weight of"
          + " the salt and yeast must be equal to 1/20th the weight of the flour.");
  IllegalArgumentException posIngredients = new IllegalArgumentException("All arguments must be "
          + "greater than 0.");


  boolean testConstructor1(Tester t) {
    return t.checkConstructorException(this.flourWater, "BagelRecipe", 10.0, 20.0, 5.0, 6.2, 4.3)
            && t.checkConstructorException(this.yeastMalt, "BagelRecipe",
                  10.0, 10.0, 0.25, 0.25, 0.35)
            && t.checkConstructorException(this.saltYeastFlour, "BagelRecipe", 10.0, 10.0, 0.25,
                  0.35, 0.25)
            && t.checkConstructorException(this.posIngredients, "BagelRecipe", 10.0, 10.0, 0.25,
                  -0.1, 0.25);
  }

  boolean testConstructor2(Tester t) {
    return t.checkConstructorException(this.posIngredients, "BagelRecipe", 10.0, 2.0);
  }

  boolean testConstructor3(Tester t) {
    return t.checkConstructorException(this.posIngredients, "BagelRecipe", 2.0, 171.0, -35.0);
  }

  boolean testSameRecipe(Tester t) {
    return t.checkExpect(this.normal.sameRecipe(this.theSame), true)
            && t.checkExpect(this.normal.sameRecipe(this.volume), false);
  }
}