package technology.sola.script.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
  class function {
    @Test
    void test() {
      scriptRuntime.scopes().beginScope();

      var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var paramToken = new Token(TokenType.IDENTIFIER, "param", null, 1, 1);
      var callVarEXpr = new Expr.Variable(token);
      var callExpr = new Expr.Call(callVarEXpr, new Token(TokenType.LEFT_PAREN, "(", null, 1, 1), List.of());
      var paramExpr = new Expr.Variable(paramToken);
      List<Stmt> body = List.of(
        new Stmt.Expression(callExpr),
        new Stmt.Expression(paramExpr)
      );
      var stmt = new Stmt.Function(
        token, List.of(paramToken), body
      );
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.function(stmt);

      assertEquals(0, errors.size());
      assertTestVariableExpression(callVarEXpr, 1);
      assertTestVariableExpression(paramExpr);
    }
  }

  @Nested
  @DisplayName("var")
  class varStmt {
    @Test
    void whenVariableAlreadyDeclared_shouldHaveError() {
      var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.scopes().beginScope();
      scriptRuntime.scopes().declare(token);

      var stmt = new Stmt.Var(token, null);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.var(stmt);

      assertEquals(1, errors.size());
      assertEquals(ScriptErrorType.ALREADY_DEFINED_VARIABLE, errors.get(0).type());
    }

    @Test
    void whenNoInitializer_shouldDefineVariable() {
      var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.scopes().beginScope();

      var stmt = new Stmt.Var(token, null);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      assertFalse(scriptRuntime.scopes().isDefinedInScope(token));

      resolver.var(stmt);

      assertEquals(0, errors.size());
      assertTrue(scriptRuntime.scopes().isDefinedInScope(token));
    }

    @Test
    void whenInitializer_shouldDefineVariableWithValueFromInitializer() {
      var initializer = initializeTestVariableExpression();
      var token = new Token(TokenType.IDENTIFIER, "test2", null, 1, 1);
      var stmt = new Stmt.Var(token, initializer);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      assertFalse(scriptRuntime.scopes().isDefinedInScope(token));

      resolver.var(stmt);

      assertEquals(0, errors.size());
      assertTrue(scriptRuntime.scopes().isDefinedInScope(token));
      assertTestVariableExpression(initializer);
    }
  }

  @Nested
  class valStmt {
    @Test
    void whenConstantAlreadyDeclared_shouldHaveError() {
      var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.scopes().beginScope();
      scriptRuntime.scopes().declare(token);

      var stmt = new Stmt.Val(token, new Expr.Literal(5d));
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.val(stmt);

      assertEquals(1, errors.size());
      assertEquals(ScriptErrorType.ALREADY_DEFINED_VARIABLE, errors.get(0).type());
    }

    @Test
    void shouldDefineConstantWithValueFromInitializer() {
      var initializer = initializeTestVariableExpression();
      var token = new Token(TokenType.IDENTIFIER, "test2", null, 1, 1);
      var stmt = new Stmt.Val(token, initializer);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      assertFalse(scriptRuntime.scopes().isDefinedInScope(token));

      resolver.val(stmt);

      assertEquals(0, errors.size());
      assertTrue(scriptRuntime.scopes().isDefinedInScope(token));
      assertTestVariableExpression(initializer);
    }
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
  class stmtIf {
    @Test
    void test() {
      var expr = initializeTestVariableExpression();
      var exprThen = initializeTestVariableExpression("then");
      var exprElse = initializeTestVariableExpression("else");
      var stmt = new Stmt.If(expr, new Stmt.Expression(exprThen), new Stmt.Expression(exprElse));
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.ifVisit(stmt);

      assertEquals(0, errors.size());
      assertTestVariableExpression(expr, 2);
      assertTestVariableExpression(exprThen, 1);
      assertTestVariableExpression(exprElse);
    }
  }

  @Nested
  class stmtReturn {
    @Test
    void test() {
      var token = new Token(TokenType.RETURN, "return", null, 1, 1);
      var expr = initializeTestVariableExpression();
      var stmt = new Stmt.Return(token, expr);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.function(
        new Stmt.Function(
          new Token(TokenType.IDENTIFIER, "test", null, 1, 1),
          List.of(),
          List.of(
            stmt
          )
        )
      );

      assertEquals(0, errors.size());
      assertTestVariableExpression(expr, 1);
    }

    @Test
    void whenNotInFunction_shouldAddError() {
      var token = new Token(TokenType.RETURN, "return", null, 1, 1);
      var expr = initializeTestVariableExpression();
      var stmt = new Stmt.Return(token, expr);
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.returnVisit(stmt);

      assertEquals(1, errors.size());
      assertEquals(ScriptErrorType.CANNOT_RETURN_FROM_TOP_LEVEL, errors.get(0).type());
    }
  }

  @Nested
  class stmtWhile {
    @Test
    void test() {
      var expr = initializeTestVariableExpression();
      var exprBody = initializeTestVariableExpression("body");
      var stmt = new Stmt.While(expr, new Stmt.Expression(exprBody));
      var resolver = new StatementResolver(scriptRuntime, expressionResolver, errors);

      resolver.whileVisit(stmt);

      assertEquals(0, errors.size());
      assertTestVariableExpression(expr, 1);
      assertTestVariableExpression(exprBody);
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
