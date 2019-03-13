
// represents a list of Person's buddies
interface ILoBuddy {
  // Checks if the given Person is in this ILoBuddy
  boolean hasBuddy(Person p);

  // Counts the number of common Persons in this list and that list
  int countCommonBuddies(ILoBuddy that);

  // Checks if the given Person is an extended buddy of this Person (e.g., that Person is buddies
  // with someone who's a buddy of that Person.
  // Accumulator: collects the Persons who've already been visited
  boolean hasExtendedBuddy(Person that, ILoBuddy visited);

  // Counts all the extended buddies in this list, excluding the ones that've already been visited
  // Accumulator: the Persons who've already been visited/counted
  int countExtendedBuddies(ILoBuddy visited);

  //Determines the Max Likelihood that the given person will hear
  double maxLikelihood(Person first, Person that, ConsLoBuddy visited);
}
