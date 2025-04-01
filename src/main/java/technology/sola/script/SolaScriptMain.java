package technology.sola.script;

import technology.sola.script.error.ErrorContainer;
import technology.sola.script.interpreter.Interpreter;
import technology.sola.script.library.StandardLibrary;
import technology.sola.script.parser.Parser;
import technology.sola.script.resolver.Resolver;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * SolaScriptMain contains the entry point for running sola scripts from the command line.
 */
public class SolaScriptMain {
  private static final ScriptRuntime scriptRuntime = new ScriptRuntime();

  /**
   * Main entry point for running sola script as an executable.
   *
   * @param args commandline arguments
   * @throws IOException if there is an issue reading a file as input
   */
  public static void main(String[] args) throws IOException {
    new StandardLibrary().addToRuntime(scriptRuntime);

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

    var errorContainer = run(new String(bytes, Charset.defaultCharset()));

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

    for (; ; ) {
      System.out.print("> ");
      String line = reader.readLine();

      if (line == null) {
        break;
      }

      run(line);
    }
  }

  private static ErrorContainer run(String source) {
    var errorContainer = new ErrorContainer();
    var tokenizer = new Tokenizer(source);
    var tokenizeResult = tokenizer.tokenize();
    var parser = new Parser(tokenizeResult.tokens());
    var parserResult = parser.parse();

    errorContainer.addErrors(tokenizeResult.errors());
    errorContainer.addErrors(parserResult.errors());

    if (errorContainer.hasError()) {
      errorContainer.printErrors();
    }

    Resolver resolver = new Resolver(scriptRuntime);
    var errors = resolver.resolve(parserResult.statements());

    errorContainer.addErrors(errors);

    var interpretationErrors = new Interpreter(scriptRuntime).interpret(parserResult.statements());

    errorContainer.addErrors(interpretationErrors);

    return errorContainer;
  }
}
