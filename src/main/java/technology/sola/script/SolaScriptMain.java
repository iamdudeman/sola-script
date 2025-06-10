package technology.sola.script;

import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * SolaScriptMain contains the entry point for running sola scripts from the command line.
 */
@NullMarked
public class SolaScriptMain {
  private static final SolaScript solaScript = new SolaScript();

  /**
   * Main entry point for running sola script as an executable.
   *
   * @param args commandline arguments
   * @throws IOException if there is an issue reading a file as input
   */
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: sola <file>");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String fileName) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(fileName));

    var errorContainer = solaScript.execute(new String(bytes, Charset.defaultCharset()));

    if (errorContainer.hasError()) {
      errorContainer.printErrors();
      System.exit(65);
    }
    if (errorContainer.hasRuntimeError()) {
      errorContainer.printErrors();
      System.exit(70);
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in, Charset.defaultCharset());
    BufferedReader reader = new BufferedReader(input);

    for ( ; ; ) {
      System.out.print("> ");
      String line = reader.readLine();

      if (line == null) {
        break;
      }

      var errorContainer = solaScript.execute(line);

      if (errorContainer.hasError()) {
        errorContainer.printErrors();
      }
    }
  }
}
