package technology.sola.script.interpreter;

import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterTest {
  @Test
  void test() {
    ScriptRuntime scriptRuntime = new ScriptRuntime();
    Interpreter interpreter = new Interpreter(scriptRuntime);

    var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
    var varExpr = new Expr.Variable(varToken);

    var errors = interpreter.interpret(List.of(
      new Stmt.Var(varToken, new Expr.Literal("value"))
    ));

    assertEquals(0, errors.size());
    assertEquals("value", scriptRuntime.lookUpVariable(varExpr));
  }

  @Test
  void whenError_shouldStopAndReportError() {
    ScriptRuntime scriptRuntime = new ScriptRuntime();
    Interpreter interpreter = new Interpreter(scriptRuntime);

    var varToken = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
    var varExpr = new Expr.Variable(varToken);

    var errors = interpreter.interpret(List.of(
      new Stmt.Expression(varExpr),
      new Stmt.Var(varToken, new Expr.Literal("value"))
    ));

    assertEquals(1, errors.size());
    assertEquals(ScriptErrorType.UNDEFINED_VARIABLE, errors.get(0).type());
    // second statement should not have been able to run
    assertThrows(
      ScriptInterpretationException.class,
      () -> scriptRuntime.lookUpVariable(varExpr)
    );
  }
}
