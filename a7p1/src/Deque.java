import tester.*;

// Represents a boolean-valued question over values of type T
interface IPred<T> {
  boolean apply(T t);
}

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

  // EFFECT: Adds a new node with the given value to the front of the list
  void addAtHead(T value) {
    this.header.append(value, 0);
  }

  // EFFECT: Adds a new node with the given value to the end of the list
  void addAtTail(T value) {
    this.header.append(value, this.size());
  }

  // Removes the first node from the deck and returns its value
  T removeFromHead() {
    if (this.header.isEmpty()) {
      throw new RuntimeException("Cannot remove an item from an Empty Deque");
    }
    return this.header.remove(1);
  }

  // Removes the last node from the deck and returns its value
  T removeFromTail() {
    if (this.header.isEmpty()) {
      throw new RuntimeException("Cannot remove an item from an Empty Deque");
    }
    return this.header.remove(this.size());
  }

  // Returns the ANode whose data matches the given IPred
  ANode<T> find(IPred<T> pred) {
    return this.header.find(pred, false);
  }

  // EFFECT: Removes the given node from the Deque, if it exists
  void removeNode(ANode<T> node) {
    if (!node.isSentinal()) {
      node.remove(0);
    }
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

  // EFFECT: removes the ANode at the given index from a Deque
  abstract T remove(int index);

  // Determines the size of the list
  int listSize() {
    return 0;
  }

  // Returns true if the ANode is a Sentinel
  boolean isSentinal() {
    return false;
  }

  // EFFECT: Creates a node with the given value at the given index
  void append(T value, int index) {
    if (index == 0) {
      ANode temp = this.next;
      this.next = new Node<>(value, temp, this);
    } else {
      this.next.append(value, index - 1);
    }
  }

  // Returns the ANode whose data matches the given pred
  ANode<T> find(IPred<T> pred, boolean checkedSentinel) {
    if (!checkedSentinel) {
      return this.next.find(pred, true);
    }
    return this;
  }
}

// Represents a sentinel value in in a Deque
class Sentinel<T> extends ANode<T> {
  // Sets the Sentinel's prev and next values to itself
  Sentinel() {
    this.setNext(this);
    this.setPrev(this);
  }

  // Checks if this Sentinel refers to itself
  boolean isEmpty() {
    return this.next.isSentinal() && this.prev.isSentinal();
  }

  // Determines the number of Nodes after this sentinel
  int size() {
    return this.next.listSize();
  }

  // Always returns true because this ANode is a Sentinel
  boolean isSentinal() {
    return true;
  }

  // If trying to remove the Sentinel, throw an error, otherwise recur down the Deque
  T remove(int index) {
    if (index == 0) {
      throw new RuntimeException("Sentinels do not have data to access");
    } else {
      return this.next.remove(index - 1);
    }
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

  // Adds this node to the size
  int listSize() {
    return 1 + this.next.listSize();
  }

  // Removes the node at the given index, and returns its data
  T remove(int index) {
    if (index == 0) {
      this.next.setPrev(this.prev);
      this.prev.setNext(this.next);
      return this.data;
    } else {
      return this.next.remove(index - 1);
    }
  }

  // Finds the Node whose data matches the given IPred
  ANode<T> find(IPred<T> pred, boolean checkedSentinel) {
    if (pred.apply(this.data)) {
      return this;
    }
    return this.next.find(pred, checkedSentinel);
  }
}

class ExamplesDeque {
  Deque<String> deque1;
  Deque<String> deque2;
  Deque<String> deque3;
  Sentinel<String> s1;
  Sentinel<String> s2;
  Sentinel<String> s3;
  ANode<String> abc;
  ANode<String> bcd;
  ANode<String> cde;
  ANode<String> def;
  ANode<String> only;
  ANode<String> made;
  ANode<String> to;
  ANode<String> test;

  class IsABC implements IPred<String> {
    public boolean apply(String s) {
      return s.equals("abc");
    }
  }

  class IsEqual implements IPred<String> {
    String comp;

    IsEqual(String comp) {
      this.comp = comp;
    }

    public boolean apply(String s) {
      return s.equals(comp);
    }
  }

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

  void testListSize(Tester t) {
    init();
    t.checkExpect(this.s1.listSize(), 0);
    t.checkExpect(this.def.listSize(), 1);
    t.checkExpect(this.cde.listSize(), 2);
  }

  void testAddAtHead(Tester t) {
    init();
    this.deque2.addAtHead("0ab");
    t.checkExpect(this.deque2.header.next, new Node<>("0ab", this.abc, this.deque2.header));
  }

  void testAddAtTail(Tester t) {
    init();
    this.deque2.addAtTail("efg");
    t.checkExpect(this.deque2.header.prev, new Node<>("efg", this.deque2.header, this.def));
  }

  void testRemoveFromHead(Tester t) {
    init();
    t.checkException(new RuntimeException("Cannot remove an item from an Empty Deque"),
            this.deque1, "removeFromHead");
    t.checkExpect(this.deque2.removeFromHead(), "abc");
    t.checkExpect(this.deque2.header.next, this.bcd);
    t.checkExpect(this.bcd.prev, this.deque2.header);
  }

  void testRemoveFromTail(Tester t) {
    init();
    t.checkException(new RuntimeException("Cannot remove an item from an Empty Deque"), this.deque1,
            "removeFromTail");
    t.checkExpect(this.deque2.removeFromTail(), "def");
    t.checkExpect(this.deque2.header.prev, this.cde);
    t.checkExpect(this.cde.next, this.deque2.header);
  }

  void testIsEmpty(Tester t) {
    init();
    t.checkExpect(this.deque1.header.isEmpty(), true);
    t.checkExpect(this.deque2.header.isEmpty(), false);
  }

  void testIsSentinal(Tester t) {
    init();
    t.checkExpect(this.s1.isSentinal(), true);
    t.checkExpect(this.abc.isSentinal(), false);
  }

  void testAppend(Tester t) {
    init();
    this.deque1.header.append("test", 0);
    t.checkExpect(this.deque1.header.next, new Node<>("test", this.deque1.header,
            this.deque1.header));
    this.deque2.header.append("test", 3);
    t.checkExpect(this.deque2.header.next.next.next.next, new Node<>("test", this.def, this.cde));
  }

  void testRemove(Tester t) {
    init();
    t.checkException(new RuntimeException("Sentinels do not have data to access"),
            this.deque1.header, "remove", 0);
    t.checkExpect(this.deque2.header.remove(2), "bcd");
    t.checkExpect(this.deque2.header.next.next, new Node<>("cde", this.def, this.abc));
    t.checkExpect(this.deque2.header.next, new Node<>("abc", this.cde, this.s2));
  }

  void testFind(Tester t) {
    init();
    t.checkExpect(this.deque1.find(new IsABC()), this.s1);
    t.checkExpect(this.deque2.find(new IsABC()), this.abc);
    t.checkExpect(this.deque3.find(new IsABC()), this.s3);
    t.checkExpect(this.deque3.find(new IsEqual("made")), this.made);
  }

  void testFindANode(Tester t) {
    init();
    t.checkExpect(this.s1.find(new IsABC(), false), this.s1);
    t.checkExpect(this.s1.find(new IsABC(), true), this.s1);
    t.checkExpect(this.s2.find(new IsABC(), false), this.abc);
    t.checkExpect(this.s2.find(new IsABC(), true), this.s2);
    t.checkExpect(this.bcd.find(new IsABC(), true), this.s2);
    t.checkExpect(this.s3.find(new IsEqual("to"), false), this.to);
  }

  void testRemoveNode(Tester t) {
    init();
    this.deque1.removeNode(this.s1);
    t.checkExpect(this.deque1.header, this.s1);
    this.deque2.removeNode(this.bcd);
    t.checkExpect(this.deque2.header.next, new Node<>("abc", this.cde, this.s2));
    t.checkExpect(this.deque2.header.next.next, new Node<>("cde", this.def, this.abc));
  }
}
