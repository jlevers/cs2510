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

  // Determines the amount of Nodes in the Deque
  int size() {
    return this.header.size();
  }
  
  //Adds A new node with the given value to the end the front
  void addAtHead(T value) {
   ANode<T> temp = this.header.next;
   this.header.next = new Node<>(value, temp, this.header);
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

  //Determines the size of the list
  public int listSize() {
    return 0;
  }
}

// Represents a sentinel value in in a Deque
class Sentinel<T> extends ANode<T> {
  // Sets the Sentinel's prev and next values to itself
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  //Determines the number of Nodes after this sentinel
  public int size() {
    return this.next.listSize();
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
      throw new IllegalArgumentException("The prev and next fields of a Node, when called with "
          + "this constructor, must be non-null.");
    }

    this.data = data;
    this.next = next;
    this.prev = prev;
    this.next.setPrev(this);
    this.prev.setNext(this);
  }

  @Override
  //Adds this node to the size
  public int listSize() {
    return 1 + this.next.listSize();
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

    this.abc = new Node<>("abc", this.bcd, this.s2);
    this.bcd = new Node<>("bcd", this.cde, this.abc);
    this.cde = new Node<>("cde", this.def, this.bcd);
    this.def = new Node<>("def", this.s2, this.cde);

    this.only = new Node<>("only");
    this.made = new Node<>("made");
    this.to = new Node<>("to");
    this.test = new Node<>("test");

    this.only = new Node<>("only", this.made, this.s3);
    this.made = new Node<>("made", this.to, this.only);
    this.to = new Node<>("to", this.test, this.made);
    this.test = new Node<>("test", this.s3, this.to);
  }

  void testNodeConstructor(Tester t) {
    t.checkConstructorException(
        new IllegalArgumentException("The prev and next fields of a Node, "
            + "when called with this constructor, must be non-null."),
        "Node", "test", null, new Node<String>("asdf"));
  }

  void testNodeConstructorSet(Tester t) {
    init();
    t.checkExpect(this.abc.next.prev, this.abc);
    t.checkExpect(this.bcd.prev.next, this.bcd);
  }
  
  void testSize(Tester t) {
    init();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
  }
  
  void testAddBeginning(Tester t) {
    init();
    this.deque2.addAtHead("0ab");
    t.checkExpect(this.deque2.header.next, new Node<>("0ab", this.abc, this.deque2.header));
  }
}
