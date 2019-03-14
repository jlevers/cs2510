// represents a Person with a user name and a list of buddies
class Person {

  String username;
  ILoBuddy buddies;
  double diction;
  double hearing;

  Person(String username) {
    this(username, 0.0, 0.0);
  }

  //Constructor when given a Person's diction and hearing scores
  Person(String username, double diction, double hearing) {
    if (diction < 0 || diction > 1) {
      throw new IllegalArgumentException("Diction score must be between 0 and 1");
    }
    if (hearing < 0 || hearing > 1) {
      throw new IllegalArgumentException("Hearing score must be between 0 and 1");
    }
    this.username = username;
    this.buddies = new MTLoBuddy();
    this.diction = diction;
    this.hearing = hearing;
  }

  // EFFECT:
  // Change this person's buddy list so that it includes the given person
  void addBuddy(Person toAdd) {
    if (!this.buddies.hasBuddy(toAdd)) {
      this.buddies = new ConsLoBuddy(toAdd, this.buddies);
    }
  }

  // Checks if that Person is the same as this Person (based on their unique usernames)
  boolean samePerson(Person that) {
    return this.username.equals(that.username);
  }

  // returns true if this Person has that as a direct buddy
  boolean hasDirectBuddy(Person that) {
    return this.buddies.hasBuddy(that);
  }

  // returns the number of people who will show up at the party
  // given by this person
  int partyCount() {
    return this.totalExtendedBuddies(new MTLoBuddy());
  }

  // Determines the total number of extended buddies that this Person has
  // Accumulator: the Persons that've already been visited
  int totalExtendedBuddies(ILoBuddy visited) {
    if (visited.hasBuddy(this)) {
      return this.buddies.countExtendedBuddies(visited);
    }

    return 1 + this.buddies.countExtendedBuddies(new ConsLoBuddy(this, visited));
  }

  // returns the number of people that are direct buddies
  // of both this and that person
  int countCommonBuddies(Person that) {
    return this.buddies.countCommonBuddies(that.buddies);
  }

  // will the given person be invited to a party
  // organized by this person?
  boolean hasExtendedBuddy(Person that) {
    return this.hasExtendedBuddyAcc(that, new MTLoBuddy());
  }

  // will the given Person be invited to a party organized by this Person?
  // Accumulator: gathers the Persons who've been checked for extended buddies so far
  boolean hasExtendedBuddyAcc(Person that, ILoBuddy visited) {
    if (visited.hasBuddy(this)) {
      return false;
    } else {
      return this.hasDirectBuddy(that) || this.buddies.hasExtendedBuddy(that,
              new ConsLoBuddy(this, visited));
    }
  }
    
    //Determines the max likelihood that the given Person will hear a message from this Person
    double maxLikelihood(Person that) {
      if (this.samePerson(that)) {
        return 1.0;
      }
    return this.buddies.maxLikelihood(this, that, new ConsLoBuddy(this, new MTLoBuddy()));
  }

  //Calculates the likelihood that Person will hear this correctly
  double calcLikelihood(Person that) {
    return this.diction * that.hearing;
  }
}
