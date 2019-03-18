import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * A class that defines a new permutation code, as well as methods for encoding
 * and decoding of the messages that use this code.
 */
class PermutationCode {
  // The original list of characters to be encoded
  ArrayList<Character> alphabet = new ArrayList<>(
      Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

  // The encoded alphabet: the 1-string at index 0 is the encoding of 'a',
  // the 1-string at index 1 is the encoding of 'b', etc.
  ArrayList<Character> code;

  // A random number generator
  Random rand;

  // Create a new random instance of the encoder/decoder with a new permutation
  // code
  PermutationCode() {
    this(new Random());
  }

  // Create a particular random instance of the encoder/decoder
  PermutationCode(Random r) {
    this.rand = r;
    this.code = this.initEncoder();
  }

  // Create a new instance of the encoder/decoder with the given code
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
    this.rand = new Random(); // won't be used, but best to not leave fields null
  }

  // Initialize the encoding permutation of the characters
  ArrayList<Character> initEncoder() {
    ArrayList<Character> cloned = new ArrayList<>(this.alphabet);
    ArrayList<Character> encoder = new ArrayList<>(26);
    for (int i = cloned.size(); i > 0; i--) {
      int random = this.rand.nextInt(cloned.size());
      char removed = cloned.remove(random);
      encoder.add(removed);
    }
    return encoder;
  }

  // produce an encoded String from the given String
  // You can assume the given string consists only of lowercase characters
  String encode(String source) {
    return this.convert(source, this.alphabet, this.code);
  }

  // produce a decoded String from the given String
  // You can assume the given string consists only of lowercase characters
  String decode(String code) {
    return this.convert(code, this.code, this.alphabet);
  }

  // Converts the given String using two coded ArrayLists
  String convert(String s, ArrayList<Character> original, ArrayList<Character> converter) {
    String converted = "";

    for (int i = 0; i < s.length(); i++) {
      char letter = s.charAt(i);
      int index = original.indexOf(letter);
      converted += converter.get(index);
    }
    return converted;
  }
}
