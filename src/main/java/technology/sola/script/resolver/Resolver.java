package technology.sola.script.resolver;

import technology.sola.script.error.ScriptError;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

public class Resolver {
  private final StatementResolver statementResolver;
  private final List<ScriptError> errors = new ArrayList<>();

  public Resolver(ScriptRuntime scriptRuntime) {
    statementResolver = new StatementResolver(scriptRuntime, new ExpressionResolver(scriptRuntime, errors), errors);
  }

  public List<ScriptError> resolve(List<Stmt> statements) {
    for (var statement : statements) {
      statement.accept(statementResolver);
    }

    return errors;
  }
}
