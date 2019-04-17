import java.util.ArrayList;
import java.util.Arrays;

import tester.*;

// Represents a FIFO data structure
class Queue<T> {
  ArrayList<T> queue;

  Queue(ArrayList<T> queue) {
    this.queue = queue;
  }

  // Adds an item to the back of the Queue
  void push(T item) {
    queue.add(item);
  }

  // Adds the given list of items to the back of the Queue
  void pushAll(ArrayList<T> items) {
    for (T t : items) {
      this.push(t);
    }
  }

  // Pops an item from the front of the Queue, if the Queue isn't empty
  T pop() {
    if (queue.size() > 0) {
      return queue.remove(0);
    }

    return null;
  }

  // Gets the size of the Queue
  int size() {
    return this.queue.size();
  }
}

class ExamplesQueue {
  Queue<Integer> q1;

  void init() {
    this.q1 = new Queue(new ArrayList<>(Arrays.asList(4, 12, 1, 7, 44)));
  }

  void testPush(Tester t) {
    init();
    this.q1.push(87);
    t.checkExpect(this.q1.queue.get(5), 87);
    this.q1.push(-4);
    t.checkExpect(this.q1.queue.get(6), -4);
  }

  void testPushAll(Tester t) {
    init();
    this.q1.pushAll(new ArrayList<>(Arrays.asList(18, -3, 0)));
    t.checkExpect(this.q1.queue, new ArrayList<>(Arrays.asList(4, 12, 1, 7, 44, 18, -3, 0)));
  }

  void testPop(Tester t) {
    init();
    t.checkExpect(this.q1.pop(), 4);
    t.checkExpect(this.q1.pop(), 12);
  }

  void testSize(Tester t) {
    init();
    t.checkExpect(this.q1.size(), 5);
    this.q1.pop();
    t.checkExpect(this.q1.size(), 4);
  }
}