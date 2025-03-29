package technology.sola.script.interpreter;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {
  private final ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter();
  private final StatementInterpreter statementInterpreter = new StatementInterpreter();

  public List<ScriptError> interpret(List<Stmt> statements) {
    List<ScriptError> errors = new ArrayList<>();

    try {
      for (Stmt statement : statements) {
        statementInterpreter.execute(statement);
      }
    } catch (ScriptInterpretationException scriptInterpretationException) {
      errors.add(new ScriptError(scriptInterpretationException.errorType, scriptInterpretationException.token, scriptInterpretationException.errorsArgs));
    }

    return errors;
  }

  private class StatementInterpreter implements Stmt.Visitor<Void> {
    void execute(Stmt statement) {
      statement.accept(this);
    }

    @Override
    public Void expression(Stmt.Expression expr) {
      expressionInterpreter.evaluate(expr.expr());
      return null;
    }
  }
}
