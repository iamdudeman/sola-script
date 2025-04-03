package technology.sola.script.runtime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class SolaScriptFunctionTest {
  @Nested
  class arity {
    @Test
    void shouldBeBasedOnDeclaration() {
      ScriptRuntime scriptRuntime = new ScriptRuntime();
      Stmt.Function declaration = new Stmt.Function(
        new Token(TokenType.IDENTIFIER, "test", null, 1 , 1),
        List.of(
          new Token(TokenType.IDENTIFIER, "param1", null, 1 , 1),
          new Token(TokenType.IDENTIFIER, "param2", null, 1 , 1)
        ),
        List.of()
      );
      SolaScriptFunction function = new SolaScriptFunction(stmt -> {}, scriptRuntime, declaration);

      assertEquals(2, function.arity());
    }
  }

  @Nested
  class call {
    @Test
    void shouldDefineArguments() {
      ScriptRuntime scriptRuntime = new ScriptRuntime();
      var param1 =  new Token(TokenType.IDENTIFIER, "param1", null, 1 , 1);
      var param2 =  new Token(TokenType.IDENTIFIER, "param2", null, 1 , 1);
      Stmt.Function declaration = new Stmt.Function(
        new Token(TokenType.IDENTIFIER, "test", null, 1 , 1),
        List.of(param1, param2),
        List.of(
          new Stmt.Expression(new Expr.Literal(null))
        )
      );
      TestInterpreter testInterpreter = new TestInterpreter(stmt -> {
        assertEquals("test", scriptRuntime.environment.get(param1));
        assertEquals(5d, scriptRuntime.environment.get(param2));
      });
      SolaScriptFunction function = new SolaScriptFunction(testInterpreter, scriptRuntime, declaration);

      function.call(List.of("test", 5d));

      assertEquals(1, testInterpreter.statementsExecuted);
      assertThrows(
        ScriptInterpretationException.class,
        () -> scriptRuntime.environment.get(param1)
      );
    }

    @Test
    void shouldExecuteEachStatementInBody() {
      ScriptRuntime scriptRuntime = new ScriptRuntime();
      Stmt.Function declaration = new Stmt.Function(
        new Token(TokenType.IDENTIFIER, "test", null, 1 , 1),
        List.of(),
        List.of(
          new Stmt.Expression(new Expr.Literal(null)),
          new Stmt.Expression(new Expr.Literal(null)),
          new Stmt.Expression(new Expr.Literal(null))
        )
      );
      TestInterpreter testInterpreter = new TestInterpreter(stmt -> {});
      SolaScriptFunction function = new SolaScriptFunction(testInterpreter, scriptRuntime, declaration);

      function.call(List.of());

      assertEquals(3, testInterpreter.statementsExecuted);
    }

    @Test
    void shouldRestoreEnvironmentOnError() {
      ScriptRuntime scriptRuntime = new ScriptRuntime();
      var param1 =  new Token(TokenType.IDENTIFIER, "param1", null, 1 , 1);
      Stmt.Function declaration = new Stmt.Function(
        new Token(TokenType.IDENTIFIER, "test", null, 1 , 1),
        List.of(param1),
        List.of(
          new Stmt.Expression(new Expr.Literal(null))
        )
      );
      TestInterpreter testInterpreter = new TestInterpreter(stmt -> {
        throw new RuntimeException("forced failure");
      });
      SolaScriptFunction function = new SolaScriptFunction(testInterpreter, scriptRuntime, declaration);

      assertThrows(
        RuntimeException.class,
        () -> function.call(List.of("test"))
      );
      assertEquals(1, testInterpreter.statementsExecuted);
      assertThrows(
        ScriptInterpretationException.class,
        () -> scriptRuntime.environment.get(param1)
      );
    }

    @Test
    void shouldReturnTheValueWhenReturnDetected() {
      ScriptRuntime scriptRuntime = new ScriptRuntime();
      Stmt.Function declaration = new Stmt.Function(
        new Token(TokenType.IDENTIFIER, "test", null, 1 , 1),
        List.of(),
        List.of(
          new Stmt.Return(
            new Token(TokenType.RETURN, "return", null, 1 , 1),
            new Expr.Literal("hello")
          )
        )
      );
      TestInterpreter testInterpreter = new TestInterpreter(stmt -> {
        throw new Stmt.Return.Exception("hello");
      });
      SolaScriptFunction function = new SolaScriptFunction(testInterpreter, scriptRuntime, declaration);

      var result = function.call(List.of());

      assertEquals(1, testInterpreter.statementsExecuted);
      assertEquals("hello", result);
    }
  }

  private static class TestInterpreter implements Consumer<Stmt> {
    private int statementsExecuted = 0;
    private final Consumer<Stmt> statementInterpreter;

    TestInterpreter(Consumer<Stmt> statementInterpreter) {
      this.statementInterpreter = statementInterpreter;
    }

    @Override
    public void accept(Stmt stmt) {
      statementsExecuted++;
      statementInterpreter.accept(stmt);
    }
  }
}
