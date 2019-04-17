import tester.*;
import java.util.ArrayList;
import java.util.Arrays;


// A collection of utility functions for LightEmAll
class Utils {
  // Turns a 2D ArrayList into a 1D ArrayList
  static <T> ArrayList<T> flatten(ArrayList<ArrayList<T>> toFlatten) {
    ArrayList<T> flat = new ArrayList<>();
    for (ArrayList<T> al : toFlatten) {
      flat.addAll(al);
    }

    return flat;
  }

  static int maxALInt(ArrayList<Integer> ints) {
    if (ints.size() == 0) {
      return -1;
    }

    int max = ints.get(0);
    for (int i = 1; i < ints.size(); i++) {
      max = Math.max(max, ints.get(i));
    }

    return max;
  }
}

class ExamplesUtils {
  ArrayList<ArrayList<Integer>> al = new ArrayList<>(Arrays.asList(
          new ArrayList<>(Arrays.asList(0, 1, 2)),
          new ArrayList<>(Arrays.asList(3, 4, 5)),
          new ArrayList<>(Arrays.asList(6, 7, 8))
  ));

  void testFlatten(Tester t) {
    t.checkExpect(Utils.flatten(this.al), new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7,
            8)));
  }

  void testMaxALInt(Tester t) {
    t.checkExpect(Utils.maxALInt(new ArrayList<>(Arrays.asList(3, 4, 1, 18))), 18);
    t.checkExpect(Utils.maxALInt(new ArrayList<>()), -1);
  }
}