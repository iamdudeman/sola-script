package technology.sola.script;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;

@NullMarked
public class TestProgramSolaScript {
  public static void main(String[] args) throws IOException {
    String[] files = new String[] {
      "variables-and-blocks",
      "if",
      "while",
      "function",
      "map"
    };

    for (var file : files) {
      runFile(file);
    }
  }

  private static void runFile(String filename) throws IOException {
    System.out.println("--" + filename + "--");
    SolaScriptMain.main(new String[] {
      "test_programs/sola/" + filename + ".sola",
    });
    System.out.println();
  }
}
