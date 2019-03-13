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
  
  Person a;
  Person b;
  Person c;
  Person d;
  Person e;

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
    
    this.a = new Person("A", this.mt, 0.95, 0.8);
    this.b = new Person("B", this.mt, 0.85, 0.99);
    this.c = new Person("C", this.mt, 0.95, 0.9);
    this.d = new Person("D", this.mt, 1, 0.95);
    this.e = new Person("E");
    
    this.a.addBuddy(this.b);
    this.a.addBuddy(this.c);
    this.b.addBuddy(this.d);
    this.c.addBuddy(this.d);

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
  
  void testTotalExtendedBuddies(Tester t) {
    init();
    t.checkExpect(this.hank.totalExtendedBuddies(this.mt), 1);
    t.checkExpect(this.cole.totalExtendedBuddies(this.mt), 2);
    t.checkExpect(this.ann.totalExtendedBuddies(this.mt), 8);
  }
  
  void testCountExtendedBuddies(Tester t) {
    init();
    t.checkExpect(this.hank.buddies.countExtendedBuddies(new ConsLoBuddy(this.hank,this.mt)), 0);
    t.checkExpect(this.cole.buddies.countExtendedBuddies(new ConsLoBuddy(this.cole,this.mt)), 1);
    t.checkExpect(this.ann.buddies.countExtendedBuddies(new ConsLoBuddy(this.ann,this.mt)), 7);
  }
  
  void testMaxLikelihood(Tester t) {
    init();
    t.checkExpect(this.e.maxLikelihood(this.a), 0.0);
    t.checkExpect(this.b.maxLikelihood(this.b), 1.0);
    t.checkExpect(this.b.maxLikelihood(this.e), 0.0);
    t.checkInexact(this.c.maxLikelihood(this.d), 0.902, 0.001);
    t.checkInexact(this.a.maxLikelihood(this.d), 0.772, 0.001);
  }
  
  void testMaxLikelihoodList(Tester t) {
    init();
    t.checkInexact(this.e.buddies.maxLikelihood(this.e, this.a, 
        new ConsLoBuddy(this.e, this.mt)), 0.0, 0.001);
    t.checkInexact(this.b.buddies.maxLikelihood(this.b, this.a, 
        new ConsLoBuddy(this.b, this.mt)), 0.0, 0.001);
    t.checkInexact(this.c.buddies.maxLikelihood(this.c, this.d, 
        new ConsLoBuddy(this.c, this.mt)), 0.902, 0.001);
    t.checkInexact(this.a.buddies.maxLikelihood(this.a, this.d, 
        new ConsLoBuddy(this.a, this.mt)), 0.772, 0.001);
  }
  
  void testCalcLikelihood(Tester t) {
    init();
    t.checkInexact(this.a.calcLikelihood(this.c), 0.855, 0.001);
  }
}