import tester.*;

// A generic list of T
interface ILo<T> {
  // Runs the given ILoDispF on this ILo<T>, producing something of type R
  <R> R visit(ILoDispF<T, R> disp);
}

// Represents an empty list of T
class MtLo<T> implements ILo<T> {
  // Dispatches the given ILoDispF on this empty list
  public <R> R visit(ILoDispF<T, R> disp) {
    return disp.forMt(this);
  }
}

// Represents a generic non-empty list of type T
class ConsLo<T> implements ILo<T> {
  T first;
  ILo<T> rest;

  ConsLo(T first, ILo<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template: Fields: this.first ... T this.rest ... ILo<T>
   *
   * Methods: this.visit(ILoDispF<T, R>) ... R
   *
   * Methods of fields: this.rest.visit(ILoDispF<T, R>) ... R
   */

  // Dispatches the given ILoDispF on this non-empty list
  public <R> R visit(ILoDispF<T, R> disp) {
    return disp.forCons(this);
  }
}

// Represents a non-method function
interface IFunc<X, Y> {
  // Calls a function with input type X and return type Y
  Y call(X x);
}

// Represents a reducer function (takes in some data to process, X, and returns a reduced version
// of type Y
interface IRed<X, Y> {
  // Reduces X into Y
  Y red(X x, Y y);
}

// Represents a predicate for comparing two things of type T
interface IPred<T> extends IFunc<T, Boolean> {
  // Applies this predicate to the given input T
  public Boolean call(T t);
}

// An IFunc that builds a list of the given length, creating each list item using this.fn
class BuildList<R> implements IFunc<Integer, ILo<R>> {
  IFunc<Integer, R> fn;

  BuildList(IFunc<Integer, R> fn) {
    this.fn = fn;
  }

  // Creates a list by calling this.fn on each number <= to the number passed to
  // this
  public ILo<R> call(Integer integer) {
    Integer currInt = integer - 1;
    if (currInt >= 0) {
      return new ConsLo<R>(this.fn.call(currInt), new BuildList<R>(this.fn).call(currInt));
    }

    return new MtLo<R>();
  }
}

// Interface for dispatching over IActors
interface IActorDispF<R> extends IFunc<IActor, R> {
  // Dispatches this function in the case of a Ship
  R forShip(Ship ship);

  // Dispatches this function in the case of a Bullet
  R forBullet(Bullet bullet);
}

// Interface for dispatching over lists of type T
interface ILoDispF<T, R> extends IFunc<ILo<T>, R> {
  // Dispatches this function in the case of a non-empty list
  R forCons(ConsLo<T> ne);

  // Dispatches this function in the case of an empty list
  R forMt(MtLo<T> mt);
}

// Represents a list operation function
abstract class ALoDispF<T, R> implements ILoDispF<T, R> {
  // Calls whatever this list function is on the given list
  public R call(ILo<T> x) {
    return x.visit(this);
  }

  /*
   * Template: Fields:
   *
   * Methods: this.call(ILo<T>) ... R this.forCons(ConsLo<T>) ... R
   * this.forMt(MtLo<T>) ... R
   *
   * Methods of Fields:
   */

  // Performs the list operation on a non-empty list
  public abstract R forCons(ConsLo<T> ne);

  // Performs the list operation on an empty list
  public abstract R forMt(MtLo<T> mt);
}

// Appends a list of T to another list of T
class Append<T> extends ALoDispF<T, ILo<T>> {
  ILo<T> toAppend;

  Append(ILo<T> toAppend) {
    this.toAppend = toAppend;
  }

  /*
   * Template: Fields: this.toAppend ... ILo<T>
   *
   * Methods:
   *
   * Methods of Fields: this.toAppend.visit(ILoDispF<T, R>) ... R
   */

  // Reconstructs the first list to make it possible to append the new list
  public ILo<T> forCons(ConsLo<T> ne) {
    return new ConsLo<T>(ne.first, ne.rest.visit(this));
  }

  // Adds the list to be appended to the end of the original list
  public ILo<T> forMt(MtLo<T> mt) {
    return toAppend;
  }
}

// Maps over a list of T
class Map<T, R> extends ALoDispF<T, ILo<R>> {
  IFunc<T, R> fun;

  Map(IFunc<T, R> fun) {
    this.fun = fun;
  }

  /*
   * Template: Fields: this.fun ... IFunc<T, R>
   *
   * Methods:
   *
   * Methods of Fields: this.fun.call(ILo<T>, R)
   */

  // Performs this.fun on the list in the Cons case
  public ILo<R> forCons(ConsLo<T> ne) {
    return new ConsLo<R>(this.fun.call(ne.first), ne.rest.visit(this));
  }

  // Performs this.fun on the list in the Mt case
  public ILo<R> forMt(MtLo<T> mt) {
    return new MtLo<R>();
  }
}

// Represents a foldr
class FoldR<T, R> extends ALoDispF<T, R> {
  IRed<T, R> fn;
  R base;

  FoldR(IRed<T, R> fn, R base) {
    this.fn = fn;
    this.base = base;
  }

  // Folds down a non-empty list
  public R forCons(ConsLo<T> ne) {
    return new FoldR<>(this.fn, fn.red(ne.first, this.base)).call(ne.rest);
  }

  // Returns the folded value
  public R forMt(MtLo<T> mt) {
    return base;
  }
}

// Filters over a list of T
class Filter<T> extends ALoDispF<T, ILo<T>> {
  IPred<T> pred;

  Filter(IPred<T> pred) {
    this.pred = pred;
  }

  /*
   * Template: Fields: this.pred ... IPred<T>
   *
   * Methods:
   *
   * Methods of Fields: this.pred.call(T) ... boolean
   */

  // Filters through a non-empty list
  public ILo<T> forCons(ConsLo<T> ne) {
    if (this.pred.call(ne.first)) {
      return new ConsLo<T>(ne.first, ne.rest.visit(this));
    }

    return ne.rest.visit(this);
  }

  // Filters through an empty list
  public ILo<T> forMt(MtLo<T> mt) {
    return new MtLo<T>();
  }
}

// Reduces a list to its length
class LengthRed<T> implements IRed<T, Integer> {
  // Increments for each item in the list
  public Integer red(T t, Integer i) {
    return ++i;
  }
}

class ExamplesLists {

  // Just for testing!
  class StrLen implements IFunc<String, Integer> {
    public Integer call(String s) {
      return s.length();
    }
  }

  // Making sure ILo<T> typechecks as expected
  MtLo<String> mt = new MtLo<>();
  ConsLo<String> s1 = new ConsLo<>("table", new ConsLo<>("dog", this.mt));
  IFunc<String, Integer> f = new StrLen();
  // Making sure ILoDispF<T, R> and Map<T, R> typecheck as expected
  ILoDispF<String, ILo<Integer>> df = new Map<>(this.f);
  ILo<Integer> afterMap = new ConsLo<>(5, new ConsLo<>(3, new MtLo<>()));

  ILo<Integer> results = new ConsLo<Integer>(4, new ConsLo<Integer>(3,
      new ConsLo<Integer>(2, new ConsLo<Integer>(1, new ConsLo<Integer>(0, new MtLo<Integer>())))));

  // Tests whether the appropriate Dispatch Function is visited
  boolean testVisit(Tester t) {
    return t.checkExpect(this.s1.visit(this.df), this.afterMap);
  }

  // Tests whether the given Dispatch Function is called
  boolean testCallIFunc(Tester t) {

    return t.checkExpect(this.f.call("test"), 4)
        && t.checkExpect(this.df.call(this.s1), this.afterMap);
  }

  // Tests whether the Function works for Empty and NonEmpty lists
  boolean testMap(Tester t) {
    return t.checkExpect(this.df.forCons(this.s1), this.afterMap)
        && t.checkExpect(this.df.forMt(this.mt), new MtLo<Integer>());
  }

  // Tests whether the appropriate list is built
  boolean testBuildList(Tester t) {
    // Just for testing purposes
    class Identity implements IFunc<Integer, Integer> {
      public Integer call(Integer integer) {
        return integer;
      }
    }
    
    IFunc<Integer, ILo<Integer>> simple = new BuildList<Integer>(new Identity());

    return t.checkExpect(simple.call(5), this.results);
  }

  // Tests FoldR
  boolean testFoldR(Tester t) {
    // Just for testing
    class Sum implements IRed<Integer, Integer> {
      public Integer red(Integer integer, Integer integer2) {
        return integer + integer2;
      }
    }

    ILoDispF<Integer, Integer> fold = new FoldR<>(new Sum(), 0);
    return t.checkExpect(this.results.visit(fold), 10);
  }

  // Tests Filter
  boolean testFilter(Tester t) {
    // Just for testing!
    class NotDog implements IPred<String> {
      public Boolean call(String s) {
        return !s.equals("dog");
      }
    }

    ILoDispF<String, ILo<String>> noDogFilter = new Filter<String>(new NotDog());
    ILo<String> noDogs = new ConsLo<String>("table", new MtLo<String>());
    return t.checkExpect(noDogFilter.call(this.s1), noDogs)
        && t.checkExpect(noDogFilter.call(new MtLo<String>()), new MtLo<String>());
  }

  // Tests Append
  boolean testAppend(Tester t) {
    ILo<String> toAppend = new ConsLo<>("one",
        new ConsLo<>("two", new ConsLo<>("three", new MtLo<>())));
    ILo<String> postAppend = new ConsLo<>("table", new ConsLo<>("dog",
        new ConsLo<>("one", new ConsLo<>("two", new ConsLo<>("three", new MtLo<>())))));
    ILoDispF<String, ILo<String>> append = new Append<String>(toAppend);

    return t.checkExpect(append.call(this.s1), postAppend)
        && t.checkExpect(append.call(new MtLo<>()), toAppend);
  }

  // Tests if the length of the list is returned
  boolean testLengthRed(Tester t) {
    ILoDispF<Integer, Integer> foldLen = new FoldR<>(new LengthRed<>(), 0);
    return t.checkExpect(this.results.visit(foldLen), 5)
        && t.checkExpect(new MtLo<Integer>().visit(foldLen), 0);
  }
}
