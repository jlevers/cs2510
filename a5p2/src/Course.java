import tester.*;

// Represents an academic course
class Course {
  String name;
  IList<Course> prereqs;

  Course(String name, IList<Course> prereqs) {
    this.name = name;
    this.prereqs = prereqs;
  }

  // Checks if this Course has a prereq with the given name
  boolean hasPrereq(String name) {
    IPred hasP = new HasPrereq(name);
    IListVisitor<Course, Boolean> ormap = new Ormap<>(hasP);
    return this.prereqs.accept(ormap);
  }

  // Checks if this course is the same as the given String
  boolean hasName(String otherName) {
    return this.name.equals(otherName);
  }
}

// Represents a predicate function for checking if a Course has a specific prereq course (by name)
class HasPrereq implements IPred<Course> {
  String prereq;

  HasPrereq(String prereq) {
    this.prereq = prereq;
  }

  // Checks if the course has this prereq
  public Boolean call(Course c) {
    IListVisitor<Course, Boolean> ormapNames = new Ormap<>(new IsCourse(this.prereq));
    IListVisitor<Course, Boolean> ormapCourses = new Ormap<>(this);
    return ormapNames.call(c.prereqs) || ormapCourses.call(c.prereqs);
  }
}

// Represents a function that checks if a String is the same as the name of a given Course
class IsCourse implements IPred<Course> {
  String name;

  IsCourse(String name) {
    this.name = name;
  }

  // Compares this.name to the name of the Course
  public Boolean call(Course t) {
    return t.hasName(this.name);
  }
}

// Represents a function that finds the deepest path of prereqs for a Course
//class DeepestPathLength extends AListVisitor<Course, Integer> {
//  public Integer visitCons(ConsList<Course> ne) {
//    Map<Course, Integer> toDepth = new Map<>(new DeepestPathLength());
//    IList<Integer> depths = toDepth.call(ne);
//    return null;
//  }
//
//  public Integer visitMt(MtList<Course> mt) {
//    return 0;
//  }
//}

// A generic list of T
interface IList<T> {
  // Runs the given ILoDispF on this IList<T>, producing something of type R
  <R> R accept(IListVisitor<T, R> disp);
}

// Represents an empty list of T
class MtList<T> implements IList<T> {
  // Dispatches the given ILoDispF on this empty list
  public <R> R accept(IListVisitor<T, R> disp) {
    return disp.visitMt(this);
  }
}

// Represents a generic non-empty list of type T
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template:
   * Fields:
   * this.first ... T
   * this.rest ... IList<T>
   *
   * Methods:
   * this.accept(IListVisitor<T, R>) ... R
   *
   * Methods of fields:
   * this.rest.accept(IListVisitor<T, R>) ... R
   */

  // Dispatches the given ILoDispF on this non-empty list
  public <R> R accept(IListVisitor<T, R> disp) {
    return disp.visitCons(this);
  }
}


// Represents a non-method function
interface IFunc<A, R> {
  // Calls a function with input type A and return type R
  R call (A x);
}

// Represents a reducer function (takes in some data to process, X, and returns a reduced version
// of type Y
interface IRed<X, Y> {
  // Reduces X into Y
  Y red(X x, Y y);
}


// Represents a predicate for comparing two things of type T
interface IPred<X> extends IFunc<X, Boolean> {
  // Applies this predicate to the given input T
  Boolean call(X t);
}


// An IFunc that builds a list of the given length, creating each list item using this.fn
class BuildList<R> implements IFunc<Integer, IList<R>> {
  IFunc<Integer, R> fn;

  BuildList(IFunc<Integer, R> fn) {
    this.fn = fn;
  }

  // Creates a list by calling this.fn on each number <= to the number passed to this
  public IList<R> call(Integer integer) {
    Integer currInt = integer - 1;
    if (currInt >= 0) {
      return new ConsList<R>(this.fn.call(currInt),
              new BuildList<R>(this.fn).call(currInt));
    }

    return new MtList<R>();
  }
}


// Interface for dispatching over lists of type T
interface IListVisitor<T, R> extends IFunc<IList<T>, R> {
  // Dispatches this function in the case of a non-empty list
  R visitCons(ConsList<T> ne);
  // Dispatches this function in the case of an empty list
  R visitMt(MtList<T> mt);
}


// Represents a list operation function
abstract class AListVisitor<T, R> implements IListVisitor<T, R> {
  // Calls whatever this list function is on the given list
  public R call(IList<T> x) {
    return x.accept(this);
  }

  /*
   * Template:
   * Fields:
   *
   * Methods:
   * this.call(IList<T>) ... R
   * this.visitCons(ConsList<T>) ... R
   * this.visitMt(MtList<T>) ... R
   *
   * Methods of Fields:
   */

  // Performs the list operation on a non-empty list
  public abstract R visitCons(ConsList<T> ne);

  // Performs the list operation on an empty list
  public abstract R visitMt(MtList<T> mt);
}


// Maps over a list of T
class Map<T, R> extends AListVisitor<T, IList<R>> {
  IFunc<T, R> fun;

  Map(IFunc<T, R> fun) {
    this.fun = fun;
  }

  /*
   * Template:
   * Fields:
   * this.fun ... IFunc<T, R>
   *
   * Methods:
   *
   * Methods of Fields:
   * this.fun.call(IList<T>, R)
   */

  // Performs this.fun on the list in the Cons case
  public IList<R> visitCons(ConsList<T> ne) {
    return new ConsList<R>(this.fun.call(ne.first), ne.rest.accept(this));
  }

  // Performs this.fun on the list in the Mt case
  public IList<R> visitMt(MtList<T> mt) {
    return new MtList<R>();
  }
}

// Represents a foldr
class FoldR<T, R> extends AListVisitor<T, R> {
  IRed<T, R> fn;
  R base;

  FoldR(IRed<T, R> fn, R base) {
    this.fn = fn;
    this.base = base;
  }

  // Folds down a non-empty list
  public R visitCons(ConsList<T> ne) {
    return new FoldR<>(this.fn, fn.red(ne.first, this.base)).call(ne.rest);
  }

  // Returns the folded value
  public R visitMt(MtList<T> mt) {
    return base;
  }
}

// Represents an ormap
class Ormap<T> extends AListVisitor<T, Boolean> {
  IPred<T> pred;

  Ormap(IPred<T> pred) {
    this.pred = pred;
  }

  // Creates a boolean reducer from this.pred and FoldR's over the given list using it
  public Boolean visitCons(ConsList<T> ne) {
    IRed<T, Boolean> red = new OrmapReduce<>(this.pred);
    IListVisitor<T, Boolean> fold = new FoldR<>(red, false);
    return ne.accept(fold);
  }

  // Returns false because the base case of an ormap is always fales
  public Boolean visitMt(MtList<T> mt) {
    return false;
  }
}

// Represents a Boolean reducer function
class OrmapReduce<T> implements IRed<T, Boolean> {
  IPred<T> pred;

  OrmapReduce(IPred<T> pred) {
    this.pred = pred;
  }

  // True if this predicate is true for the given value, or if the base value is true
  public Boolean red(T t, Boolean base) {
    return pred.call(t) || base;
  }
}


// Filters over a list of T
class Filter<X> extends AListVisitor<X, IList<X>> {
  IPred<X> pred;

  Filter(IPred<X> pred) {
    this.pred = pred;
  }

  /*
   * Template:
   * Fields:
   * this.pred ... IPred<X>
   *
   * Methods:
   *
   * Methods of Fields:
   * this.pred.call(T) ... boolean
   */

  // Filters through a non-empty list
  public IList<X> visitCons(ConsList<X> ne) {
    if (this.pred.call(ne.first)) {
      return new ConsList<X>(ne.first, ne.rest.accept(this));
    }

    return ne.rest.accept(this);
  }

  // Filters through an empty list
  public IList<X> visitMt(MtList<X> mt) {
    return new MtList<X>();
  }
}


class ExamplesCourse {

  IList<Course> mtListCourses = new MtList<>();
  Course cs2500 = new Course("Fundies 1", this.mtListCourses);
  Course cs2510 = new Course("Fundies 2", new ConsList<>(this.cs2500, this.mtListCourses));
  Course cs3000 = new Course("Algo", new ConsList<>(this.cs2510, this.mtListCourses));

  // Just for testing!
  class StrLen implements IFunc<String, Integer> {
    public Integer call(String s) {
      return s.length();
    }
  }

  // Making sure IList<T> typechecks as expected
  MtList<String> mt = new MtList<>();
  ConsList<String> s1 = new ConsList<>("table", new ConsList<>("dog", this.mt));
  IFunc<String, Integer> f = new StrLen();
  // Making sure IListVisitor<T, R> and Map<T, R> typecheck as expected
  IListVisitor<String, IList<Integer>> df = new Map<>(this.f);
  IList<Integer> afterMap = new ConsList<>(5, new ConsList<>(3, new MtList<>()));

  IList<Integer> results = new ConsList<Integer>(4,
          new ConsList<Integer>(3,
                  new ConsList<Integer>(2,
                          new ConsList<Integer>(1,
                                  new ConsList<Integer>(0, new MtList<Integer>())))));

  boolean testVisit(Tester t) {
    return t.checkExpect(this.s1.accept(this.df), this.afterMap);
  }

  boolean testCallIFunc(Tester t) {

    return t.checkExpect(this.f.call("test"), 4)
            && t.checkExpect(this.df.call(this.s1), this.afterMap);
  }

  boolean testMap(Tester t) {
    return t.checkExpect(this.df.visitCons(this.s1), this.afterMap)
            && t.checkExpect(this.df.visitMt(this.mt), new MtList<Integer>());
  }

  boolean testBuildList(Tester t) {
    // Just for testing purposes
    class Identity implements IFunc<Integer, Integer> {
      public Integer call(Integer integer) {
        return integer;
      }
    }
    IFunc<Integer, IList<Integer>> simple = new BuildList<Integer>(new Identity());

    return t.checkExpect(simple.call(5), this.results);
  }

  boolean testFoldR(Tester t) {
    // Just for testing
    class Sum implements IRed<Integer, Integer> {
      public Integer red(Integer integer, Integer integer2) {
        return integer + integer2;
      }
    }

    IListVisitor<Integer, Integer> fold = new FoldR<>(new Sum(), 0);
    return t.checkExpect(this.results.accept(fold), 10);
  }

  boolean testFilter(Tester t) {
    // Just for testing!
    class NotDog implements IPred<String> {
      public Boolean call(String s) {
        return !s.equals("dog");
      }
    }

    IListVisitor<String, IList<String>> noDogFilter = new Filter<String>(new NotDog());
    IList<String> noDogs = new ConsList<String>("table", new MtList<String>());
    return t.checkExpect(noDogFilter.call(this.s1), noDogs)
            && t.checkExpect(noDogFilter.call(new MtList<String>()), new MtList<String>());
  }
}
