package technology.sola.script;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * Small program for running test sola scripts.
 */
@NullMarked
public class TestProgramSolaScript {
  /**
   * Main method for running test sola scripts.
   *
   * @param args command line args
   * @throws IOException if a test program file fails to load
   */
  public static void main(String[] args) throws IOException {
    String[] files = new String[]{
      "variables-and-blocks",
      "if",
      "while",
      "function",
      "map",
      "nullish"
    };

    for (var file : files) {
      runFile(file);
    }
  }

  private static void runFile(String filename) throws IOException {
    System.out.println("--" + filename + "--");
    SolaScriptMain.main(new String[]{
      "test_programs/sola/" + filename + ".sola",
    });
    System.out.println();
  }
}
