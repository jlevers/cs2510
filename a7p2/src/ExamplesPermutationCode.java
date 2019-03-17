import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;

class ExamplesPermutationCode {
  
  Random random = new Random(1);
  PermutationCode perm1 = new PermutationCode(this.random);
  ArrayList<String> encoder = 
      new ArrayList<>(Arrays.asList("r", "n", "h", "o" , "y", "q", "t","u", "l", "v",
          "a", "x", "g", "i", "k", "c", "e", "j", "z", "s", "f", "m", "d", "w", "b", "p"));
  
  void testInitEncoder(Tester t) {
    t.checkExpect(this.perm1.code, this.encoder);
  }
  
  void testEncode(Tester t) {
    t.checkExpect(this.perm1.encode("abc"), "rnh");
    t.checkExpect(this.perm1.encode("xyz"), "wbp");
  }
}
