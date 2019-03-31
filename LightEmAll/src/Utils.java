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
}
