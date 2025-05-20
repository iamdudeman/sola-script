package technology.sola.script.resolver;

import org.jspecify.annotations.NullMarked;
import technology.sola.script.error.ScriptError;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolver handles updating {@link technology.sola.script.runtime.ScopeTable} information for a {@link ScriptRuntime}
 * for a list of {@link Stmt}s.
 */
@NullMarked
public class Resolver {
  private final StatementResolver statementResolver;
  private final List<ScriptError> errors = new ArrayList<>();

  /**
   * Creates an instance for desired {@link ScriptRuntime}.
   *
   * @param scriptRuntime the runtime this resolver is associated with
   */
  public Resolver(ScriptRuntime scriptRuntime) {
    statementResolver = new StatementResolver(scriptRuntime, new ExpressionResolver(scriptRuntime, errors), errors);
  }

  /**
   * Processes variable scope resolutions for a list of {@link Stmt}s in the attached {@link ScriptRuntime}.
   *
   * @param statements the statements to resolve variables for
   * @return any {@link ScriptError}s that happened while resolving
   */
  public List<ScriptError> resolve(List<Stmt> statements) {
    for (var statement : statements) {
      statement.accept(statementResolver);
    }

    return errors;
  }
}
