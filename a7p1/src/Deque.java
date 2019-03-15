import tester.*;

// Represents a Deque
class Deque<T> {
  Sentinel<T> header;

  // Initializes a starter Deque
  Deque() {
    this.header = new Sentinel<>();
  }

  // Sets the Deque's header to be the given Sentinel
  Deque(Sentinel<T> header) {
    this.header = header;
  }
}

// Represents a node in a Deque
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // EFFECT: sets this ANode's next reference to the given node
  void setNext(ANode<T> next) {
    this.next = next;
  }

  // EFFECT: sets this ANode's prev reference to the given node
  void setPrev(ANode<T> prev) {
    this.prev = prev;
  }
}

// Represents a sentinel value in in a Deque
class Sentinel<T> extends ANode<T> {
  // Sets the Sentinel's prev and next values to itself
  Sentinel() {
    this.next = this;
    this.prev = this;
  }
}

// Represents an item in a Deque
class Node<T> extends ANode<T> {
  T data;

  // Initialize this Node to point to null values
  Node(T data) {
    this.next = null;
    this.prev = null;
    this.data = data;
  }

  // Allows the data, and prev/next references to be set for this Node
  Node(T data, ANode<T> next, ANode<T> prev) {
    if (this.next == null || this.prev == null) {
      throw new IllegalArgumentException("The prev and next fields of a Node, when called with " +
              "this constructor, must be non-null.");
    }

    this.data = data;

    this.next = next;
    this.prev = prev;
    this.next.setPrev(this);
    this.prev.setNext(this);
  }
}

class ExamplesDeque {
  Deque<String> deque1, deque2, deque3;
  ANode<String> s1, s2;
  ANode<String> abc, bcd, cde, def;
  ANode<String> only, made, to, test;

  void init() {
    this.deque1 = new Deque<>();
    this.deque2 = new Deque<>();
    this.deque3 = new Deque<>();

    this.s1 = new Sentinel<>();
    this.s2 = new Sentinel<>();

    this.abc = new Node<>("abc");
    this.abc.setNext(this.s1);
    this.abc.setPrev(this.bcd);
    this.bcd = new Node<>("bcd");
    this.bcd.setNext(this.cde);
    this.cde = new Node<>("cde");
    this.cde.setNext(this.def);
    this.def = new Node<>("def");
    this.def.setNext(this.s1);

    this.only = new Node<>("only");
    this.only.setNext(this.made);
    this.only.setPrev(this.s2);
    this.made = new Node<>("made");
    this.made.setNext(this.to);
    this.to = new Node<>("to");
    this.to.setNext(this.test);
    this.test = new Node<>("test");
    this.test.setNext(this.s2);
  }

  void testNodeConstructor(Tester t) {
    t.checkConstructorException(new IllegalArgumentException("The prev and next fields of a Node, "
            + "when called with this constructor, must be non-null."),
            "Node", "test", null, new Node<String>("asdf"));
  }
}