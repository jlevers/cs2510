import tester.*;

// a campus tour
class CampusTour {
  int startTime; // minutes from midnight
  ITourLocation startingLocation;

  CampusTour(int startTime, ITourLocation startingLocation) {
    this.startTime = startTime;
    this.startingLocation = startingLocation;
  }

  /*
   * Template
   * Fields:
   * this.startTime ... int
   * this.startingLocation ... ITourLocation
   *
   * Methods:
   * this.sameTour(CampusTour) ... boolean
   *
   * Methods of fields:
   * this.startingLocation.sameTourLocation(ITourLocation) ... boolean
   * this.startingLocation.sameTourEnd(TourEnd) ... boolean
   * this.startingLocation.sameMandatory(Mandatory) ... boolean
   * this.startingLocation.sameBranchingTour(BranchingTour) ... boolean
   */

  // is this tour the same tour as the given one?
  boolean sameTour(CampusTour other) {
    return this.startTime == other.startTime
            && this.startingLocation.sameTourLocation(other.startingLocation);
  }
}

// a spot on the tour
interface ITourLocation {
  // Checks if this tour location (and route) is the same as the given tour location
  boolean sameTourLocation(ITourLocation other);
  
  // Checks if this TourEnd is the same as the given ATourLocation
  boolean sameTourEnd(TourEnd other);

  // Checks if this Mandatory is the same as the given ATourLocation
  boolean sameMandatory(Mandatory other);
  
  // Checks if this BranchingTour is the same as the given ATourLocation
  boolean sameBranchingTour(BranchingTour other);
}

abstract class ATourLocation implements ITourLocation {
  String speech; // the speech to give at this spot on the tour

  ATourLocation(String speech) {
    this.speech = speech;
  }

  /*
   * Template:
   * Fields:
   * this.speech ... String
   *
   * Methods:
   * this.sameTourLocation(ITourLocation) ... boolean
   * this.sameTourEnd(TourEnd) ... boolean
   * this.sameMandatory(Mandatory) ... boolean
   * this.sameBranchingTour(BranchingTour) ... boolean
   *
   * Methods of fields:
   */

  // Checks if the tour locations of this and other are the same
  abstract public boolean sameTourLocation(ITourLocation other);
  
  // Checks if other is the same TourEnd as this
  public boolean sameTourEnd(TourEnd other) {
    return false;
  }
  
  // Checks if other is the same Mandatory as this
  public boolean sameMandatory(Mandatory other) {
    return false;
  }
  
  // Checks if other is the same BranchingTour as this
  public boolean sameBranchingTour(BranchingTour other) {
    return false;
  }
}

// the end of the tour
class TourEnd extends ATourLocation {
  ICampusLocation location;

  TourEnd(String speech, ICampusLocation location) {
    super(speech);
    this.location = location;
  }

  /*
   * Template
   * Fields:
   * this.location ... ICampusLocation
   *
   * Methods (see abstract class):
   *
   * Methods of fields:
   * this.location.sameCampusLocation(ICampusLocation) ... boolean
   * this.location.sameBuilding(Building) ... boolean
   * this.location.sameQuad(Quad) ... boolean
   */

  // Checks if this TourEnd is the same as the other ATourLocation
  public boolean sameTourLocation(ITourLocation other) {
    return other.sameTourEnd(this);
  }
  
  // Checks if this TourEnd is the same as the given ITourLocation
  public boolean sameTourEnd(TourEnd other) {
    return  (this.speech.equals(other.speech))
        && this.location.sameCampusLocation(other.location);
  }
}

//a mandatory spot on the tour with the next place to go
class Mandatory extends ATourLocation {
  ICampusLocation location;
  ITourLocation next;

  Mandatory(String speech, ICampusLocation location, ITourLocation next) {
    super(speech);
    this.location = location;
    this.next = next;
  }

  /*
   * Template
   * Fields:
   * this.location ... ICampusLocation
   * this.next ... ITourLocation
   *
   * Methods (see abstract class):
   *
   * Methods of fields:
   * this.location.sameCampusLocation(ICampusLocation) ... boolean
   * this.location.sameBuilding(Building) ... boolean
   * this.location.sameQuad(Quad) ... boolean
   * this.next.sameTourLocation(ITourLocation) ... boolean
   * this.next.sameTourEnd(ITourLocation) ... boolean
   * this.next.sameMandatory(ITourLocation) ... boolean
   * this.next.sameBranchingTour(ITourLocation) ... boolean
   */
  
  // Checks if this Mandatory location is the same as the given ATourLocation
  public boolean sameTourLocation(ITourLocation other) {
    return other.sameMandatory(this);
  }
  
  // Checks if this Mandatory location is the same as the given Mandatory
  public boolean sameMandatory(Mandatory other) {
    return (this.speech.equals(other.speech))
        && this.location.sameCampusLocation(other.location) 
        && this.next.sameTourLocation(other.next);
  }
}

// up to the tour guide where to go next
class BranchingTour extends ATourLocation {
  ITourLocation option1;
  ITourLocation option2;

  BranchingTour(String speech, ITourLocation option1, ITourLocation option2) {
    super(speech);
    this.option1 = option1;
    this.option2 = option2;
  }

  /*
   * Template
   * Fields:
   * this.option1 ... ITourLocation
   * this.option2 ... ITourLocation
   *
   * Methods (see abstract class):
   *
   * Methods of fields:
   * this.option1.sameTourLocation(ITourLocation) ... boolean
   * this.option1.sameTourEnd(ITourLocation) ... boolean
   * this.option1.sameMandatory(ITourLocation) ... boolean
   * this.option1.sameBranchingTour(ITourLocation) ... boolean
   * this.option2.sameTourLocation(ITourLocation) ... boolean
   * this.option2.sameTourEnd(ITourLocation) ... boolean
   * this.option2.sameMandatory(ITourLocation) ... boolean
   * this.option2.sameBranchingTour(ITourLocation) ... boolean
   */

  // Determines if the given location is the same as this Branching Tour
  public boolean sameTourLocation(ITourLocation other) {
    return other.sameBranchingTour(this);
  }
  
  // Determines if the given BranchingTour is the same as this
  public boolean sameBranchingTour(BranchingTour other) {
    return (this.speech.equals(other.speech))
        && (this.option1.sameTourLocation(other.option1) 
        || this.option1.sameTourLocation(other.option2)) 
        && (this.option2.sameTourLocation(other.option1) 
            || this.option2.sameTourLocation(other.option2));
  }
}

interface ICampusLocation {
  // Checks if the given ICampusLocation is the same as this
  boolean sameCampusLocation(ICampusLocation other);
  
  // Checks if the given Building is the same as this
  boolean sameBuilding(Building other);
  
  // Checks if the given Quad is the same as this
  boolean sameQuad(Quad other);
}

class Building implements ICampusLocation {
  String name;
  Address address;

  Building(String name, Address address) {
    this.name = name;
    this.address = address;
  }

  /*
   * Template:
   * Fields:
   * this.name ... String
   * this.address ... Address
   *
   * Methods:
   * this.sameCampusLocation(ICampusLocation) ... boolean
   * this.sameBuilding(Building) ... boolean
   * this.sameQuad(Quad) ... boolean
   *
   * Methods of fields:
   * this.address.sameAddress(Address) ... boolean
   */
  
  // Checks if the given ICampusLocation is the same as this
  public boolean sameCampusLocation(ICampusLocation other) {
    return other.sameBuilding(this);
  }
  
  // Checks if the given Building is the same as this
  public boolean sameBuilding(Building other) {
    return this.name.equals(other.name) && this.address.sameAddress(other.address);
  }
  
  // Checks if the given Quad is the same as this
  public boolean sameQuad(Quad other) {
    return false;
  }
}

class Address {
  String street;
  int number;

  Address(String street, int number) {
    this.number = number;
    this.street = street;
  }

  /*
   * Template
   * Fields:
   * this.street ... String
   * this.number ... int
   *
   * Methods:
   * this.sameAddress(Address) ... boolean
   *
   * Methods of fields:
   */
  
  // Checks if the given address is the same as this
  public boolean sameAddress(Address other) {
    return (this.street.equals(other.street)) && (this.number == other.number);
  }
}

class Quad implements ICampusLocation {
  String name;
  ILoCampusLocation surroundings; // in clockwise order, starting north

  Quad(String name, ILoCampusLocation surroundings) {
    this.name = name;
    this.surroundings = surroundings;
  }

  /*
   * Template:
   * Fields:
   * this.name ... String
   * this.surroundings ... ILoCampusLocation
   *
   * Methods:
   * this.sameCampusLocation(ICampusLocation) ... boolean
   * this.sameBuilding(Building) ... boolean
   * this.sameQuad(Quad) ... boolean
   *
   * Methods of fields:
   * this.surroundings.sameSurroundings(ILoCampusLocation) ... boolean
   * this.surroundings.sameMtSurroundings(MtLoCampusLocation) ... boolean
   * this.surroundings.sameConsSurroundings(ConsLoCampusLocation) ... boolean
   */
  
  // Checks if the given CampusLocation is the same as this
  public boolean sameCampusLocation(ICampusLocation other) {
    return other.sameQuad(this);
  }
  
  // Checks if the given Building is the same as this
  public boolean sameBuilding(Building other) {
    return false;
  }
  
  //Checks if the given Quad is the same as this Quad
  public boolean sameQuad(Quad other) {
    return this.name.equals(other.name) && this.surroundings.sameSurroundings(other.surroundings);
  }
}

interface ILoCampusLocation {
  // Checks if this ILoCampusLocation is the same as the other ILoCampusLocation
  boolean sameSurroundings(ILoCampusLocation other);
  
  // Checks if this ILoCampusLOcation is the same as the given MtLoCampusLocation
  boolean sameMtSurroundings(MtLoCampusLocation other);
  
  // Checks if this ILoCampusLocation is the same as the given ConsLoCampusLocation
  boolean sameConsSurroundings(ConsLoCampusLocation other);
}

class MtLoCampusLocation implements ILoCampusLocation {
  /*
   * Template
   * Fields:
   *
   * Methods:
   * this.sameSurroundings(ILoCampusLocation) ... boolean
   * this.sameMtSurroundings(MtLoCampusLocation) ... boolean
   * this.sameConsSurroundings(ConsLoCampusLocation) ... boolean
   *
   * Methods of fields:
   */

  // Checks if this MtLoCampusLocation is the same as the other ILoCampusLocation
  public boolean sameSurroundings(ILoCampusLocation other) {
    return other.sameMtSurroundings(this);
  }
  
  // Checks if this MtLoCampusLocation is the same as the other MtLoCampusLocation (always true)
  public boolean sameMtSurroundings(MtLoCampusLocation other) {
    return true;
  }
  
  // Checks if this MtLoCampusLocation is the same as the other ConsLoCampusLocation (always false)
  public boolean sameConsSurroundings(ConsLoCampusLocation other) {
    return false;
  }
}

class ConsLoCampusLocation implements ILoCampusLocation {
  ICampusLocation first;
  ILoCampusLocation rest;

  ConsLoCampusLocation(ICampusLocation first, ILoCampusLocation rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template
   * Fields:
   * this.first ... ICampusLocation
   * this.rest ... ILoCampusLocation
   *
   * Methods:
   * this.sameSurroundings(ILoCampusLocation) ... boolean
   * this.sameMtSurroundings(MtLoCampusLocation) ... boolean
   * this.sameConsSurroundings(ConsLoCampusLocation) ... boolean
   *
   * Methods of fields:
   * this.first.sameCampusLocation(ICampusLocation) ... boolean
   * this.first.sameBuilding(Building) ... boolean
   * this.first.sameQuad(Quad) ... boolean
   * this.rest.sameSurroundings(ILoCampusLocation) ... boolean
   * this.rest.sameMtSurroundings(MtLoCampusLocation) ... boolean
   * this.rest.sameConsSurroundings(ConsLoCampusLocation) ... boolean
   */
  
  // Checks if this ConsLoCampusLocation is the same as the other ILoCampusLocation
  public boolean sameSurroundings(ILoCampusLocation other) {
    return other.sameConsSurroundings(this);
  }
  
  // Checks if this ConsLoCampusLocation is the same as the other MtLoCampusLocation (always false)
  public boolean sameMtSurroundings(MtLoCampusLocation other) {
    return false;
  }
  
  // Checks if this ConsLoCampusLocation is the same as the other ConsLoCampusLocation
  public boolean sameConsSurroundings(ConsLoCampusLocation other) {
    return this.first.sameCampusLocation(other.first) && this.rest.sameSurroundings(other.rest);
  }
}

class ExamplesCampus {
  MtLoCampusLocation mt = new MtLoCampusLocation();

  Address snellAddress = new Address("Huntington Ave", 285);
  Building snell = new Building("Snell Library", this.snellAddress);
  Address shillmanAddress = new Address("Forsyth St", 115);
  Building shillman = new Building("Shillman Hall", this.shillmanAddress);
  Address stwestAddress = new Address("Forsyth St", 10);
  Building stwest = new Building("Stetson West", this.stwestAddress);
  Address speareAddress = new Address("Opera Pl", 5);
  Building speare = new Building("Speare Hall", this.speareAddress);
  
  ConsLoCampusLocation centennialLocs = new ConsLoCampusLocation(this.shillman,
      new ConsLoCampusLocation(this.snell, this.mt));
  Quad centennial = new Quad("Centennial", this.centennialLocs);
  
  ConsLoCampusLocation speareQuadLocs = new ConsLoCampusLocation(this.stwest,
      new ConsLoCampusLocation(this.speare, this.mt));
  Quad speareQuad = new Quad("Speare Quad", this.speareQuadLocs);
  
  Quad sadQuad = new Quad("Very Sad", this.mt);
  
  String speech1 = "This is a really inspiring speech about why you should go here!!!";
  String speech2 = "I didn't really sleep enough last night, so I'm a crappy tour guide...:(";
  String speech3 = "I'M SO STOKED AHHHHHHHHH!!!";
  
  TourEnd end1 = new TourEnd(this.speech1, this.snell);
  TourEnd end2 = new TourEnd(this.speech2, this.sadQuad);
  
  Mandatory mandatory1 = new Mandatory(this.speech3, this.stwest, this.end1);
  Mandatory mandatory2 = new Mandatory(this.speech1, this.shillman, this.end1);
  
  BranchingTour branching1 = new BranchingTour(this.speech1, this.mandatory1, this.end2);
  BranchingTour branching2 = new BranchingTour(this.speech1, this.end2, this.mandatory1);
  BranchingTour branching3 = new BranchingTour(this.speech3, this.end1, this.mandatory2);
  
  CampusTour tour1 = new CampusTour(120, this.branching1);
  CampusTour tour2 = new CampusTour(120, this.branching2);
  CampusTour totallyDifferentTour = new CampusTour(600, this.branching3);

  // Tests sameTour()
  boolean testSameTour(Tester t) {
    return t.checkExpect(this.tour1.sameTour(this.tour2), true)
            && t.checkExpect(this.tour1.sameTour(this.totallyDifferentTour), false);
  }

  // Tests testSameTourLocation()
  boolean testSameTourLocation(Tester t) {
    return t.checkExpect(this.end1.sameTourLocation(this.end1), true)
            && t.checkExpect(this.end1.sameTourLocation(this.end2), false)
            && t.checkExpect(this.mandatory1.sameTourLocation(this.mandatory1), true)
            && t.checkExpect(this.mandatory1.sameTourLocation(this.mandatory2), false)
            && t.checkExpect(this.branching1.sameTourLocation(this.branching1), true)
            && t.checkExpect(this.branching1.sameTourLocation(this.branching2), true)
            && t.checkExpect(this.branching2.sameTourLocation(this.branching3), false);
  }

  // Tests sameTourEnd()
  boolean testSameTourEnd(Tester t) {
    return t.checkExpect(this.end1.sameTourEnd(this.end1), true)
            && t.checkExpect(this.end2.sameTourEnd(this.end1), false)
            && t.checkExpect(this.mandatory2.sameTourEnd(this.end2), false)
            && t.checkExpect(this.branching1.sameTourEnd(this.end1), false);
  }

  // Tests sameMandatory()
  boolean testSameMandatory(Tester t) {
    return t.checkExpect(this.mandatory1.sameMandatory(this.mandatory1), true)
            && t.checkExpect(this.mandatory2.sameMandatory(this.mandatory1), false)
            && t.checkExpect(this.branching1.sameMandatory(this.mandatory1), false)
            && t.checkExpect(this.end1.sameMandatory(this.mandatory2), false);
  }

  // Tests sameBranchingTour()
  boolean testSameBranchingTour(Tester t) {
    return t.checkExpect(this.branching1.sameBranchingTour(this.branching1), true)
            && t.checkExpect(this.branching1.sameBranchingTour(this.branching2), true)
            && t.checkExpect(this.branching1.sameBranchingTour(this.branching3), false)
            && t.checkExpect(this.end2.sameBranchingTour(this.branching3), false)
            && t.checkExpect(this.mandatory1.sameBranchingTour(this.branching2), false);
  }

  // Tests sameCampusLocation()
  boolean testSameCampusLocation(Tester t) {
    return t.checkExpect(this.snell.sameCampusLocation(this.snell), true)
            && t.checkExpect(this.snell.sameCampusLocation(this.stwest), false)
            && t.checkExpect(this.snell.sameCampusLocation(this.centennial), false)
            && t.checkExpect(this.centennial.sameCampusLocation(this.centennial), true)
            && t.checkExpect(this.centennial.sameCampusLocation(this.sadQuad), false)
            && t.checkExpect(this.centennial.sameCampusLocation(this.speare), false);
  }

  // Tests sameBuilding()
  boolean testSameBuilding(Tester t) {
    return t.checkExpect(this.snell.sameBuilding(this.snell), true)
            && t.checkExpect(this.speare.sameBuilding(this.snell), false)
            && t.checkExpect(this.sadQuad.sameBuilding(this.snell), false);
  }

  // Tests sameQuad()
  boolean testSameQuad(Tester t) {
    return t.checkExpect(this.centennial.sameQuad(this.centennial), true)
            && t.checkExpect(this.centennial.sameQuad(this.speareQuad), false)
            && t.checkExpect(this.stwest.sameQuad(this.centennial), false);
  }

  // Tests testSameAddress()
  boolean testSameAddress(Tester t) {
    return t.checkExpect(this.stwestAddress.sameAddress(this.stwestAddress), true)
            && t.checkExpect(this.stwestAddress.sameAddress(this.speareAddress), false);
  }

  // Tests sameSurroundings()
  boolean testSameSurroundings(Tester t) {
    return t.checkExpect(this.speareQuadLocs.sameSurroundings(this.speareQuadLocs), true)
            && t.checkExpect(this.speareQuadLocs.sameSurroundings(this.centennialLocs), false)
            && t.checkExpect(this.mt.sameSurroundings(this.mt), true)
            && t.checkExpect(this.mt.sameSurroundings(this.speareQuadLocs), false);
  }

  // Tests sameMtSurroundings()
  boolean testSameMtSurroundings(Tester t) {
    return t.checkExpect(this.mt.sameMtSurroundings(this.mt), true)
            && t.checkExpect(this.speareQuadLocs.sameMtSurroundings(this.mt), false);
  }

  // Tests sameConsSurroundings()
  boolean testSameConsSurroundings(Tester t) {
    return t.checkExpect(this.speareQuadLocs.sameConsSurroundings(this.speareQuadLocs), true)
            && t.checkExpect(this.mt.sameConsSurroundings(this.centennialLocs), false);
  }
}
