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
    }
    else {
      throw new RuntimeException("This Student is already in the course " + c.name);
    }
  }

  // Checks if that Student is the same as this Student
  boolean sameStudent(Student that) {
    return that.name.equals(this.name) && that.id == this.id;
  }

  // Checks if this Student and that Student are in any of the same Courses
  boolean classmates(Student s) {
    Ormap<Course> allCourses = new Ormap<>(new InCourse(s, this));
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
    this.courses = new ConsList<>(c, this.courses);
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
  Student s1;
  Student s2;

  InCourse(Student s1, Student s2) {
    this.s1 = s1;
    this.s2 = s2;
  }

  // Checks if the given Student is in this Course
  public Boolean apply(Course c) {
    Ormap<Student> s1InCourse = new Ormap<>(new SameStudent(this.s1));
    Ormap<Student> s2InCourse = new Ormap<>(new SameStudent(this.s2));
    return c.students.accept(s1InCourse) && c.students.accept(s2InCourse);
  }
}

// Reduces a list of Courses to an integer, which represents how many Courses the given Student
// is in
class CountDejavu implements IRed<Course, Integer> {
  Student s;

  CountDejavu(Student s) {
    this.s = s;
  }

  // Checks if the given Course has this Student in it; if so, returns 1,
  // otherwise 0
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
  Course cs2510sec2;
  Course cs1800;
  Course math1341;
  Course math2321;
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
    this.cs2510sec2 = new Course("Fundies 2", this.i1);
    this.math1341 = new Course("Calc 1", this.i2);
    this.math2321 = new Course("Calc 3", this.i2);
  }

  // Used to initialize a course separately from the init() method to test
  // addCourse
  // for Instructors
  void initCourse() {
    cs1800 = new Course("Discrete Structures", this.i1);
  }

  void testEnroll(Tester t) {
    init();
    this.s1.enroll(this.cs2500);
    t.checkExpect(this.s1.courses, new ConsList<>(this.cs2500, new MtList<>()));
    t.checkExpect(this.cs2500.students, new ConsList<>(this.s1, new MtList<>()));
    t.checkException("Testing re-enrolling a Student in a Course they're already enrolled in",
        new RuntimeException("This Student is already in the course Fundies 1"), this.s1, "enroll",
        this.cs2500);
    this.s2.enroll(this.cs2500);
    t.checkExpect(this.s2.courses, new ConsList<>(this.cs2500, new MtList<>()));
    t.checkExpect(this.cs2500.students,
        new ConsList<>(this.s2, new ConsList<>(this.s1, new MtList<>())));
  }

  void testSameCourseClass(Tester t) {
    init();
    SameCourse sc = new SameCourse(this.cs2510);
    t.checkExpect(sc.apply(this.cs2510), true);
    t.checkExpect(sc.apply(this.math1341), false);
    this.s1.enroll(this.cs2510);
    this.s2.enroll(this.cs2510sec2);
    this.s3.enroll(this.cs2510sec2);
    t.checkExpect(sc.apply(this.cs2510sec2), false);
  }

  void testSameStudentClass(Tester t) {
    init();
    SameStudent ss = new SameStudent(this.s3);
    t.checkExpect(ss.apply(this.s3), true);
    t.checkExpect(ss.apply(this.s4), false);
  }

  void testInCourseClass(Tester t) {
    init();
    InCourse ic = new InCourse(this.s2, this.s1);
    t.checkExpect(ic.apply(this.cs2510), false);
    this.s2.enroll(this.cs2510);
    t.checkExpect(ic.apply(this.cs2510), false);
    this.s1.enroll(this.cs2510);
    t.checkExpect(ic.apply(this.cs2510), true);

  }

  void testAddCourse(Tester t) {
    init();
    t.checkExpect(this.i1.courses, new ConsList<>(this.cs2510sec2,
        new ConsList<>(this.cs2510, new ConsList<>(this.cs2500, new MtList<>()))));

    // Adds a course to the registrar and adds it to the instructor's list of
    // courses
    initCourse();
    t.checkExpect(this.i1.courses, new ConsList<>(this.cs1800, new ConsList<>(this.cs2510sec2,
        new ConsList<>(this.cs2510, new ConsList<>(this.cs2500, new MtList<>())))));
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
    this.s2.enroll(this.cs2510sec2);
    this.s1.enroll(this.cs2510sec2);
    t.checkExpect(this.cs2510.sameCourse(this.cs2510), true);
    t.checkExpect(this.cs2510.sameCourse(this.cs2510sec2), false);
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
    this.s5.enroll(this.cs2510sec2);
    this.s4.enroll(this.cs2510);
    t.checkExpect(this.s3.classmates(this.s4), true);
    t.checkExpect(this.s4.classmates(this.s3), true);
    t.checkExpect(this.s4.classmates(this.s1), false);
    t.checkExpect(this.s5.classmates(this.s4), false);
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