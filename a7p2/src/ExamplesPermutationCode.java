import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;

class ExamplesPermutationCode {

  Random random = new Random(1);
  PermutationCode perm1 = new PermutationCode(this.random);
  ArrayList<Character> encoder = new ArrayList<>(Arrays.asList('r', 'n', 'h', 'o', 'y', 'q', 't',
          'u', 'l', 'v', 'a', 'x', 'g', 'i', 'k', 'c', 'e', 'j', 'z', 's', 'f', 'm', 'd', 'w', 'b',
          'p'));

  void testInitEncoder(Tester t) {
    t.checkExpect(this.perm1.code, this.encoder);
  }

  void testEncode(Tester t) {
    t.checkExpect(this.perm1.encode("abc"), "rnh");
    t.checkExpect(this.perm1.encode(""), "");
  }

  void testDecode(Tester t) {
    t.checkExpect(this.perm1.decode("hoy"), "cde");
    t.checkExpect(this.perm1.decode(""), "");
  }

  void testConvert(Tester t) {
    t.checkExpect(this.perm1.convert("abc", this.perm1.alphabet, this.perm1.code), "rnh");
    t.checkExpect(this.perm1.convert("wbp", this.perm1.code, this.perm1.alphabet), "xyz");
  }
}
