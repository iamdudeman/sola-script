package technology.sola.script.interpreter;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {
  private final StatementInterpreter statementInterpreter;

  public Interpreter(ScriptRuntime scriptRuntime) {
    statementInterpreter = new StatementInterpreter(scriptRuntime, new ExpressionInterpreter(scriptRuntime));
  }

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
}
