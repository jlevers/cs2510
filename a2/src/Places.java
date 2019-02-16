import tester.*;

/*
 * Some of the Place methods double-count certain Features. totalCapacity(), foodinessRating(), and
 * restaurantInfo() all have this this issue. The reason for it is that when recursing into Places
 * reachable via ShuttleBus, some Places will have multiple routes to a single other Place, and
 * our implementation doesn't account for those multiple routes by making sure we only count the
 * Features at those locations once (instead of each time we recurse over that Place).
 */

class Place {
  String name;
  ILoFeature features;
  int totalRestaurants;

  Place(String name, ILoFeature features) {
    this.name = name;
    this.features = features;
  }

  /*
   * Template:
   * Fields: this.name ... String
   * this.features ... ILoFeature
   * 
   * Methods:
   * this.totalCapacity() ... int
   * this.foodinessRating() ... double
   * this.restaurantInfo() ... String
   * this.restaurantInfoHelper() ... String
   * this.totalRestaurants() ... int
   *
   * Methods of Fields:
   */

  // Returns total capacity of all Restaurants reachable from this Place
  int totalCapacity() {
    return this.features.capacity();
  }
  
  // Returns the total number of Restaurants reachable from this Place
  int totalRestaurants() {
    return this.features.totalRestaurants();
  }
  
  // Returns average rating of all Restaurants in list of features
  double foodinessRating() {
    int totalRestaurants = this.features.totalRestaurants();
    if (totalRestaurants > 0) {
      return this.features.totalRating() / this.totalRestaurants();
    }

    return 0.0;
  }

  
  // Returns a comma-separated list of all the Restaurants reachable from this place.
  // Calls the helper so that the substring doesn't happen on every recursive call.
  String restaurantInfo() {
    String info = this.restaurantInfoHelper();
    return info.length() > 0 ? info.substring(2) : info;
  }
 
  // Gets the restaurant info for all reachable Restaurants.
  String restaurantInfoHelper() {
    return this.features.restaurantInfo();
  }
  
  
}

interface ILoFeature {
  // Returns capacity of all reachable features in this ILoFeature
  int capacity();
  
  // Returns average rating of all IFeatures reachable from this ILoFeature
  double totalRating();
  
  // Returns number of Restaurants reachable from this ILoFeature
  int totalRestaurants();
  
  // Returns a comma-separated list of all the Restaurants reachable in the current list
  String restaurantInfo();
}

class MtLoFeature implements ILoFeature {
  /*
   * Template:
   * Fields:
   * 
   * Methods:
   * this.capacity() ... int
   * this.totalRating() ... double
   * this.totalRestaurants() ... int
   * this.restaurantInfo() ... String
   * 
   * Methods of fields:
   */
  
  // Returns capacity of this MtLoFeature (always 0)
  public int capacity() {
    return 0;
  }
  
  // Returns sum of ratings of all restaurants in this empty list (0)
  public double totalRating() {
    return 0;
  }
  
  // Returns total number of restaurants in this empty list (0)
  public int totalRestaurants() {
    return 0;
  }
  
  // Returns an empty string because there are no Restaurants in an empty list
  public String restaurantInfo() {
    return "";
  }
}

class ConsLoFeature implements ILoFeature {
  IFeature first;
  ILoFeature rest;

  ConsLoFeature(IFeature first, ILoFeature rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template:
   * Fields:
   * this.first ... IFeature
   * this.rest ... ILoFeature
   * 
   * Methods:
   * this.capacity() ... int
   * this.totalRating() ... double
   * this.totalRestaurants() ... int
   * this.restaurantInfo() ... String
   * 
   * Methods of fields:
   */

  // Returns the capacity of this list of features
  public int capacity() {
    return this.first.capacity() + this.rest.capacity();
  }
  
  public int totalRestaurants() {
    return this.first.isRestaurant() + this.rest.totalRestaurants();
  }
  
  // Returns average rating of all restaurants in this list
  public double totalRating() {
    return this.first.rating() + this.rest.totalRating();
  }
  
  // Returns a comma-separated list of all the Restaurants reachable in this list
  public String restaurantInfo() {
    return this.first.restaurantInfo() + this.rest.restaurantInfo();
  }
}

interface IFeature {
  // Returns capacity of this IFeature
  int capacity();
  
  // Returns rating of this IFeature
  double rating();
  
  // Returns 1 if this IFeature is a Restaurant, 0 otherwise
  int isRestaurant();
  
  // Returns the info of any reachable restaurants
  String restaurantInfo();
}

class Restaurant implements IFeature {
  String name;
  String type;
  double averageRating;

  Restaurant(String name, String type, double averageRating) {
    super();
    this.name = name;
    this.type = type;
    this.averageRating = averageRating;
  }

  /*
   * Template:
   * Fields:
   * this.name ... String
   * this.type ... String
   * this.averageRating ... double
   * 
   * Methods:
   * this.capacity() ... int
   * this.rating() ... double
   * this.isRestaurant() ... int
   * this.restaurantInfo() ... String
   * Methods of Fields:
   */

  // Returns capacity of this Restaurant (which doesn't have a capacity field)
  public int capacity() {
    return 0;
  }
  
  // Returns 1 because this is a  Restaurant
  public int isRestaurant() {
    return 1;
  }
  
  // Returns the rating of the Restaurant
  public double rating() {
    return this.averageRating;
  }
  
  // Returns this restaurants information
  public String restaurantInfo() {
    return ", " + this.name + " (" + this.type + ")";
  }
}

class Venue implements IFeature {
  String name;
  String type;
  int capacity;

  Venue(String name, String type, int capacity) {
    this.name = name;
    this.type = type;
    this.capacity = capacity;
  }

  /*
   * Template:
   * Fields:
   * this.name ... String
   * this.type ... String
   * this.capacity ... int
   * 
   * Methods:
   * this.capacity() ... int
   * this.rating() ... double
   * this.isRestaurant() ... int
   * this.restaurantInfo() ... String
   * 
   * Methods of Fields:
   */

  // Returns capacity of this Venue
  public int capacity() {
    return this.capacity;
  }
  
  // Returns 0 because this is not a Restaurant
  public int isRestaurant() {
    return 0;
  }
  
  // Returns the rating of the Venue (0)
  public double rating() {
    return 0.0;
  }
  
  // Returns an empty string because this is not a Restaurant
  public String restaurantInfo() {
    return "";
  }
}

class ShuttleBus implements IFeature {
  String name;
  Place destination;

  ShuttleBus(String name, Place destination) {
    this.name = name;
    this.destination = destination;
  }

  /*
   * Template:
   * Fields:
   * this.name ... String
   * this.destination ... Place
   * 
   * Methods:
   * this.capacity() ... int
   * this.rating() ... double
   * this.isRestaurant() ... int
   * this.restaurantInfo() ... String
   * Methods of Fields:
   */

  // Returns capacity of Places reachable from this ShuttleBus
  public int capacity() {
    return this.destination.totalCapacity();
  }
  
  // Returns the number of Restaurants reachable from this ShuttleBus
  // Attempted to call the totalRestaurants function recursively, but it only
  // exists in the Place class, not sure how to implement in IFeature
  public int isRestaurant() {
    return this.destination.totalRestaurants();
  }
  
  // Returns the ratings of the restaurants reachable by the ShuttleBus
  // Double counting the ratings of restaurants here
  public double rating() {
    return this.destination.foodinessRating() * this.isRestaurant();
  }
  
  // Returns the information of all Restaurants reachable from this ShuttleBus
  public String restaurantInfo() {
    return this.destination.restaurantInfoHelper();
  }
}

/*
 * There are three places: Boston, New York City, Toronto. In Boston, there is a
 * Restaurant named "Legal Seafoods" of type "Seafood" and an average rating of
 * 4.0. There is also a venue named "Hampshire House" of type "Wedding" and a capacity
 * of 500. There is a ShuttleBus "PeterPan" that goes to South Station. In New York
 * City, there is a Restaurant named "Le Bernardin", of type "French Seafood",
 * with a rating of 4.7. There is a ShuttleBus named "MegaBus", with the
 * destination Harvard. There is another ShuttleBus named "Greyhound", with
 * destination North End. In Toronto, there is a ShuttleBus named "FlixBus" with
 * destination CambridgeSide Galleria.
 */

class ExamplesPlaces {

  ILoFeature empty = new MtLoFeature();


  // North End
  IFeature dailyCatch = new Restaurant("The Daily Catch", "Sicilian", 4.4);
  IFeature tdGarden = new Venue("TD Garden", "stadium", 19580);
  ILoFeature northEndList = new ConsLoFeature(this.tdGarden,
      new ConsLoFeature(this.dailyCatch, this.empty));
  Place northEnd = new Place("North End", this.northEndList);

  // Harvard
  IFeature freshman15 = new ShuttleBus("Freshmen-15", this.northEnd);
  IFeature borderCafe = new Restaurant("Border Cafe", "Tex-Mex", 4.5);
  IFeature harvardStadium = new Venue("Harvard Stadium", "football", 30323);

  ILoFeature harvardList = new ConsLoFeature(this.freshman15, new ConsLoFeature(this.borderCafe,
      new ConsLoFeature(this.harvardStadium, this.empty)));
  Place harvard = new Place("Harvard", this.harvardList);
  
  // South Station
  IFeature littleItalyExpress = new ShuttleBus("Little Italy Express", this.northEnd);
  IFeature reginas = new Restaurant("Regina's Pizza", "pizza", 4.0);
  IFeature crimsonCruiser = new ShuttleBus("Crimson Cruiser", this.harvard);
  IFeature bostonCommon = new Venue("Boston Common", "public", 150000);

  ILoFeature southStationList = new ConsLoFeature(this.littleItalyExpress,
      new ConsLoFeature(this.reginas, new ConsLoFeature(this.crimsonCruiser,
          new ConsLoFeature(this.bostonCommon, this.empty))));
  Place southStation = new Place("South Station", this.southStationList);
  
  // CambridgeSide
  IFeature sarku = new Restaurant("Sarku Japan", "teriyaki", 3.9);
  IFeature starbucks = new Restaurant("Starbucks", "coffee", 4.1);
  IFeature bridgeShuttle = new ShuttleBus("bridge shuttle", this.southStation);

  ILoFeature csgList = new ConsLoFeature(this.sarku,
      new ConsLoFeature(this.starbucks, new ConsLoFeature(this.bridgeShuttle, this.empty)));
  Place cambridgeSide = new Place("CambridgeSide Galleria", this.csgList);


  // Boston
  IFeature legalSeaFoods = new Restaurant("Legal Sea Foods", "Seafood", 4.0);
  IFeature hampshireHouse = new Venue("Hampshire House","wedding", 500);
  IFeature peterPan = new ShuttleBus("Peter Pan", this.harvard);

  ILoFeature bostonList = new ConsLoFeature(this.tdGarden,
          new ConsLoFeature(this.legalSeaFoods, new ConsLoFeature(this.peterPan, this.empty)));
  Place boston = new Place("Boston", this.bostonList);

  // New York City
  IFeature leBernardin = new Restaurant("Le Bernardin", "French Seafood", 4.7);
  IFeature megabus = new ShuttleBus("Megabus", this.harvard);
  IFeature greyhound = new ShuttleBus("Greyhound", this.northEnd);

  ILoFeature nycList = new ConsLoFeature(this.greyhound,
          new ConsLoFeature(this.megabus, new ConsLoFeature(this.leBernardin, this.empty)));
  Place newYorkCity = new Place("New York City", this.nycList);

  // Toronto
  IFeature flixbus = new ShuttleBus("FlixBus", this.southStation);

  ILoFeature torontoList = new ConsLoFeature(this.flixbus, this.empty);
  Place toronto = new Place("Toronto", this.torontoList);

  
  boolean testTotalCapacityPlace(Tester t) {
    return t.checkExpect(this.harvard.totalCapacity(), 49903);
  }

  boolean testCapacityILoFeature(Tester t) {
    return t.checkExpect(this.empty.capacity(), 0)
        && t.checkExpect(this.csgList.capacity(), 219483)
        && t.checkExpect(this.harvardList.capacity(), 49903);
  }
  
  boolean testCapacityIFeature(Tester t) {
    return t.checkExpect(this.freshman15.capacity(), 19580)
        && t.checkExpect(this.tdGarden.capacity(), 19580)
        && t.checkExpect(this.borderCafe.capacity(), 0);
  }
  
  boolean testFoodinessRating(Tester t) {
    return t.checkExpect(this.harvard.foodinessRating(), 4.45)
        && t.checkInexact(this.cambridgeSide.foodinessRating(), 4.22, .01)
        && t.checkExpect(this.toronto.foodinessRating(), 4.325);
  }

  boolean testTotalRestaurants(Tester t) {
    return t.checkExpect(this.bostonList.totalRestaurants(), 3)
        && t.checkExpect(this.torontoList.totalRestaurants(), 4);
  }

  boolean testTotalRating(Tester t) {
    return t.checkExpect(this.bostonList.totalRating(), 12.9)
        && t.checkExpect(this.harvardList.totalRating(), 8.9);
  }

  boolean testRating(Tester t) {
    return t.checkExpect(this.borderCafe.rating(), 4.5)
        && t.checkExpect(this.crimsonCruiser.rating(), 8.9)
        && t.checkExpect(this.tdGarden.rating(), 0.0);
  }

  boolean testIsRestaurant(Tester t) {
    return t.checkExpect(this.borderCafe.isRestaurant(), 1)
        && t.checkExpect(this.bridgeShuttle.isRestaurant(), 4)  // Double counting
        && t.checkExpect(this.tdGarden.isRestaurant(), 0);
  }

  boolean testRestaurantInfo(Tester t) {
    return t.checkExpect(this.cambridgeSide.restaurantInfo(),
        "Sarku Japan (teriyaki), Starbucks (coffee), The Daily Catch (Sicilian), "
        + "Regina's Pizza (pizza), The Daily Catch (Sicilian), Border Cafe (Tex-Mex)");
  }

  boolean testRestaurantInfoILoFeatures(Tester t) {
    return t.checkExpect(this.northEndList.restaurantInfo(), ", The Daily Catch (Sicilian)")
        && t.checkExpect(this.csgList.restaurantInfo(),
            ", Sarku Japan (teriyaki), Starbucks (coffee), The Daily Catch (Sicilian), "
            + "Regina's Pizza (pizza), The Daily Catch (Sicilian), Border Cafe (Tex-Mex)");
  }

  boolean testRestaurantInfoFeatures(Tester t) {
    return t.checkExpect(this.leBernardin.restaurantInfo(), ", Le Bernardin (French Seafood)")
        && t.checkExpect(this.tdGarden.restaurantInfo(), "")
        && t.checkExpect(this.freshman15.restaurantInfo(), ", The Daily Catch (Sicilian)");
  }
}


