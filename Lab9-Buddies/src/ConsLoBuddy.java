// represents a list of Person's buddies
class ConsLoBuddy implements ILoBuddy {

  Person first;
  ILoBuddy rest;

  ConsLoBuddy(Person first, ILoBuddy rest) {
      this.first = first;
      this.rest = rest;
  }

  // Checks if the given Person is in this ConsLoBuddy
  public boolean hasBuddy(Person p) {
      return this.first.samePerson(p) || this.rest.hasBuddy(p);
  }

  // Checks if that has any common Persons with this
  public int countCommonBuddies(ILoBuddy that) {
    return (that.hasBuddy(this.first) ? 1 : 0) + this.rest.countCommonBuddies(that);
  }

  // Checks if that Person is an extended buddy of this Person (buddies with any of this Person's
  // buddies
  // Accumulator: collects the Persons who've already been visited
  public boolean hasExtendedBuddy(Person that, ILoBuddy visited) {
    if (visited.hasBuddy(this.first)) {
      return this.rest.hasExtendedBuddy(that, visited);
    } else {
      return this.first.samePerson(that)
              || this.first.hasExtendedBuddyAcc(that, visited)
              || this.rest.hasExtendedBuddy(that, new ConsLoBuddy(this.first, visited));
    }
  }
  
  //Returns the amount of buddies in this list of buddies
  public int length() {
    return 1 + this.rest.length();
  }
}
