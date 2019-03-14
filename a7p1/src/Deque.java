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

  Node(T data, ANode<T> next, ANode<T> prev) {
    if (this.next == null || this.prev == null) {
      throw new IllegalArgumentException("The prev and next fields of a Node, when called with " +
              "this constructor, must be non-null.");
    }
    this.next = next;
    this.prev = prev;
    this.data = data;
  }
}