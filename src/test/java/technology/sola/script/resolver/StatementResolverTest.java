package technology.sola.script.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptError;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatementResolverTest {
  private ScriptRuntime scriptRuntime;
  private ExpressionResolver expressionResolver;
  private List<ScriptError> errors;

  @BeforeEach
  void setup() {
    errors = new ArrayList<>();
    scriptRuntime = new ScriptRuntime();
    expressionResolver = new ExpressionResolver(scriptRuntime, errors);
  }

  @Nested
  @DisplayName("var")
  class varDecl {
    // todo
  }

  @Nested
  class expression {
    @Test
    void test() {
      var expr = initializeTestVariableExpression();
      var stmt = new Stmt.Expression(expr);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.expression(stmt);

      assertEquals(0, errors.size());
      assertTestVariableExpression(expr);
    }
  }

  @Nested
  class block {
    @Test
    void test() {
      var expr = initializeTestVariableExpression();
      var exprStmt = new Stmt.Expression(expr);
      var stmt = new Stmt.Block(List.of(exprStmt));
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.block(stmt);
      // running twice here to ensure scope is properly ended from previous block
      resolver.block(stmt);

      assertEquals(0, errors.size());
      assertTestVariableExpression(expr, 1);
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
