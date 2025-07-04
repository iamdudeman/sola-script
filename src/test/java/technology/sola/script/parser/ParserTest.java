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
  class declFun {
    @Test
    void valid() {
      var source = """
        fun greeting(name, test) {
          name;
        }
        """;
      var expected = """
        fun greeting(name,test) {
        name
        }
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        fun ;
        fun name ;
        fun name( ;
        fun name(test, test2 ;
        fun name(test, test2) ;
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_NAME)
        .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_FUNCTION_NAME)
        .withErrors(ScriptErrorType.EXPECT_NAME)
        .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_PARAMETERS)
        .withErrors(ScriptErrorType.EXPECT_BRACE_BEFORE_FUNCTION_BODY)
        .verify(null);
    }

    @Test
    void invalid_tooManyArguments() {
      var arguments = new String[256];
      Arrays.fill(arguments, "test");
      var source = """
          fun tooMany(%s) {

          }
          """.formatted(Arrays.stream(arguments).map(Object::toString).collect(Collectors.joining(",")));

      new ParserTester(source)
        .withErrors(ScriptErrorType.TOO_MANY_ARGUMENTS)
        .verify(null);
    }
  }

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

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_NAME)
        .withErrors(ScriptErrorType.EXPECT_SEMI_AFTER_VARIABLE_DECLARATION)
        .verify(null);
    }
  }

  @Nested
  class declVal {
    @Test
    void valid() {
      var source = """
        val test = 5;
        """;
      var expected = """
        val test = 5
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        val ;
        val test;
        val test =;
        val test = 5
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_NAME)
        .withErrors(ScriptErrorType.EXPECT_INITIALIZER_EXPRESSION)
        .withErrors(ScriptErrorType.EXPECT_EXPRESSION)
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
    class exprTernary {
      @Test
      void valid() {
        var source = """
          test ? 5 : 4;
          """;
        var expected = """
          test ? 5 : 4
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          test ? 5 : ;
          test ? 5 ;
          test ? ;
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.EXPECT_EXPRESSION, ScriptErrorType.EXPECT_COLON_AFTER_TERNARY_TRUE_EXPR, ScriptErrorType.EXPECT_EXPRESSION)
          .verify("");
      }
    }

    @Nested
    class exprNullishCoalescence {
      @Test
      void valid() {
        var source = """
          12.32 ?? 5;
          """;
        var expected = """
          12.32 ?? 5
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          2 ?? 5;
          ?? 5;
          """;
        var expected = """
          2 ?? 5
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.INVALID_BINARY_EXPRESSION)
          .verify(expected);
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
          functionCall();
          functionCallArgs("test", 5);
          objectGetter.someValue;
          objectGetter?.someValue;
          functionCallArgs?.("test", 5);
          """;
        var expected = """
          functionCall()
          functionCallArgs(test, 5)
          objectGetter.someValue
          objectGetter?.someValue
          functionCallArgs?.(test, 5)
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          invalidFunctionCall(test;
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
          functionCall(%s);
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
          var test = {};
          """;
        var expected = """
          false
          true
          null
          string
          12.37
          (true)
          testVar
          var test = {}
          """;

        new ParserTester(source)
          .verify(expected);
      }

      @Test
      void invalid() {
        var source = """
          (1 + 1 ;
          test = { ;
          false
          """;

        new ParserTester(source)
          .withErrors(ScriptErrorType.EXPECT_PAREN_AFTER_EXPRESSION)
          .withErrors(ScriptErrorType.EXPECT_BRACE_AFTER_MAP_CREATION)
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
  class stmtReturn {
    @Test
    void valid() {
      var source = """
        return;
        return true;
        """;
      var expected = """
        return
        return true
        """;

      new ParserTester(source)
        .verify(expected);
    }

    @Test
    void invalid() {
      var source = """
        return true
        """;

      new ParserTester(source)
        .withErrors(ScriptErrorType.EXPECT_SEMI_AFTER_RETURN_VALUE)
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
