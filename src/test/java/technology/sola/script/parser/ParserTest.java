package technology.sola.script.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptError;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

// todo update error check stuff


class ParserTest {
  @Nested
  class stmtExpression {
    @Nested
    class exprAssignment {
      @Test
      void valid() {
        var source = """
          test = 5;
          someObj.test = 5;
          """;
        var expected = """
          test = 5
          someObj.test = 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(0, result.errors.size());
        assertEquals(expected, result.parsedScript);
      }

      @Test
      void invalid() {
        var source = """
          4 = 5;
          = 5;
          """;
        var result = visualizeScriptParsing(source);

        assertEquals(2, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_ASSIGNMENT_TARGET, error.type());

        error = result.errors.get(1);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());
      }
    }

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

      @Test
      void invalid() {
        var source = """
          2 || 5;
          || 5;
          """;
        var expected = """
          2 || 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(1, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

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

      @Test
      void invalid() {
        var source = """
          2 && 5;
          && 5;
          """;
        var expected = """
          2 && 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(1, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

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

      @Test
      void invalid() {
        var source = """
          2 == 5;
          == 5;
          != 5;
          5 != 2;
          """;
        var expected = """
          2 == 5
          5 != 2
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(2, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

        error = result.errors.get(1);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

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

      @Test
      void invalid() {
        var source = """
          2 > 5;
          2 < 5;
          > 5;
          < 5;
          5 >= 2;
          5 <= 2;
          >= 5;
          <= 5;
          """;
        var expected = """
          2 > 5
          2 < 5
          5 >= 2
          5 <= 2
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(4, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

        error = result.errors.get(1);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

        error = result.errors.get(2);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

        error = result.errors.get(3);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

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

      @Test
      void invalid() {
        var source = """
          2 + 5;
          + 5;
          """;
        var expected = """
          2 + 5
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(1, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

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

      @Test
      void invalid() {
        var source = """
          2 * 5;
          * 5;
          / 5;
          5 / 2;
          """;
        var expected = """
          2 * 5
          5 / 2
          """.trim();
        var result = visualizeScriptParsing(source);

        assertEquals(2, result.errors.size());
        var error = result.errors.get(0);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

        error = result.errors.get(1);
        assertEquals(ScriptErrorType.INVALID_BINARY_EXPRESSION, error.type());

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
        assertEquals(ScriptErrorType.EXPECT_PAREN_AFTER_ARGUMENTS, error.type());

        error = result.errors.get(1);
        assertEquals(ScriptErrorType.EXPECT_PROPERTY_NAME_AFTER_DOT, error.type());
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
        assertEquals(ScriptErrorType.TOO_MANY_ARGUMENTS, error.type());
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
        assertEquals(ScriptErrorType.EXPECT_SEMI_AFTER_EXPRESSION, error.type());
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
