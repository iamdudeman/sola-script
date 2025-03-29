package technology.sola.script.interpreter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Parser;
import technology.sola.script.parser.Stmt;
import technology.sola.script.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.*;

// todo test all the things that are implemented

class ExpressionInterpreterTest {
  @Nested
  class set {
    // todo not yet implemented to test
  }

  @Nested
  class assign {
    // todo not yet implemented to test
  }

  @Nested
  class logical {
    @Nested
    class or {
      @Test
      void whenLeftTruthy_shouldReturnLeft() {
        assertEvaluation("true || 5;", true);
        assertEvaluation("5 || true;", 5d);
      }

      @Test
      void whenLeftNotTruthy_shouldReturnRight() {
        assertEvaluation("false || 5;", 5d);
        assertEvaluation("null || true;", true);
      }
    }

    @Nested
    class and {
      @Test
      void whenLeftNotTruthy_shouldReturnLeft() {
        assertEvaluation("false && 5;", false);
        assertEvaluation("null && true;", null);
      }

      @Test
      void whenLeftTruthy_shouldReturnRight() {
        assertEvaluation("true && 5;", 5d);
        assertEvaluation("5 && true;", true);
      }
    }
  }

  @Nested
  class binary {
    @Nested
    class greater {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true > 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 > 3;", true);
        assertEvaluation("3 > 5;", false);
        assertEvaluation("5 > 5;", false);
      }
    }

    @Nested
    class greaterEqual {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true >= 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 >= 3;", true);
        assertEvaluation("3 >= 5;", false);
        assertEvaluation("5 >= 5;", true);
      }
    }

    @Nested
    class less {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true < 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 < 3;", false);
        assertEvaluation("3 < 5;", true);
        assertEvaluation("5 < 5;", false);
      }
    }

    @Nested
    class lessEqual {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true <= 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 <= 3;", false);
        assertEvaluation("3 <= 5;", true);
        assertEvaluation("5 <= 5;", true);
      }
    }

    @Nested
    class equalEqual {
      @Test
      void test() {
        assertEvaluation("null == null;", true);
        assertEvaluation("5 == 5;", true);
        assertEvaluation("\"test\" == \"test\";", true);

        assertEvaluation("null == 5;", false);
        assertEvaluation("5 == 3;", false);
        assertEvaluation("\"test\" == \"test2\";", false);
      }
    }

    @Nested
    class bangEqual {
      @Test
      void test() {
        assertEvaluation("null != null;", false);
        assertEvaluation("5 != 5;", false);
        assertEvaluation("\"test\" != \"test\";", false);

        assertEvaluation("null != 5;", true);
        assertEvaluation("5 != 3;", true);
        assertEvaluation("\"test\" != \"test2\";", true);
      }
    }

    // todo other cases

    // todo star case
  }

  @Nested
  class unary {
    @Test
    void bang() {
      assertEvaluation("!true;", false);
      assertEvaluation("!false;", true);
    }

    @Test
    void minus_whenNotNumber_shouldThrow() {
      assertThrows(
        ScriptInterpretationException.class,
        () -> evaluateExpressionStatementSource("-false;")
      );
    }

    @Test
    void minus() {
      assertEvaluation("-5;", -5d);
      assertEvaluation("--5;", 5d);
    }
  }

  @Nested
  class call {
    // todo not yet implemented to test
  }

  @Nested
  class get {
    // todo not yet implemented to test
  }

  @Nested
  class thisVisit {
    // todo not yet implemented to test
  }

  @Nested
  class superVisit {
    // todo not yet implemented to test
  }

  @Nested
  class variable {
    // todo not yet implemented to test
  }

  @Nested
  class grouping {
    @Test
    void test() {
      assertEvaluation("( 3 );", 3d);
    }
  }

  @Nested
  class literal {
    @Test
    void test() {
      assertEvaluation("3;", 3d);
    }
  }

  private void assertEvaluation(String source, Object expectedValue) {
    var result = evaluateExpressionStatementSource(source);

    assertEquals(expectedValue, result);
  }

  private Object evaluateExpressionStatementSource(String source) {
    var tokenizer = new Tokenizer(source);
    var parser = new Parser(tokenizer.tokenize().tokens());
    var expressionInterpreter = new ExpressionInterpreter();
    var statements = parser.parse().statements();

    if (statements.size() != 1) {
      fail("Expecting exactly one statement for the test.");
      return null;
    }

    if (statements.get(0) instanceof Stmt.Expression statement) {
      return expressionInterpreter.evaluate(statement.expr());
    } else {
      fail("Test source code is not an expression statement.");
      return null;
    }
  }
}
