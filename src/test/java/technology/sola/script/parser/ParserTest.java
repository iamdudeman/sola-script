package technology.sola.script.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
  @Nested
  class stmtExpression {
    @Test
    void invalid() {
      var source = """
      false
      """;

      var result = visualizeScriptParsing(source);

      assertEquals(1, result.errors.size());
      var error = result.errors.get(0);
      assertEquals(ScriptErrorType.PARSE, error.type());
      assertEquals("Expect ';' after expression.", error.message());
    }

    @Test
    void validPrimary() {
      var source = """
      false;
      true;
      null;
      "string";
      12.37;
      ( true );
      testVar;
      this;
      super.someMethod;
      """;
      var expected = """
      false
      true
      null
      string
      12.37
      (true)
      testVar
      this
      super.someMethod
      """.trim();
      var result = visualizeScriptParsing(source);

      assertEquals(0, result.errors.size());
      assertEquals(expected, result.parsedScript);
    }
  }

  private TestResult visualizeScriptParsing(String source) {
    var tokenizer = new Tokenizer(source);
    var parser = new Parser(tokenizer.tokenize().tokens());
    var parserResultPrinter = new ParserResultPrinter();
    var parserResult = parser.parse();
    var parsedScript = parserResultPrinter.print(parserResult);

    return new TestResult(
      parsedScript,
      parserResult.errors()
    );
  }

  private record TestResult(String parsedScript, List<ScriptError> errors) {
  }
}
