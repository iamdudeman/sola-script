package technology.sola.script.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
  @Nested
  class stmtExpression {
    @Nested
    class exprLogicOr {
      @Test
      void valid() {
        var source = """
          12.32 || 5;
          """;
        var expected = """
          12.32 || 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprLogicAnd {
      @Test
      void valid() {
        var source = """
          12.32 && 5;
          """;
        var expected = """
          12.32 && 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprEquality {
      @Test
      void valid() {
        var source = """
          12.32 == 5;
          12.32 != 5;
          """;
        var expected = """
          12.32 == 5
          12.32 != 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprComparison {
      @Test
      void valid() {
        var source = """
          12.32 > 5;
          12.32 < 5;
          12.32 >= 5;
          12.32 <= 5;
          """;
        var expected = """
          12.32 > 5
          12.32 < 5
          12.32 >= 5
          12.32 <= 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprTerm {
      @Test
      void valid() {
        var source = """
          12.32 + 5;
          12.32 - 5;
          """;
        var expected = """
          12.32 + 5
          12.32 - 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprFactor {
      @Test
      void valid() {
        var source = """
          12.32 * 5;
          12.32 / 5;
          """;
        var expected = """
          12.32 * 5
          12.32 / 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprUnary {
      @Test
      void valid() {
        var source = """
          -12.32;
          !false;
          """;
        var expected = """
          -12.32
          !false
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }
    }

    @Nested
    class exprCall {
      @Test
      void valid() {
        var source = """
          methodCall();
          methodCallArgs("test", 5);
          objectGetter.someValue;
          """;
        var expected = """
          methodCall()
          methodCallArgs(test, 5)
          objectGetter.someValue
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }

      @Test
      void invalid() {
        var source = """
          invalidMethodCall(test;
          invalidGetter.;
          """;
        var result = visualizeScriptParsing(source);

        assertEquals(2, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.PARSE, error.type());
        assertEquals("Expect ')' after arguments.", error.message());

        error = result.errors.get(1);
        assertEquals(ScriptErrorType.PARSE, error.type());
        assertEquals("Expect property name after '.'.", error.message());
      }

      @Test
      void invalidArgumentCount() {
        var arguments = new String[256];
        Arrays.fill(arguments, "test");
        var source = """
          methodCall(%s);
          """.formatted(Arrays.stream(arguments).map(Object::toString).collect(Collectors.joining(",")));
        var result = visualizeScriptParsing(source);

        assertEquals(1, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.SEMANTIC, error.type());
        assertEquals("Can't have more than 255 arguments.", error.message());
      }
    }

    @Nested
    class exprPrimary {
      @Test
      void valid() {
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
