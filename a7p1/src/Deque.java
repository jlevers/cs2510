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
    if (next == null || prev == null) {
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
  Sentinel<String> s1, s2, s3;
  ANode<String> abc, bcd, cde, def;
  ANode<String> only, made, to, test;

  void init() {
    this.s1 = new Sentinel<>();
    this.s2 = new Sentinel<>();
    this.s3 = new Sentinel<>();
    
    this.deque1 = new Deque<>(this.s1);
    this.deque2 = new Deque<>(this.s2);
    this.deque3 = new Deque<>(this.s3);

    this.abc = new Node<>("abc");
    this.bcd = new Node<>("bcd");
    this.cde = new Node<>("cde");
    this.def = new Node<>("def");
    
    this.abc = new Node<>("abc", this.s2, this.bcd);
    this.bcd = new Node<>("bcd", this.abc, this.cde);
    this.cde = new Node<>("cde", this.bcd, this.def);
    this.def = new Node<>("def", this.cde, this.s2);

    this.only = new Node<>("only");
    this.made = new Node<>("made");
    this.to = new Node<>("to");
    this.test = new Node<>("test");
    
    this.only = new Node<>("only",this.s3, this.made);
    this.made = new Node<>("made", this.only, this.to);
    this.to = new Node<>("to", this.made, this.test);
    this.test = new Node<>("test", this.to, this.s3);
  }

  void testNodeConstructor(Tester t) {
    t.checkConstructorException(new IllegalArgumentException("The prev and next fields of a Node, "
            + "when called with this constructor, must be non-null."),
            "Node", "test", null, new Node<String>("asdf"));
  }
  
  void testNodeConstructorSet(Tester t) {
    init();
    t.checkExpect(this.abc.next.prev, this.abc);
    t.checkExpect(this.bcd.prev.next, this.bcd);
  }
}