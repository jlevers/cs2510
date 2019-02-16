import tester.*;

class Direction {
  String description;
  int miles;

  Direction(String description, int miles) {
    this.description = description;
    this.miles = miles;
  }

  // Makes a longer direction into a shorter one that instructs the drivers to switch
  Direction splitDirection(String nextDriver, int switchOff, int drivenSoFar) {
    return new Direction("Switch with " + nextDriver, switchOff - drivenSoFar);
  }

  // Gets the remaining part of the direction after splitting it up
  Direction remainingDirection(int switchOff, int drivenSoFar) {
    return new Direction(this.description, this.miles - (switchOff - drivenSoFar));
  }

  // Checks if this direction is long enough that the drivers must switch, given a switch distance
  boolean needToSwitch(int switchOff, int drivenSoFar) {
    return this.miles + drivenSoFar > switchOff;
  }

  // How long is this direction?
  int length() {
    return this.miles;
  }
}

interface ILoDirection {
  // Splits up the directions into chunks of the given size, with the drivers in the given order
  ILoRoadTripChunk splitUpDirections(int switchOff, String driver1, String driver2);

  // Splits up the directions into chunks of the given size, with the drivers in the given order
  // Accumulator: soFar, a list of directions in this chunk
  ILoRoadTripChunk splitUpDirectionsAcc(int switchOff, String driver1, String driver2,
                                        ILoDirection soFar);

  // Finds the mileage total of this list of directions
  int length();

  // Appends the given Direction to the base list of Directions
  ILoDirection append(ILoDirection base);
}

class MtLoDirection implements ILoDirection {
  // Splits up an empty list of directions into a list of RoadTripChunks (which is always empty)
  public ILoRoadTripChunk splitUpDirections(int switchOff, String driver1, String driver2) {
    return new MtLoRoadTripChunk();
  }

  // For an empty list, the RoadTripChunk should just end
  public ILoRoadTripChunk splitUpDirectionsAcc(int switchOff, String driver1, String driver2, ILoDirection soFar) {
    return new ConsLoRoadTripChunk(new RoadTripChunk(driver1, soFar), new MtLoRoadTripChunk());
  }

  // Gets the length of this empty list of directions (always 0)
  public int length() {
    return 0;
  }

  // Append to empty list of directions
  public ILoDirection append(ILoDirection base) {
    return base;
  }
}

class ConsLoDirection implements ILoDirection {
  Direction first;
  ILoDirection rest;

  ConsLoDirection(Direction first, ILoDirection rest) {
    this.first = first;
    this.rest = rest;
  }

  // Splits up this list of Directions into a list of RoadTripChunks based on the switchOff distance
  public ILoRoadTripChunk splitUpDirections(int switchOff, String driver1, String driver2) {
    return this.splitUpDirectionsAcc(switchOff, driver1, driver2, new MtLoDirection());
  }

  // Splits up this list of Directions into a list of RoadTripChunks, accumulating the directions in
  // a chunk until the current drive has driven the switchOff distance
  public ILoRoadTripChunk splitUpDirectionsAcc(int switchOff, String current, String next,
                                     ILoDirection soFar) {

    // Gets the number of miles in the accumulated directions
    int milesSoFar = soFar.length();

    // If the next direction is longer than the allowed mileage per driver:
    if (this.first.needToSwitch(switchOff, milesSoFar)) {

      // This is the piece of the next direction needed to get this chunk's mileage to be as long
      // as switchOff
      Direction subDirection = this.first.splitDirection(next, switchOff, milesSoFar);
      // Add the two new directions above to the list of accumulated directions
      ILoDirection updatedDirs = new ConsLoDirection(subDirection, soFar);
      // Create a chunk with the accumulated list of directions
      RoadTripChunk chunk = new RoadTripChunk(current, updatedDirs);

      // Update the first direction in the rest of this list of directions with the length of the
      // partial direction (before the most recent driver switch) subtracted from its distance
      ILoDirection newRest = new ConsLoDirection(this.first.remainingDirection(switchOff,
              milesSoFar), this.rest);

      return new ConsLoRoadTripChunk(chunk, newRest.splitUpDirections(switchOff, next, current));
    } else {  // If the next direction won't go over the mileage limit for the current driver

      // Add the next direction to the accumulated list of directions
      ILoDirection newLoDirections = soFar.append(new ConsLoDirection(this.first,
              new MtLoDirection()));
      return this.rest.splitUpDirectionsAcc(switchOff, current, next, newLoDirections);
    }
  }

  // Gets the total mileage of this list of directions
  public int length() {
    return this.first.length() + this.rest.length();
  }

  // Appends this list to the base list given
  public ILoDirection append(ILoDirection base) {
    return new ConsLoDirection(this.first, this.rest.append(base));
  }
}


class RoadTrip {
  String driver1;
  String driver2;
  ILoDirection directions;

  RoadTrip(String driver1, String driver2, ILoDirection directions) {
    this.driver1 = driver1;
    this.driver2 = driver2;
    this.directions = directions;
  }

  // Splits up this road trip into RoadTripChunks, each of which is `switchOff` miles long
  ILoRoadTripChunk splitUpTrip(int switchOff) {
    return this.directions.splitUpDirections(switchOff, this.driver1, this.driver2);
  }
}

class RoadTripChunk {
  String driver;
  ILoDirection directions;

  RoadTripChunk(String driver, ILoDirection directions) {
    this.driver = driver;
    this.directions = directions;
  }
}

interface ILoRoadTripChunk {}

class MtLoRoadTripChunk implements ILoRoadTripChunk {}

class ConsLoRoadTripChunk implements ILoRoadTripChunk {
  RoadTripChunk first;
  ILoRoadTripChunk rest;

  ConsLoRoadTripChunk(RoadTripChunk first, ILoRoadTripChunk rest) {
    this.first = first;
    this.rest = rest;
  }
}


class ExamplesRoadTrip {
  ILoDirection mt = new MtLoDirection();
  ILoRoadTripChunk mtRoadTripChunk = new MtLoRoadTripChunk();

  ILoDirection dirs = new ConsLoDirection(new Direction("Make a left at Alberquerque", 13),
          new ConsLoDirection(new Direction("Make a right at the fork", 2),
                  new ConsLoDirection(new Direction("Make a left at the next fork", 3),
                          new ConsLoDirection(new Direction("Take the overpass", 45),
                                  new ConsLoDirection(new Direction("Destination on left", 12),
                                          this.mt)))));
  RoadTrip roadTrip = new RoadTrip("Hazel", "Henry", this.dirs);

  ILoDirection chunkDirs1 = new ConsLoDirection(new Direction("Make a left at Alberquerque", 13),
          new ConsLoDirection(new Direction("Make a right at the fork", 2),
                  new ConsLoDirection(new Direction("Switch with Henry", 0), this.mt)));
  RoadTripChunk chunk1 = new RoadTripChunk("Hazel", this.chunkDirs1);
  ILoDirection chunkDirs2 = new ConsLoDirection(new Direction("Make a left at the next fork", 3),
          new ConsLoDirection(new Direction("Switch with Hazel", 0), this.mt));
  ILoDirection chunkDirs3 = new ConsLoDirection(new Direction("Switch with Henry", 15), this.mt);
  ILoDirection chunkDirs4 = new ConsLoDirection(new Direction("Switch with Hazel", 15), this.mt);
  ILoDirection chunkDirs5 = new ConsLoDirection(new Direction("Take the overpass", 45),
                                  new ConsLoDirection(new Direction("Destination on left", 12), this.mt));

  ILoRoadTripChunk chunkedRoadTrip = new ConsLoRoadTripChunk(new RoadTripChunk("Hazel",
          this.chunkDirs1),
            new ConsLoRoadTripChunk(new RoadTripChunk("Henry", this.chunkDirs2),
                  new ConsLoRoadTripChunk(new RoadTripChunk("Hazel", this.chunkDirs3),
                          new ConsLoRoadTripChunk(new RoadTripChunk("Henry", this.chunkDirs3),
                                  new ConsLoRoadTripChunk(new RoadTripChunk("Hazel",
                                          this.chunkDirs5), this.mtRoadTripChunk)))));




  ILoDirection simpleDirs = new ConsLoDirection(new Direction("Go straight", 25),
          new ConsLoDirection(new Direction("Turn left at the stop sign", 30), this.mt));
  RoadTrip simpleRoadTrip = new RoadTrip("Jesse", "Claudia", this.simpleDirs);

  RoadTripChunk simpleChunk1 = new RoadTripChunk("Jesse",
          new ConsLoDirection(new Direction("Switch with Claudia", 15), this.mt));

  RoadTripChunk simpleChunk2 = new RoadTripChunk("Claudia",
          new ConsLoDirection(new Direction("Go straight", 10),
                  new ConsLoDirection(new Direction("Switch with Jesse", 5), this.mt)));

  RoadTripChunk simpleChunk3 = new RoadTripChunk("Jesse",
          new ConsLoDirection(new Direction("Switch with Claudia", 15), this.mt));

  RoadTripChunk simpleChunk4 = new RoadTripChunk("Claudia",
          new ConsLoDirection(new Direction("Turn left at the stop sign", 10), this.mt));

  ILoRoadTripChunk simpleRoadTripChunkList = new ConsLoRoadTripChunk(this.simpleChunk1,
          new ConsLoRoadTripChunk(this.simpleChunk2,
                  new ConsLoRoadTripChunk(this.simpleChunk3,
                          new ConsLoRoadTripChunk(this.simpleChunk4, this.mtRoadTripChunk))));

  boolean testSplitUpTrip(Tester t) {
    // return t.checkExpect(this.roadTrip.splitUpTrip(15), this.chunkedRoadTrip);
    return t.checkExpect(this.simpleRoadTrip.splitUpTrip(15), this.simpleRoadTripChunkList);
  }
}