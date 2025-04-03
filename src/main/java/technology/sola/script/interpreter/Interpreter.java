package technology.sola.script.interpreter;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

/**
 * Interpreter handles interpreting a list of {@link Stmt} for a given {@link ScriptRuntime} state.
 */
public class Interpreter {
  private final StatementInterpreter statementInterpreter;

  /**
   * Creates an instance for desired {@link ScriptRuntime}.
   *
   * @param scriptRuntime the runtime
   */
  public Interpreter(ScriptRuntime scriptRuntime) {
    statementInterpreter = new StatementInterpreter(scriptRuntime, new ExpressionInterpreter(scriptRuntime));
  }

  /**
   * Interprets a list of {@link Stmt}s utilizing the attached {@link ScriptRuntime}.
   *
   * @param statements the list of statements
   * @return any {@link ScriptError}s that happened during execution
   */
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
