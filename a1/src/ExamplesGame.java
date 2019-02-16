interface IResource {
}

interface IAction {
}

class Denial implements IResource {
  String subject;
  int believability;

  public Denial(String subject, int believability) {
    this.subject = subject;
    this.believability = believability;
  }
}

class Bribe implements IResource {
  String target;
  int value;

  public Bribe(String target, int value) {
    this.target = target;
    this.value = value;
  }
}

class Apology implements IResource {
  String excuse;
  boolean reusable;

  public Apology(String excuse, boolean reusable) {
    this.excuse = excuse;
    this.reusable = reusable;
  }
}

class Purchase implements IAction {
  int cost;
  IResource item;

  public Purchase(int cost, IResource item) {
    this.cost = cost;
    this.item = item;
  }
}

class Swap implements IAction {
  IResource consumed;
  IResource received;

  public Swap(IResource consumed, IResource received) {
    this.consumed = consumed;
    this.received = received;
  }
}

class ExamplesGame {
  IResource iDidntKnow = new Denial("knowledge", 51);
  IResource witness = new Bribe("innocent witness", 49);
  IResource iWontDoItAgain = new Apology("I won't do it again", false);
  IResource itWasntMe = new Denial("blame Bob", 12);
  IResource congress = new Bribe("no comment", 14);
  IResource yeahRight = new Apology("Seriously, I won't do it again", true);

  IAction purchase1 = new Purchase(100, this.iDidntKnow);
  IAction swap1 = new Swap(this.witness, this.iDidntKnow);
  IAction purchase2 = new Purchase(45, this.congress);
  IAction swap2 = new Swap(this.itWasntMe, this.congress);
}
