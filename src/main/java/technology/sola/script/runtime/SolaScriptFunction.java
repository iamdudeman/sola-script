package technology.sola.script.runtime;

import technology.sola.script.parser.Stmt;

import java.util.List;
import java.util.function.Consumer;

/**
 * SolaScriptFunction is a runtime definition of a function declaration that is callable (implements
 * {@link SolaScriptCallable} interface).
 */
public class SolaScriptFunction implements SolaScriptCallable {
  private final Consumer<Stmt> executeStatement;
  private final Stmt.Function declaration;
  private final ScriptRuntime scriptRuntime;
  private final Environment closure;

  /**
   * Creates an instance of the runtime function declaration.
   *
   * @param executeStatement the consumer that handles execution at runtime
   * @param scriptRuntime    the {@link ScriptRuntime} state
   * @param declaration      the {@link Stmt.Function} declaration
   */
  public SolaScriptFunction(Consumer<Stmt> executeStatement, ScriptRuntime scriptRuntime, Stmt.Function declaration) {
    this.executeStatement = executeStatement;
    this.scriptRuntime = scriptRuntime;
    this.declaration = declaration;
    this.closure = scriptRuntime.environment;
  }

  @Override
  public int arity() {
    return declaration.parameters().size();
  }

  @Override
  public Object call(List<Object> arguments) {
    Environment environment = new Environment(closure);

    for (int i = 0; i < declaration.parameters().size(); i++) {
      environment.defineVariable(
        declaration.parameters().get(i).lexeme(),
        arguments.get(i)
      );
    }

    var currentEnvironmentHandle = new EnvironmentHandle(scriptRuntime.environment);

    try {
      scriptRuntime.environment = environment;

      for (var statement : declaration.body()) {
        executeStatement.accept(statement);
      }
    } catch (Stmt.Return.Exception returnValue) {
      return returnValue.value;
    } finally {
      scriptRuntime.restoreEnvironment(currentEnvironmentHandle);
    }

    return null;
  }
}
