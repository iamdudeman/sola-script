package technology.sola.script.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
  @Nested
  class declVar {
    @Test
    void valid() {
      var source = """
        var test = 5;
        var test2;
        """;
      var expected = """
        var test = 5
        var test2
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        var ;
        var test
        """;
      var expected = """
        var test = 5
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_VARIABLE_NAME)
        .withErrors(ScriptErrorType.EXPECT_SEMI_AFTER_VARIABLE_DECLARATION)
        .verify(null);
    }
  }

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
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          4 = 5;
          = 5;
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_ASSIGNMENT_TARGET, ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify("4");
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
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          2 || 5;
          || 5;
          """;
        var expected = """
          2 || 5
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          2 && 5;
          && 5;
          """;
        var expected = """
          2 && 5
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_BINARY_EXPRESSION, ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .withErrors(
            ScriptErrorType.INVALID_BINARY_EXPRESSION, ScriptErrorType.INVALID_BINARY_EXPRESSION,
            ScriptErrorType.INVALID_BINARY_EXPRESSION, ScriptErrorType.INVALID_BINARY_EXPRESSION
          )
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          2 + 5;
          + 5;
          """;
        var expected = """
          2 + 5
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_BINARY_EXPRESSION, ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
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
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          invalidMethodCall(test;
          invalidGetter.;
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_ARGUMENTS, ScriptErrorType.EXPECT_PROPERTY_NAME_AFTER_DOT)
          .verify("");
      }

      @Test
      void invalidArgumentCount() {
        var arguments = new String[256];
        Arrays.fill(arguments, "test");
        var source = """
          methodCall(%s);
          """.formatted(Arrays.stream(arguments).map(Object::toString).collect(Collectors.joining(",")));

        new ParserTester(source)
          .withErrors(ScriptErrorType.TOO_MANY_ARGUMENTS)
          .verify(null);
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
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          false
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.EXPECT_SEMI_AFTER_EXPRESSION)
          .verify("");
      }
    }
  }

  @Nested
  class stmtIf {
    @Test
    void valid() {
      var source = """
        if (true) {

        } else {

        }
        """;
      var expected = """
        if (true) {}
        else {}
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        if ;
        if (true;
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_IF)
        .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_CONDITION)
        .verify(null);
    }
  }

  @Nested
  class stmtWhile {
    @Test
    void valid() {
      var source = """
        while (true) {

        }
        """;
      var expected = """
        while (true) {}
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        while ;
        while (true;
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_WHILE)
        .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_CONDITION)
        .verify(null);
    }
  }

  @Nested
  class block {
    @Test
    void valid() {
      var source = """
        {}
        { test; }
        """;
      var expected = """
        {}
        {
          test
        }
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        {
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_BRACE_AFTER_BLOCK)
        .verify(null);
    }
  }

  private static class ParserTester {
    private final String source;
    private final List<ScriptErrorType> expectedErrors = new ArrayList<>();

    ParserTester(String source) {
      this.source = source;
    }

    ParserTester withErrors(ScriptErrorType... expectedErrors) {
      if (expectedErrors != null) {
        this.expectedErrors.addAll(Arrays.asList(expectedErrors));
      }

      return this;
    }

    void verify(String expectedVisualization) {
      var tokenizer = new Tokenizer(source);
      var parser = new Parser(tokenizer.tokenize().tokens());
      var parserResultPrinter = new ParserResultPrinter();
      var parserResult = parser.parse();
      var parsedScript = parserResultPrinter.print(parserResult);
      var errors = parserResult.errors();

      assertEquals(expectedErrors.size(), errors.size(), "Expected errors are present or missing.");

      for (int i = 0; i < errors.size(); i++) {
        var error = errors.get(i);

        assertEquals(expectedErrors.get(i), error.type(), "Expected error at position " + i + " was wrong.");
      }

      if (expectedVisualization != null) {
        assertEquals(expectedVisualization.trim(), parsedScript);
      }
    }
  }
}
