import tester.*;

// Represents a course with an instructor and enrolled students
class Course {
  String name;
  IList<Student> students;
  Instructor prof;

  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<>();
    this.prof.addCourse(this);
  }

  // Creates a Course with a list of Students taking the course
  Course(String name, Instructor prof, IList<Student> students) {
    this(name, prof);
    this.students = students;
  }

  // Checks if that course is the same as this course
  boolean sameCourse(Course that) {
    Andmap<Student> sameStudents = new Andmap<>(new IncludesStudents(this.students));
    return that.prof.sameInstructor(this.prof) && that.name.equals(this.name)
            && that.students.accept(sameStudents);
  }
}

// Represents a student enrolled in classes
class Student {
  String name;
  int id;
  IList<Course> courses;

  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<>();
  }

  // Creates a Student with a list of courses they're taking
  Student(String name, int id, IList<Course> courses) {
    this(name, id);
    this.courses = courses;
  }

  // Enrolls this student in the given Course
  // EFFECTS: adds this Student to the list of Students for the given Course, and adds the given
  // Course to this Student's list of Courses
  void enroll(Course c) {
    Ormap<Course> alreadyEnrolled = new Ormap<>(new SameCourse(c));

    if (!alreadyEnrolled.apply(this.courses)) {
      c.students = new ConsList<>(this, c.students);
      this.courses = new ConsList<>(c, this.courses);
    } else {
      throw new RuntimeException("This Student is already in the course " + c.name);
    }
  }

  // Checks if that Student is the same as this Student
  boolean sameStudent(Student that) {
    return that.name.equals(this.name) && that.id == this.id;
  }

  // Checks if this Student and that Student are in any of the same Courses
  boolean classmates(Student s) {
    Ormap<Course> allCourses = new Ormap<>(new InCourse(s));
    return s.courses.accept(allCourses);
  }
}

// Represents an instructor of a course
class Instructor {
  String name;
  IList<Course> courses;

  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<>();
  }

  // Creates an Instructor with a list of courses they're teaching
  Instructor(String name, IList<Course> courses) {
    this(name);
    this.courses = courses;
  }

  // Adds a Course to this Instructor if they're not already teaching it
  // EFFECTS: adds a Course to this Instructor's list of Courses
  void addCourse(Course c) {
    Ormap<Course> hasCourse = new Ormap<>(new SameCourse(c));
    if (this.courses.accept(hasCourse)) {
      throw new RuntimeException("This Instructor is already teaching the given course.");
    } else {
      this.courses = new ConsList<>(c, this.courses);
    }
  }

  // Checks if that Instructor is the same as this Instructor
  boolean sameInstructor(Instructor that) {
    return that.name.equals(this.name);
  }

  // Checks if the given Student is in more than one of this Instructor's Courses
  boolean dejavu(Student s) {
    FoldR<Course, Integer> countStudent = new FoldR<>(new CountDejavu(s), 0);
    return this.courses.accept(countStudent) > 1;
  }
}

// Checks if two Courses are the same
class SameCourse implements IPred<Course> {
  Course c;

  SameCourse(Course c) {
    this.c = c;
  }

  // Checks if the two courses are the same
  public Boolean apply(Course course) {
    return course.sameCourse(c);
  }
}

// Checks if the given Student is in this list of Students
class IncludesStudents implements IPred<Student> {
  IList<Student> students;

  IncludesStudents(IList<Student> students) {
    this.students = students;
  }

  // Checks if this Student is in this Ilist<Student>
  public Boolean apply(Student s) {
    Ormap<Student> checkForStudent = new Ormap<>(new SameStudent(s));
    return this.students.accept(checkForStudent);
  }
}

// Checks if two Students are the same
class SameStudent implements IPred<Student> {
  Student s;

  SameStudent(Student s) {
    this.s = s;
  }

  // Checks if the given Student is the same as this Student
  public Boolean apply(Student student) {
    return student.sameStudent(s);
  }
}

// Checks if the given Student is in the Course
class InCourse implements IPred<Course> {
  Student s;

  InCourse(Student s) {
    this.s = s;
  }

  // Checks if the given Student is in this Course
  public Boolean apply(Course c) {
    Ormap<Student> inCourse = new Ormap<>(new SameStudent(this.s));
    return c.students.accept(inCourse);
  }
}

// Reduces a list of Courses to an integer, which represents how many Courses the given Student
// is in
class CountDejavu implements IRed<Course, Integer> {
  Student s;

  CountDejavu(Student s) {
    this.s = s;
  }

  // Checks if the given Course has this Student in it; if so, returns 1, otherwise 0
  public Integer red(Course course, Integer integer) {
    Ormap<Student> studentInCourse = new Ormap<>(new SameStudent(this.s));
    if (course.students.accept(studentInCourse)) {
      return 1 + integer;
    }

    return integer;
  }
}

class ExamplesRegistrar {
  Course cs2500;
  Course cs2510;
//  Course cs2510sec2;
  Course math1341;
  Course math2321;
  IList<Course> csDep;
  IList<Course> mathDep;
  Student s1;
  Student s2;
  Student s3;
  Student s4;
  Student s5;
  Instructor i1;
  Instructor i2;


  // Sets the default values for the test data
  void init() {
    this.i1 = new Instructor("Amal Ahmed");
    this.i2 = new Instructor("Lee-Peng Lee");

    this.s1 = new Student("Jesse", 1);
    this.s2 = new Student("Claudia", 2);
    this.s3 = new Student("Ben", 3);
    this.s4 = new Student("Jane", 4);
    this.s5 = new Student("Vitruvius", 5);

    this.cs2500 = new Course("Fundies 1", this.i1);
    this.cs2510 = new Course("Fundies 2", this.i1);
//    this.cs2510sec2 = new Course("Fundies 2", this.i1);
    this.math1341 = new Course("Calc 1", this.i2);
    this.math2321 = new Course("Calc 3", this.i2);
  }

  void testEnroll(Tester t) {
    init();
    this.s1.enroll(this.cs2500);
    t.checkExpect(this.s1.courses, new ConsList<>(this.cs2500, new MtList<>()));
    t.checkExpect(this.cs2500.students, new ConsList<>(this.s1, new MtList<>()));
    t.checkException("Testing re-enrolling a Student in a Course they're already enrolled in",
            new RuntimeException("This Student is already in the course Fundies 1"),
            this.s1, "enroll", this.cs2500);
    this.s2.enroll(this.cs2500);
    t.checkExpect(this.s2.courses, new ConsList<>(this.cs2500, new MtList<>()));
    t.checkExpect(this.cs2500.students, new ConsList<>(this.s2, new ConsList<>(this.s1,
            new MtList<>())));
  }

  void testSameCourseClass(Tester t) {
    init();
    SameCourse sc = new SameCourse(this.cs2510);
    t.checkExpect(sc.apply(this.cs2510), true);
    t.checkExpect(sc.apply(this.math1341), false);
    this.s1.enroll(this.cs2510);
//    this.s2.enroll(this.cs2510sec2);
//    this.s3.enroll(this.cs2510sec2);
//    t.checkExpect(sc.apply(this.cs2510sec2), false);
  }

  void testSameStudentClass(Tester t) {
    init();
    SameStudent ss = new SameStudent(this.s3);
    t.checkExpect(ss.apply(this.s3), true);
    t.checkExpect(ss.apply(this.s4), false);
  }

  void testInCourseClass(Tester t) {
    init();
    InCourse ic = new InCourse(this.s2);
    t.checkExpect(ic.apply(this.cs2510), false);
    this.s2.enroll(this.cs2510);
    t.checkExpect(ic.apply(this.cs2510), true);

  }

  void testAddCourse(Tester t) {
    init();
    this.i1.addCourse(this.math2321);
    t.checkExpect(this.i1.courses, new ConsList<>(this.math2321, new ConsList<>(this.cs2510,
            new ConsList<>(this.cs2500, new MtList<>()))));
    t.checkException("Testing that the same Course can't be added to an Instructor twice",
            new RuntimeException("This Instructor is already teaching the given course."),
            this.i1, "addCourse", this.cs2510);
  }

  void testCountDejavuClass(Tester t) {
    init();
    CountDejavu dj = new CountDejavu(this.s2);
    t.checkExpect(dj.red(this.math1341, 0), 0);
    s2.enroll(this.math2321);
    s2.enroll(this.math1341);
    t.checkExpect(dj.red(this.math1341, 0), 1);
    t.checkExpect(dj.red(this.math2321, 1), 2);
  }

  void testSameCourse(Tester t) {
    init();
    t.checkExpect(this.cs2500.sameCourse(this.cs2500), true);
    t.checkExpect(this.cs2510.sameCourse(this.math1341), false);
    this.s3.enroll(this.cs2510);
//    this.s2.enroll(this.cs2510sec2);
//    this.s1.enroll(this.cs2510sec2);
    t.checkExpect(this.cs2510.sameCourse(this.cs2510), true);
//    t.checkExpect(this.cs2510.sameCourse(this.cs2510sec2), false);
  }

  void testSameStudent(Tester t) {
    init();
    t.checkExpect(this.s1.sameStudent(this.s1), true);
    t.checkExpect(this.s4.sameStudent(this.s5), false);
    this.s4.enroll(this.math1341);
    t.checkExpect(this.s4.sameStudent(this.s4), true);
  }

  void testSameInstructor(Tester t) {
    init();
    t.checkExpect(this.i1.sameInstructor(this.i1), true);
    t.checkExpect(this.i2.sameInstructor(this.i1), false);
  }

  void testClassmates(Tester t) {
    init();
    this.s3.enroll(this.math1341);
    this.s4.enroll(this.cs2500);
    this.s4.enroll(this.math1341);
    t.checkExpect(this.s3.classmates(this.s4), true);
    t.checkExpect(this.s4.classmates(this.s3), true);
    t.checkExpect(this.s4.classmates(this.s1), false);
  }

  void testDejavu(Tester t) {
    init();
    t.checkExpect(this.i1.dejavu(this.s2), false);
    t.checkExpect(this.i2.dejavu(this.s2), false);
    s2.enroll(this.math1341);
    t.checkExpect(this.i2.dejavu(this.s2), false);
    s2.enroll(this.math2321);
    t.checkExpect(this.i2.dejavu(this.s2), true);
  }
}


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
   * Template: Fields: this.first ... T this.rest ... IList<T>
   *
   * Methods: this.accept(IListVisitor<T, R>) ... R
   *
   * Methods of fields: this.rest.accept(IListVisitor<T, R>) ... R
   */

  // Dispatches the given ILoDispF on this non-empty list
  public <R> R accept(IListVisitor<T, R> disp) {
    return disp.visitCons(this);
  }
}

// Represents a non-method function
interface IFunc<A, R> {
  // Calls a function with input type A and return type R
  R apply(A x);
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
  Boolean apply(X t);
}

// An IFunc that builds a list of the given length, creating each list item using this.fn
class BuildList<R> implements IFunc<Integer, IList<R>> {
  IFunc<Integer, R> fn;

  BuildList(IFunc<Integer, R> fn) {
    this.fn = fn;
  }

  // Creates a list by calling this.fn on each number <= to the number passed to this
  public IList<R> apply(Integer integer) {
    Integer currInt = integer - 1;
    if (currInt >= 0) {
      return new ConsList<R>(this.fn.apply(currInt), new BuildList<R>(this.fn).apply(currInt));
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
   * Template: Fields:
   *
   * Methods: this.call(IList<T>) ... R this.visitCons(ConsList<T>) ... R
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
   * Template: Fields: this.fun ... IFunc<T, R>
   *
   * Methods:
   *
   * Methods of Fields: this.fun.call(IList<T>, R)
   */

  // Performs this.fun on the list in the Cons case
  public IList<R> visitCons(ConsList<T> ne) {
    return new ConsList<R>(this.fun.apply(ne.first), ne.rest.accept(this));
  }

  // Performs this.fun on the list in the Mt case
  public IList<R> visitMt(MtList<T> mt) {
    return new MtList<R>();
  }

  //Accepts the given list, allows for delegation
  public IList<R> apply(IList<T> x) {
    return x.accept(this);
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

  //Accepts the given list, allows for delegation
  public R apply(IList<T> x) {
    return x.accept(this);
  }

}

// Represents an ormap
class Ormap<T> extends AListVisitor<T, Boolean> {
  IPred<T> pred;

  Ormap(IPred<T> pred) {
    this.pred = pred;
  }

  // Creates a boolean reducer from this.pred and FoldR's over the given list
  // using it
  public Boolean visitCons(ConsList<T> ne) {
    IRed<T, Boolean> red = new OrmapReduce<>(this.pred);
    IListVisitor<T, Boolean> fold = new FoldR<>(red, false);
    return ne.accept(fold);
  }

  // Returns false because the base case of an ormap is always false
  public Boolean visitMt(MtList<T> mt) {
    return false;
  }

  //Accepts the given list, allows for delegation
  public Boolean apply(IList<T> x) {
    return x.accept(this);
  }

}

// Represents a Boolean reducer function for an ormap
class OrmapReduce<T> implements IRed<T, Boolean> {
  IPred<T> pred;

  OrmapReduce(IPred<T> pred) {
    this.pred = pred;
  }

  // True if this predicate is true for the given value, or if the base value is true
  public Boolean red(T t, Boolean base) {
    return pred.apply(t) || base;
  }
}


// Represents an andmap
class Andmap<T> extends AListVisitor<T, Boolean> {
  IPred<T> pred;

  Andmap(IPred<T> pred) {
    this.pred = pred;
  }

  // Creates a boolean reducer from this.pred and FoldR's over the given list
  // using it
  public Boolean visitCons(ConsList<T> ne) {
    IRed<T, Boolean> red = new AndmapReduce<>(this.pred);
    IListVisitor<T, Boolean> fold = new FoldR<>(red, true);
    return ne.accept(fold);
  }

  // Returns true because the base case of an andmap is always true
  public Boolean visitMt(MtList<T> mt) {
    return true;
  }

  //Accepts the given list, allows for delegation
  public Boolean apply(IList<T> x) {
    return x.accept(this);
  }

}

// Represents a Boolean reducer function for an andmap
class AndmapReduce<T> implements IRed<T, Boolean> {
  IPred<T> pred;

  AndmapReduce(IPred<T> pred) {
    this.pred = pred;
  }

  // True if this predicate is true for the given value and the base value is true
  public Boolean red(T t, Boolean base) {
    return pred.apply(t) && base;
  }
}

// Filters over a list of T
class Filter<X> extends AListVisitor<X, IList<X>> {
  IPred<X> pred;

  Filter(IPred<X> pred) {
    this.pred = pred;
  }

  /*
   * Template: Fields: this.pred ... IPred<X>
   *
   * Methods:
   *
   * Methods of Fields: this.pred.call(T) ... boolean
   */

  // Filters through a non-empty list
  public IList<X> visitCons(ConsList<X> ne) {
    if (this.pred.apply(ne.first)) {
      return new ConsList<X>(ne.first, ne.rest.accept(this));
    }

    return ne.rest.accept(this);
  }

  // Filters through an empty list
  public IList<X> visitMt(MtList<X> mt) {
    return new MtList<X>();
  }

  public IList<X> apply(IList<X> x) {
    return x.accept(this);
  }
}

class ExamplesLists {
  // Just for testing!
  class StrLen implements IFunc<String, Integer> {
    public Integer apply(String s) {
      return s.length();
    }
  }

  class GreaterThan4 implements IPred<Integer> {
    public Boolean apply(Integer t) {
      return t > 4;
    }
  }

  MtList<String> mt = new MtList<>();
  ConsList<String> s1 = new ConsList<>("table", new ConsList<>("dog", this.mt));

  IFunc<String, Integer> f = new StrLen();
  IListVisitor<String, IList<Integer>> df = new Map<>(this.f);
  IList<Integer> afterMap = new ConsList<>(5, new ConsList<>(3, new MtList<>()));

  IList<Integer> results = new ConsList<Integer>(4,
          new ConsList<Integer>(3, new ConsList<Integer>(2,
                  new ConsList<Integer>(1, new ConsList<Integer>(0, new MtList<Integer>())))));

  // tests visit
  boolean testVisit(Tester t) {
    return t.checkExpect(this.s1.accept(this.df), this.afterMap);
  }

  // tests call to a Function
  boolean testCallIFunc(Tester t) {

    return t.checkExpect(this.f.apply("test"), 4)
            && t.checkExpect(this.df.apply(this.s1), this.afterMap);
  }

  // tests Map
  boolean testMap(Tester t) {
    return t.checkExpect(this.df.visitCons(this.s1), this.afterMap)
            && t.checkExpect(this.df.visitMt(this.mt), new MtList<Integer>());
  }

  // Tests build list
  boolean testBuildList(Tester t) {
    // Just for testing purposes
    class Identity implements IFunc<Integer, Integer> {
      public Integer apply(Integer integer) {
        return integer;
      }
    }

    IFunc<Integer, IList<Integer>> simple = new BuildList<Integer>(new Identity());

    return t.checkExpect(simple.apply(5), this.results);
  }

  // Tests Foldr
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

  // tests OrmapReduce
  boolean testOrmapReduce(Tester t) {
    IRed<Integer, Boolean> red = new OrmapReduce<>(new GreaterThan4());
    return t.checkExpect(red.red(5, true), true) && t.checkExpect(red.red(5, false), true)
            && t.checkExpect(red.red(4, true), true) && t.checkExpect(red.red(4, false), false);
  }

  // tests Ormap
  boolean testOrmap(Tester t) {
    IListVisitor<Integer, Boolean> ormap = new Ormap<>(new GreaterThan4());
    return t.checkExpect(this.afterMap.accept(ormap), true)
            && t.checkExpect(this.results.accept(ormap), false);
  }

  // tests AndmapReduce
  boolean testAndmapReduce(Tester t) {
    IRed<Integer, Boolean> red = new AndmapReduce<>(new GreaterThan4());
    return t.checkExpect(red.red(5, true), true) && t.checkExpect(red.red(5, false), false)
            && t.checkExpect(red.red(4, true), false) && t.checkExpect(red.red(4, false), false);
  }

  // tests Andmap
  boolean testAndmap(Tester t) {
    IListVisitor<Integer, Boolean> andmap = new Andmap<>(new GreaterThan4());
    IList<Integer> temp = new ConsList<>(6, new ConsList<>(5, new MtList<>()));
    return t.checkExpect(this.afterMap.accept(andmap), false)
            && t.checkExpect(this.results.accept(andmap), false)
            && t.checkExpect(temp.accept(andmap), true);
  }

  // tests Filter
  boolean testFilter(Tester t) {
    // Just for testing!
    class NotDog implements IPred<String> {
      public Boolean apply(String s) {
        return !s.equals("dog");
      }
    }

    IListVisitor<String, IList<String>> noDogFilter = new Filter<String>(new NotDog());
    IList<String> noDogs = new ConsList<String>("table", new MtList<String>());
    return t.checkExpect(noDogFilter.apply(this.s1), noDogs)
            && t.checkExpect(noDogFilter.apply(new MtList<String>()), new MtList<String>());
  }
}
