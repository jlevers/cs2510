interface ISundae {}

class Scoop implements ISundae {
  String flavor;

  public Scoop(String flavor) {
    this.flavor = flavor;
  }
}

class Topping implements ISundae {
  ISundae inner;
  String name;

  public Topping(ISundae inner, String name) {
    this.inner = inner;
    this.name = name;
  }
}

class ExamplesSundae {
  ISundae chocolate = new Scoop("chocolate");
  ISundae vanilla = new Scoop("vanilla");

  ISundae rainbowSundae = new Topping(this.chocolate, "rainbow sprinkles");
  ISundae caramelSundae = new Topping(this.rainbowSundae, "caramel");
  ISundae yummy = new Topping(this.caramelSundae, "whipped cream");

  ISundae chocolateSprinkles = new Topping(this.vanilla, "chocolate sprinkles");
  ISundae fudge = new Topping(this.chocolateSprinkles, "fudge");
  ISundae noThankYou = new Topping(this.fudge, "plum sauce");
}