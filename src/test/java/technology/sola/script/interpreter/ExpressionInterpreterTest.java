package technology.sola.script.interpreter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.parser.Parser;
import technology.sola.script.parser.Stmt;
import technology.sola.script.tokenizer.Tokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
    // todo
  }

  @Nested
  class binary {
    // todo
  }

  @Nested
  class unary {
    // todo
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
      var source = """
      ( 3 ) ;
      """;
      var result = evaluateExpressionStatementSource(source);

      assertEquals(3d, result);
    }
  }

  @Nested
  class literal {
    @Test
    void test() {
      var source = """
      3 ;
      """;
      var result = evaluateExpressionStatementSource(source);

      assertEquals(3d, result);
    }
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
