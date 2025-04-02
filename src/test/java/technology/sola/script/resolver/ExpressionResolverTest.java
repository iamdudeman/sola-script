package technology.sola.script.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.parser.Expr;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionResolverTest {
  private ScriptRuntime scriptRuntime;
  private List<ScriptError> errors;


  @BeforeEach
  void setup() {
    scriptRuntime = new ScriptRuntime();
    errors = new ArrayList<>();
  }

  @Nested
  class set {
    // todo not yet implemented to test
  }

  @Nested
  class assign {
    @Test
    void test() {
      var target = new Token(TokenType.IDENTIFIER, "target", null, 1, 1);

      scriptRuntime.scopes().beginScope();
      scriptRuntime.scopes().define(target);

      var variableExpr = initializeTestVariableExpression();
      var expr = new Expr.Assign(target, variableExpr);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.assign(expr);

      assertEquals(0, errors.size());
      assertTestVariableExpression(variableExpr, 0);
      assertTestVariableExpression(expr, 1);
    }
  }

  @Nested
  class logical {
    @Test
    void test() {
      var variableExpr = initializeTestVariableExpression();
      var variableExpr2 = initializeTestVariableExpression("rightTest");
      var expr = new Expr.Logical(variableExpr, new Token(TokenType.AMP_AMP, "&&", null, 1, 1), variableExpr2);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.logical(expr);

      assertEquals(0, errors.size());
      assertTestVariableExpression(variableExpr, 1);
      assertTestVariableExpression(variableExpr2);
    }
  }

  @Nested
  class binary {
    @Test
    void test() {
      var variableExpr = initializeTestVariableExpression();
      var variableExpr2 = initializeTestVariableExpression("rightTest");
      var expr = new Expr.Binary(variableExpr, new Token(TokenType.PLUS, "+", null, 1, 1), variableExpr2);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.binary(expr);

      assertEquals(0, errors.size());
      assertTestVariableExpression(variableExpr, 1);
      assertTestVariableExpression(variableExpr2);
    }
  }

  @Nested
  class unary {
    @Test
    void test() {
      var variableExpr = initializeTestVariableExpression();
      var expr = new Expr.Unary(new Token(TokenType.BANG, "!", null, 1, 1), variableExpr);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.unary(expr);

      assertEquals(0, errors.size());
      assertTestVariableExpression(variableExpr);
    }
  }

  @Nested
  class call {
    @Test
    void test() {
      var variableExpr = initializeTestVariableExpression();
      var variableExpr2 = initializeTestVariableExpression("arg");
      var expr = new Expr.Call(variableExpr, new Token(TokenType.LEFT_PAREN, "(", null, 1, 1), List.of(variableExpr2));
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.call(expr);

      assertEquals(0, errors.size());
      assertTestVariableExpression(variableExpr, 1);
      assertTestVariableExpression(variableExpr2);
    }
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
    @Test
    void test() {
      var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.scopes().beginScope();
      scriptRuntime.scopes().define(token);

      var expr = new Expr.Variable(token);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.variable(expr);

      assertEquals(0, errors.size());
      assertEquals(0, scriptRuntime.scopes().getDistance(expr));
    }

    @Test
    void invalidSelfReference() {
      var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.scopes().beginScope();
      scriptRuntime.scopes().declare(token);

      var expr = new Expr.Variable(token);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.variable(expr);

      assertEquals(1, errors.size());
      assertEquals(ScriptErrorType.INVALID_SELF_INITIALIZATION, errors.get(0).type());
    }
  }

  @Nested
  class grouping {
    @Test
    void test() {
      var variableExpr = initializeTestVariableExpression();
      var expr = new Expr.Grouping(variableExpr);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.grouping(expr);

      assertEquals(0, errors.size());
      assertTestVariableExpression(variableExpr);
    }
  }

  @Nested
  class literal {
    @Test
    void test() {
      var expr = new Expr.Literal(3d);
      var resolver = new ExpressionResolver(scriptRuntime, errors);

      resolver.literal(expr);

      assertEquals(0, errors.size());
    }
  }

  private Expr initializeTestVariableExpression() {
    return initializeTestVariableExpression("test");
  }

  private Expr initializeTestVariableExpression(String lexeme) {
    var token = new Token(TokenType.IDENTIFIER, lexeme, null, 1, 1);

    scriptRuntime.scopes().beginScope();
    scriptRuntime.scopes().define(token);

    return new Expr.Variable(token);
  }

  private void assertTestVariableExpression(Expr expr) {
    assertTestVariableExpression(expr, 0);
  }

  private void assertTestVariableExpression(Expr expr, int distance) {
    assertEquals(distance, scriptRuntime.scopes().getDistance(expr));
  }
}
