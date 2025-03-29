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
    @Test
    void or_whenLeftTruthy_shouldReturnLeft() {
      assertEvaluation("true || 5;", true);
      assertEvaluation("5 || true;", 5d);
    }

    @Test
    void or_whenLeftNotTruthy_shouldReturnRight() {
      assertEvaluation("false || 5;", 5d);
      assertEvaluation("null || true;", true);
    }

    @Test
    void and_whenLeftNotTruthy_shouldReturnLeft() {
      assertEvaluation("false && 5;", false);
      assertEvaluation("null && true;", null);
    }

    @Test
    void and_whenLeftTruthy_shouldReturnRight() {
      assertEvaluation("true && 5;", 5d);
      assertEvaluation("5 && true;", true);
    }
  }

  @Nested
  class binary {
    // todo
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
