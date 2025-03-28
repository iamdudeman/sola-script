package technology.sola.script.interpreter;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Stmt;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {
  private List<ScriptError> errors = new ArrayList<>();

  public void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        // todo execute
      }
    } catch (ScriptInterpretationException scriptInterpretationException) {
      errors.add(new ScriptError(scriptInterpretationException.errorType, scriptInterpretationException.token, scriptInterpretationException.errorsArgs));
    }
  }
}
