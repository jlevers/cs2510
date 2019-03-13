import tester.*;


// runs tests for the buddies problem
public class ExamplesBuddies {
  Person ann;
  Person bob;
  Person cole;
  Person dan;
  Person ed;
  Person fay;
  Person gabi;
  Person hank;
  Person jan;
  Person kim;
  Person len;

  ILoBuddy mt = new MTLoBuddy();
  ILoBuddy testBuddy;

  // Initialize data
  void init() {

    this.ann = new Person("Ann");
    this.bob = new Person("Bob");
    this.cole = new Person("Cole");
    this.dan = new Person("Dan");
    this.ed = new Person("Ed");
    this.fay = new Person("Fay");
    this.gabi = new Person("Gabi");
    this.hank = new Person("Hank");
    this.jan = new Person("Jan");
    this.kim = new Person("Kim");
    this.len = new Person("Len");

    this.ann.addBuddy(this.bob);
    this.ann.addBuddy(this.cole);
    this.bob.addBuddy(this.ann);
    this.bob.addBuddy(this.ed);
    this.bob.addBuddy(this.hank);
    this.cole.addBuddy(this.dan);
    this.dan.addBuddy(this.cole);
    this.ed.addBuddy(this.fay);
    this.fay.addBuddy(this.ed);
    this.fay.addBuddy(this.gabi);
    this.gabi.addBuddy(this.ed);
    this.gabi.addBuddy(this.fay);
    this.jan.addBuddy(this.kim);
    this.jan.addBuddy(this.len);
    this.kim.addBuddy(this.jan);
    this.kim.addBuddy(this.len);
    this.len.addBuddy(this.jan);
    this.len.addBuddy(this.kim);

    this.testBuddy = new ConsLoBuddy(this.ed, this.mt);
  }

  void testSamePerson(Tester t) {
    init();
    t.checkExpect(this.ann.samePerson(this.ann), true);
    t.checkExpect(this.bob.samePerson(this.cole), false);
  }

  void testHasBuddy(Tester t) {
    init();
    t.checkExpect(this.mt.hasBuddy(dan), false);
    t.checkExpect(this.testBuddy.hasBuddy(this.ed), true);
    t.checkExpect(this.testBuddy.hasBuddy(this.fay), false);
  }

  void testHasDirectBuddy(Tester t) {
    init();
    t.checkExpect(this.fay.hasDirectBuddy(this.gabi), true);
    t.checkExpect(this.fay.hasDirectBuddy(this.dan), false);
  }

  void testAddBuddy(Tester t) {
    init();
    t.checkExpect(this.fay.hasDirectBuddy(this.kim), false);
    this.fay.addBuddy(this.kim);
    t.checkExpect(this.fay.hasDirectBuddy(this.kim), true);
  }

  void testCountCommonBuddiesPerson(Tester t) {
    init();
    t.checkExpect(this.fay.countCommonBuddies(this.gabi), 1);
    t.checkExpect(this.kim.countCommonBuddies(this.dan), 0);
    this.bob.addBuddy(this.gabi);
    t.checkExpect(this.fay.countCommonBuddies(this.bob), 2);
  }

  void testCountCommonBuddiesList(Tester t) {
    init();
    t.checkExpect(this.fay.buddies.countCommonBuddies(this.gabi.buddies), 1);
    t.checkExpect(this.kim.buddies.countCommonBuddies(this.dan.buddies), 0);
    this.bob.addBuddy(this.gabi);
    t.checkExpect(this.fay.buddies.countCommonBuddies(this.bob.buddies), 2);
  }

  void testHasExtendedBuddyPerson(Tester t) {
    init();
    t.checkExpect(this.ed.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.ann.hasExtendedBuddy(this.hank), true);
    t.checkExpect(this.ann.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.cole.hasExtendedBuddy(this.dan), true);
    t.checkExpect(this.jan.hasExtendedBuddy(this.cole), false);
  }

  void testHasExtendedBuddyAccPerson(Tester t) {
    init();
    t.checkExpect(this.ed.hasExtendedBuddyAcc(this.fay, this.mt), true);
    t.checkExpect(this.ann.hasExtendedBuddyAcc(this.hank, this.mt), true);
    t.checkExpect(this.bob.hasExtendedBuddyAcc(this.hank, new ConsLoBuddy(this.ann,
            this.mt)), true);
    t.checkExpect(this.bob.hasExtendedBuddyAcc(this.gabi, new ConsLoBuddy(this.ann,
            this.mt)), true);
    t.checkExpect(this.hank.hasExtendedBuddyAcc(this.ed, new ConsLoBuddy(this.bob, this.mt)),
            false);
  }

  void testHasExtendedBuddyList(Tester t) {
    init();
    t.checkExpect(this.ed.buddies.hasExtendedBuddy(this.fay, new ConsLoBuddy(this.ed, this.mt)),
            true);
    t.checkExpect(this.ann.buddies.hasExtendedBuddy(this.hank, this.mt), true);
    t.checkExpect(this.ann.buddies.hasExtendedBuddy(this.gabi, new ConsLoBuddy(this.ann, this.mt)),
            true);
    t.checkExpect(this.cole.buddies.hasExtendedBuddy(this.dan, new ConsLoBuddy(this.cole, this.mt)),
            true);
    t.checkExpect(this.jan.buddies.hasExtendedBuddy(this.cole, this.mt), false);
  }

  void testPartyCount(Tester t) {
    init();
    t.checkExpect(this.hank.partyCount(), 1);
    t.checkExpect(this.cole.partyCount(), 2);
    t.checkExpect(this.ann.partyCount(), 8);
  }

  // TODO: add tests for totalExtendedBuddies and countExtendedBuddies
}