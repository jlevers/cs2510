
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
}
