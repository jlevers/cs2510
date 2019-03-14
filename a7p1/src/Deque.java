// Represents a Deque
class Deque<T> {
  Sentinel<T> header;

  Deque(Sentinel<T> header) {
    this.header = header;
  }
}

// Represents a node in a Deque
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }
}

// Represents a sentinel value in in a Deque
class Sentinel<T> extends ANode<T> {
  Sentinel(ANode<T> next, ANode<T> prev) {
    super(next, prev);
  }

  // Sets the Sentinel's prev and next values to itself
  Sentinel() {
    super(new Sentinel<>(), new Sentinel<>());
    this.next = this;
    this.prev = this;
  }
}

// Represents an item in a Deque
class Node<T> extends ANode<T> {
  T data;

  Node(ANode<T> next, ANode<T> prev, T data) {
    super(next, prev);
    this.data = data;
  }
}