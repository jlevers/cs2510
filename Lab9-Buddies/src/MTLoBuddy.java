// represents an empty list of Person's buddies
class MTLoBuddy implements ILoBuddy {
  MTLoBuddy() {}

  // An MTLoBuddy never has the given Person in it
  public boolean hasBuddy(Person p) {
    return false;
  }

  // An MTLoBuddy never has the same People in it as any other list
  public int countCommonBuddies(ILoBuddy that) {
    return 0;
  }

  // An MTLoBuddy never has any buddies at all
  public boolean hasExtendedBuddy(Person that, ILoBuddy visited) {
    return false;
  }

  // An MTLoBuddy never has any extended buddies
  public int countExtendedBuddies(ILoBuddy visited) {
    return 0;
  }

  //Determines the max likelihood the given person will hear the message
  // 0, because if that Person is not in this list of buddies they will not receive the message
  public double maxLikelihood(Person first, Person that, ConsLoBuddy visited) {
    return 0;
  }
}
