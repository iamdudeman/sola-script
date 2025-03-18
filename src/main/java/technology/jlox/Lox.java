package technology.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  static boolean hadError = false;
  static boolean hadRuntimeError = false;
  private static final Interpreter interpreter = new Interpreter();

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: lox <file>");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void error(Token token, String message) {
    if (token.type() == TokenType.EOF) {
      report(token.line(), " at end", message);
    } else {
      report(token.line(), " at '" + token.lexeme() + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() + "\n[line " + error.token.line() + "]");
    hadRuntimeError = true;
  }

  private static void runFile(String fileName) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(fileName));

    run(new String(bytes, Charset.defaultCharset()));

    if (hadError) {
      System.exit(65);
    }
    if (hadRuntimeError) {
      System.exit(70);
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in, Charset.defaultCharset());
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();

      if (line == null) {
        break;
      }

      run(line);
      hadError = false;
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    if (hadError) {
      return;
    }

    interpreter.interpret(statements);
  }

  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }
}
