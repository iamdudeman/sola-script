package technology.sola.script.interpreter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatementInterpreterTest {
  private ScriptRuntime scriptRuntime;
  private ExpressionInterpreter expressionInterpreter;

  @BeforeEach
  void setup() {
    scriptRuntime = new ScriptRuntime();
    expressionInterpreter = new ExpressionInterpreter(scriptRuntime);
  }

  @Nested
  @DisplayName("var")
  class varStmt {
    @Test
    void test() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var varExpr = new Expr.Variable(varToken);
      var stmt = new Stmt.Var(varToken, null);

      statementInterpreter.var(stmt);

      assertNull(scriptRuntime.lookUpVariable(varExpr));
    }

    @Test
    void testWithInitializer() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var varExpr = new Expr.Variable(varToken);
      var stmt = new Stmt.Var(varToken, new Expr.Literal("value"));

      statementInterpreter.var(stmt);

      assertEquals("value", scriptRuntime.lookUpVariable(varExpr));
    }
  }

  @Nested
  class expression {
    @Test
    void test() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.defineVariable(varToken.lexeme(), "initial");

      var varExpr = new Expr.Variable(varToken);
      var stmt = new Stmt.Expression(
        new Expr.Assign(varToken, new Expr.Literal("updated"))
      );

      statementInterpreter.expression(stmt);

      assertEquals("updated", scriptRuntime.lookUpVariable(varExpr));
    }
  }

  @Nested
  class stmtIf {
    @Test
    void whenConditionTrue_shouldThenBranch() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var varExpr = new Expr.Variable(varToken);

      scriptRuntime.defineVariable(varToken.lexeme(), "initial");

      var stmt = new Stmt.If(
        new Expr.Literal("test"),
        new Stmt.Expression(new Expr.Assign(varToken, new Expr.Literal("updated"))),
        null
      );

      statementInterpreter.ifVisit(stmt);

      assertEquals("updated", scriptRuntime.lookUpVariable(varExpr));
    }

    @Test
    void whenConditionFalse_withElse_shouldElseBranch() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var varExpr = new Expr.Variable(varToken);

      scriptRuntime.defineVariable(varToken.lexeme(), "initial");

      var stmt = new Stmt.If(
        new Expr.Literal(null),
        new Stmt.Expression(new Expr.Assign(varToken, new Expr.Literal("updated"))),
        new Stmt.Expression(new Expr.Assign(varToken, new Expr.Literal("else")))
      );

      statementInterpreter.ifVisit(stmt);

      assertEquals("else", scriptRuntime.lookUpVariable(varExpr));
    }

    @Test
    void whenConditionFalse_withNoElse_shouldDoNothing() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var varExpr = new Expr.Variable(varToken);

      scriptRuntime.defineVariable(varToken.lexeme(), "initial");

      var stmt = new Stmt.If(
        new Expr.Literal(false),
        new Stmt.Expression(new Expr.Assign(varToken, new Expr.Literal("updated"))),
        null
      );

      statementInterpreter.ifVisit(stmt);

      assertEquals("initial", scriptRuntime.lookUpVariable(varExpr));
    }
  }

  @Nested
  class stmtWhile {
    @Test
    void test() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      var counterToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      var counterExpr = new Expr.Variable(counterToken);

      scriptRuntime.defineVariable(counterToken.lexeme(), 1d);

      var plusToken = new Token(TokenType.PLUS, "+", null, 1, 1);
      var stmt = new Stmt.While(
        new Expr.Binary(counterExpr, new Token(TokenType.LESS, "<", null, 1, 1), new Expr.Literal(3d)),
        new Stmt.Expression(new Expr.Assign(counterToken, new Expr.Binary(new Expr.Variable(counterToken), plusToken, new Expr.Literal(1d))))
      );

      statementInterpreter.whileVisit(stmt);

      assertEquals(3d, scriptRuntime.lookUpVariable(counterExpr));
    }
  }

  @Nested
  class block {
    @Test
    void test() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);
      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      scriptRuntime.defineVariable(varToken.lexeme(), "initial");

      List<Stmt> statements = List.of(
        new Stmt.Var(varToken, new Expr.Literal("updated"))
      );
      var stmt = new Stmt.Block(statements);
      var varExpr = new Expr.Variable(varToken);

      statementInterpreter.block(stmt);

      assertEquals("initial", scriptRuntime.lookUpVariable(varExpr));
    }

    /**
     * Test case creates a global with initial value and then inside a block declares that same variable with a
     * different value. When the block exits because of an error it should restore the environment so when the variable
     * is read the initial global value is seen.
     */
    @Test
    void whenError_shouldStillRestoreEnvironment() {
      StatementInterpreter statementInterpreter = new StatementInterpreter(scriptRuntime, expressionInterpreter);

      scriptRuntime.defineVariable("test", "initial");

      var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
      List<Stmt> statements = List.of(
        new Stmt.Var(varToken, new Expr.Literal("updated")),
        new Stmt.Expression(
          new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1, 1), new Expr.Literal("test"))
        )
      );
      var stmt = new Stmt.Block(statements);
      var varExpr = new Expr.Variable(varToken);

      scriptRuntime.scopes().beginScope();
      scriptRuntime.scopes().define(varToken);
      scriptRuntime.scopes().resolveLocal(varExpr, varToken);
      scriptRuntime.scopes().endScope();

      assertThrows(
        ScriptInterpretationException.class,
        () -> statementInterpreter.block(stmt)
      );

      assertEquals("initial", scriptRuntime.lookUpVariable(varExpr));
    }
  }
}
