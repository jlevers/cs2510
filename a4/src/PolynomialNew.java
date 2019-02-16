import tester.*;

// Represents a monomial ax^i
class Monomial {
  int degree;
  int coefficient;

  Monomial(int degree, int coefficient) {
    if (degree < 0) {
      throw new IllegalArgumentException(
          "The degree of a Monomial must be non-negative. Given " + degree);
    }

    this.degree = degree;
    this.coefficient = coefficient;
  }

  /*
   * Template
   * Fields:
   * this.degree ... int
   * this.coefficient ... int
   *
   * Methods:
   * this.genDegreePred() ... IMonomialPred
   * this.sameMonomial(Monomial) boolean
   *
   * Methods of Fields:
   */

  // Generates a predicate based on this.degree
  IMonomialPred genDegreePred() {
    return new OfDegree(this.degree);
  }

  //Determines if the given monomial is the same as this
  boolean sameMonomial(Monomial other) {
    return (this.coefficient == other.coefficient) && (this.degree == other.degree);
  }
}

// An interface for all predicates for Monomials
interface IMonomialPred {
  // Applies this predicate to a Monomial
  boolean apply(Monomial m);
}

// A predicate for checking if a Monomial the same degree as this
class OfDegree implements IMonomialPred {
  int degree;

  OfDegree(int degree) {
    this.degree = degree;
  }
  /*
   * Template
   * Fields:
   * this.degree ... int
   *
   * Methods:
   * this.apply(Monomial) ... boolean
   *
   * Methods of Fields:
   */

  // Checks if the given Monomial is of degree this.degree
  public boolean apply(Monomial m) {
    return m.degree == this.degree;
  }

}

//A predicate for determining if a coefficient of a Monomial is 0
class NoZero implements IMonomialPred {
  /*
   * Template
   * Fields:
   *
   * Methods:
   * this.apply(Monomial) ... boolean
   *
   * Methods of Fields:
   */
  // Determines if the given Monomial coefficient is not 0
  public boolean apply(Monomial m) {
    return (m.coefficient != 0);
  }
}

// An interface for all comparators for Monomials
interface IMonomialComparator {
  // Compares the two given Monomials
  boolean compare(Monomial first, Monomial second);
}

class GreaterDegree implements IMonomialComparator {
  /*
   * Template
   * Fields:
   *
   * Methods:
   * this.apply(Monomial) ... boolean
   *
   * Methods of Fields:
   */
  // Returns true if first has a higher degree than second, false otherwise
  public boolean compare(Monomial first, Monomial second) {
    return first.degree > second.degree;
  }
}

// An interface for lists of Monomials
interface ILoMonomial {
  // Gets the length of this list of Monomials
  int length();

  // Filters this list of Monomials based on an IMonomialPred
  ILoMonomial filter(IMonomialPred pred);

  // Checks if this list of Monomials has any Monomials with the same degree
  boolean validPolynomial();

  // Sorts the list of Monomials based on an IMonomialComparator
  ILoMonomial sort(IMonomialComparator comp);

  // Sorts the list of Monomials based on an IMonomialComparator, using an
  // accumulator
  ILoMonomial sortHelper(IMonomialComparator comp, ILoMonomial acc);

  // Inserts the given element into this list using the given comparator
  ILoMonomial insert(IMonomialComparator comp, Monomial monomial);

  //Determines if this list of monomials is the same as the given ILoMonomial
  boolean sameMonomials(ILoMonomial other);

  //Determines if the given monomial is the same as MtLoMonomial
  boolean sameMtMonomial(MtLoMonomial other);

  //Determines if the given monomial is the same as ConsLoMonomial
  boolean sameConsMonomial(ConsLoMonomial other);
}

// Represents an empty list of Monomials
class MtLoMonomial implements ILoMonomial {
  /*
   * Template
   * Fields:
   *
   * Methods:
   * this.length() ... int
   * this.filter(IMonomialPred) ... ILoMonomial
   * this.validPolynomial() ... boolean
   * this.sort(IMonomialComparator) ... ILoMonomial
   * this.sortHelper(IMonomialComparator) ... ILoMonomial
   * this.insert(IMonomialComparartor, Monomial) ...ILoMonomial
   * this.sameMonomials(ILoMOnomial) ... boolean
   * this.sameMtMonomial(MtLoMonomial) ... boolean
   * this.sameConsMonomial(ConsLoMonomial) ... boolean
   *
   * Methods of Fields:
   */

  // Gets the length of this MtLoMonomial (always 0)
  public int length() {
    return 0;
  }

  // Filters this MtLoMonomial based on the given predicate (always an
  // MtLoMonomial)
  public ILoMonomial filter(IMonomialPred pred) {
    return this;
  }

  // Checks if this MtLoMonomial is a valid Polynomial (always true)
  public boolean validPolynomial() {
    return true;
  }

  // Sorts this MtLoMonomial using the given comparator (always just this)
  public ILoMonomial sort(IMonomialComparator comp) {
    return this;
  }

  // Sorts this MtLoMonomial using the comparator, with an accumulator of the
  // sorted list so far
  public ILoMonomial sortHelper(IMonomialComparator comp, ILoMonomial acc) {
    return acc;
  }

  // Inserts the given monomial into this MtLoMonomial
  public ILoMonomial insert(IMonomialComparator comp, Monomial monomial) {
    return new ConsLoMonomial(monomial, this);
  }

  //Determines if this empty is the same as the given Monomial {
  public boolean sameMonomials(ILoMonomial other) {
    return other.sameMtMonomial(this);
  }

  //Determines if the given MtLoMonomial is the same as the given MtLoMonomial
  public boolean sameMtMonomial(MtLoMonomial other) {
    return true;
  }

  //Determines if the given MtLoMonomial is the same as the given ConsLoMonomial
  public boolean sameConsMonomial(ConsLoMonomial other) {
    return false;
  }
}

// Represents a non-empty list of Monomials
class ConsLoMonomial implements ILoMonomial {
  Monomial first;
  ILoMonomial rest;

  ConsLoMonomial(Monomial first, ILoMonomial rest) {
    this.first = first;
    this.rest = rest;
  }
  /*
   * Template
   * Fields:
   * this.first ... Monomial
   * this.rest ... ILoMonomial
   *
   * Methods:
   * this.length() ... int
   * this.filter(IMonomialPred) ... ILoMonomial
   * this.validPolynomial() ... boolean
   * this.sort(IMonomialComparator) ... ILoMonomial
   * this.sortHelper(IMonomialComparator) ... ILoMonomial
   * this.insert(IMonomialComparartor, Monomial) ...ILoMonomial
   * this.sameMonomials(ILoMOnomial) ... boolean
   * this.sameMtMonomial(MtLoMonomial) ... boolean
   * this.sameConsMonomial(ConsLoMonomial) ... boolean
   *
   * Methods of Fields:
   * this.first.genDegreePred() ... IMonomialPred
   * this.first.sameMonomial(Monomial) ... boolean
   * this.rest.length() ... int
   * this.rest.filter(IMonomialPred) ... ILoMonomial
   * this.rest.validPolynomial() ... boolean
   * this.rest.sort(IMonomialComparator) ... ILoMonomial
   * this.rest.sortHelper(IMonomialComparator) ... ILoMonomial
   * this.rest.insert(IMonomialComparartor, Monomial) ...ILoMonomial
   * this.rest.sameMonomials(ILoMOnomial) ... boolean
   * this.rest.sameMtMonomial(MtLoMonomial) ... boolean
   * this.rest.sameConsMonomial(ConsLoMonomial) ... boolean
   */

  // Gets the length of this ConsLoMonomial
  public int length() {
    return 1 + this.rest.length();
  }

  // Filters this ConsLoMonomial based on the given predicate
  public ILoMonomial filter(IMonomialPred pred) {
    if (pred.apply(this.first)) {
      return new ConsLoMonomial(this.first, this.rest.filter(pred));
    }
    else {
      return this.rest.filter(pred);
    }
  }

  // Checks if this ConsLoMonomial is a valid Polynomial (e.g., has no repeated
  // degrees)
  public boolean validPolynomial() {
    int lengthDupDegree = this.rest.filter(this.first.genDegreePred()).length();
    return lengthDupDegree == 0 && this.rest.validPolynomial();
  }

  // Sorts this ConsLoMonomial using the given comparator
  public ILoMonomial sort(IMonomialComparator comp) {
    return this.sortHelper(comp, new MtLoMonomial());
  }

  // Sorts this ConsLoMonomial using the given comparator, accumulating the sorted
  // list in acc
  public ILoMonomial sortHelper(IMonomialComparator comp, ILoMonomial acc) {
    return this.rest.sortHelper(comp, acc.insert(comp, this.first));
  }

  // Inserts monomial into this ConsLoMonomial using the given comparator
  public ILoMonomial insert(IMonomialComparator comp, Monomial monomial) {
    if (comp.compare(this.first, monomial)) {
      return new ConsLoMonomial(this.first, this.rest.insert(comp, monomial));
    }
    else {
      return new ConsLoMonomial(monomial, this);
    }
  }

  //Determines if the given ConsLoMonomial is the same as ILoMonomial
  public boolean sameMonomials(ILoMonomial other) {
    return other.sameConsMonomial(this);
  }

  //Determines if this is the same as the given MtLoMonomial
  public boolean sameMtMonomial(MtLoMonomial other) {
    return false;
  }

  //Determines if the given ConsLoMonomial is the same as this
  public boolean sameConsMonomial(ConsLoMonomial other) {
    return this.first.sameMonomial(other.first)
        && this.rest.sameMonomials(other.rest);
  }
}

// Represents a polynomial ax^i + bx^(i-1) + ... + nx^0
class Polynomial {
  ILoMonomial monomials;

  public Polynomial(ILoMonomial monomials) {
    if (!monomials.validPolynomial()) {
      throw new IllegalArgumentException("Polynomials cannot contain monomials of the same "
          + "degree, unless the coefficients of one or more of the duplicated monomials is 0.");
    }
    this.monomials = monomials;
  }

  /*
   * Template
   * Fields:
   * this.monomials ... ILoMonomial
   *
   * Methods:
   * this.samePolynomial(Polynomial) ... boolean
   *
   * Methods of Fields:
   * this.monomials.length() ... int
   * this.monomials.filter(IMonomialPred) ... ILoMonomial
   * this.monomials.validPolynomial() ... boolean
   * this.monomials.sort(IMonomialComparator) ... ILoMonomial
   * this.monomials.sortHelper(IMonomialComparator) ... ILoMonomial
   * this.monomials.insert(IMonomialComparartor, Monomial) ...ILoMonomial
   * this.monomials.sameMonomials(ILoMOnomial) ... boolean
   * this.monomials.sameMtMonomial(MtLoMonomial) ... boolean
   * this.monomials.sameConsMonomial(ConsLoMonomial) ... boolean
   */

  public boolean samePolynomial(Polynomial other) {
    ILoMonomial sortedThis = this.monomials.sort(new GreaterDegree());
    ILoMonomial noZeroCoefficients = sortedThis.filter(new NoZero());
    ILoMonomial sortedOther = other.monomials.sort(new GreaterDegree());
    ILoMonomial noZeroCoefficientsOther = sortedOther.filter(new NoZero());

    return noZeroCoefficients.sameMonomials(noZeroCoefficientsOther);
  }
}

// Test class
class ExamplesPolynomial {
  Monomial deg0 = new Monomial(0, 2);
  Monomial deg1 = new Monomial(1, 1);
  Monomial deg2 = new Monomial(2, 5);
  Monomial extraDeg2 = new Monomial(2, 3);
  Monomial co0Deg2 = new Monomial(2, 0);

  IMonomialPred ofDeg2 = new OfDegree(2);
  IMonomialComparator greaterDeg = new GreaterDegree();

  ILoMonomial mt = new MtLoMonomial();
  ILoMonomial forPoly = new ConsLoMonomial(this.deg2,
      new ConsLoMonomial(this.deg0, new ConsLoMonomial(this.deg1, this.mt)));
  ILoMonomial sortedForPoly = new ConsLoMonomial(this.deg2,
      new ConsLoMonomial(this.deg1, new ConsLoMonomial(this.deg0, this.mt)));
  ILoMonomial skip1 = new ConsLoMonomial(this.deg2, new ConsLoMonomial(this.deg0, this.mt));

  ILoMonomial withDupDeg2 = new ConsLoMonomial(this.deg1,
      new ConsLoMonomial(this.deg2, new ConsLoMonomial(this.co0Deg2, this.mt)));
  ILoMonomial dupDeg2 = new ConsLoMonomial(this.deg2, new ConsLoMonomial(this.co0Deg2, this.mt));

  ILoMonomial withzerocoef = new ConsLoMonomial(this.deg1,
      new ConsLoMonomial(this.co0Deg2, this.mt));
  ILoMonomial withoutzero = new ConsLoMonomial(this.deg1, this.mt);

  Polynomial poly = new Polynomial(this.forPoly);

  // Tests Monomial.genDegreePred()
  boolean testGenDegreePred(Tester t) {
    return t.checkExpect(this.deg1.genDegreePred(), new OfDegree(1));
  }

  // Tests IMonomialPred.apply()
  boolean testApply(Tester t) {
    return t.checkExpect(this.ofDeg2.apply(this.deg2), true)
        && t.checkExpect(this.ofDeg2.apply(this.co0Deg2), true)
        && t.checkExpect(this.ofDeg2.apply(this.deg0), false);
  }

  // Tests IMonomialComparator.compare()
  boolean testCompare(Tester t) {
    return t.checkExpect(this.greaterDeg.compare(this.deg2, this.deg1), true)
        && t.checkExpect(this.greaterDeg.compare(this.deg0, this.deg1), false)
        && t.checkExpect(this.greaterDeg.compare(this.deg1, this.deg1), false);
  }

  // Tests ILoMonomial.testFilter()
  boolean testFilter(Tester t) {
    return t.checkExpect(this.withDupDeg2.filter(this.ofDeg2), this.dupDeg2)
        && t.checkExpect(this.forPoly.filter(this.ofDeg2), new ConsLoMonomial(this.deg2, this.mt));
  }

  // Tests ILoMonomial.length()
  boolean testLength(Tester t) {
    return t.checkExpect(this.forPoly.length(), 3) && t.checkExpect(this.dupDeg2.length(), 2)
        && t.checkExpect(this.mt.length(), 0);
  }

  // Tests ILoMonomial.validPolynomial()
  boolean testValidPolynomial(Tester t) {
    return t.checkExpect(this.forPoly.validPolynomial(), true)
        && t.checkExpect(this.withDupDeg2.validPolynomial(), false);
  }

  // Tests ILoMonomial.sort()
  boolean testSort(Tester t) {
    return t.checkExpect(this.forPoly.sort(this.greaterDeg), this.sortedForPoly)
        && t.checkExpect(this.sortedForPoly.sort(this.greaterDeg), this.sortedForPoly)
        && t.checkExpect(this.mt.sort(this.greaterDeg), this.mt);
  }

  // Tests ILoMonomial.sortHelper()
  boolean testSortHelper(Tester t) {
    return t.checkExpect(this.forPoly.sortHelper(this.greaterDeg, new MtLoMonomial()),
        this.sortedForPoly)
        && t.checkExpect(this.mt.sortHelper(this.greaterDeg, this.sortedForPoly),
            this.sortedForPoly);
  }

  // Tests ILoMonomial.insert()
  boolean testInsert(Tester t) {
    return t.checkExpect(this.skip1.insert(this.greaterDeg, this.deg1), this.sortedForPoly)
        && t.checkExpect(this.mt.insert(this.greaterDeg, this.deg2),
            new ConsLoMonomial(this.deg2, this.mt));
  }

  // Tests Monomial()
  boolean testMonomialConstructor(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException("The degree of a Monomial must be non-negative. Given -1"),
        "Monomial", -1, 3);
  }

  // Tests Polynomial()
  boolean testPolynomialConstructor(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException("Polynomials cannot contain monomials of the same "
            + "degree, unless the coefficients of one or more of the duplicated monomials"
            + " is 0."),
        "Polynomial", this.withDupDeg2);
  }

  // Tests ILoMonomial.filter(NoZero)
  boolean testFilterZeroes(Tester t) {
    return t.checkExpect(this.withzerocoef.filter(new NoZero()), this.withoutzero);
  }

  ILoMonomial ex1 = new ConsLoMonomial(new Monomial(2, 4),
      new ConsLoMonomial(new Monomial(3,1), new ConsLoMonomial(new Monomial(0, 0), this.mt)));

  ILoMonomial ex2 = new ConsLoMonomial(new Monomial(3, 1),
      new ConsLoMonomial(new Monomial(2,4), this.mt));

  Polynomial polyex1 = new Polynomial(this.ex1);
  Polynomial polyex2 = new Polynomial(this.ex2);

  //Tests if the given Polynomials are the same
  boolean testSamePolynomial(Tester t) {
    return t.checkExpect(this.poly.samePolynomial(this.poly), true)
        && t.checkExpect(this.poly.samePolynomial(new Polynomial(this.skip1)), false)
        && t.checkExpect(this.polyex1.samePolynomial(this.polyex2), true);
  }

  //Tests if the given ILoMonomials are the same
  boolean testSameMonomials(Tester t) {
    return t.checkExpect(this.mt.sameMonomials(this.mt), true)
        && t.checkExpect(this.forPoly.sameMonomials(this.forPoly), true)
        && t.checkExpect(this.dupDeg2.sameMonomials(this.forPoly), false)
        && t.checkExpect(this.mt.sameMonomials(this.forPoly), false);
  }
}