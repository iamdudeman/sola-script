package technology.sola.script.runtime;

import technology.sola.script.parser.Stmt;

import java.util.List;
import java.util.function.Consumer;

public class SolaScriptFunction implements SolaScriptCallable {
  private final Consumer<Stmt> executeStatement;
  private final Stmt.Function declaration;
  private final ScriptRuntime scriptRuntime;
  private final Environment closure;

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
      environment.define(
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
    } finally {
      scriptRuntime.restoreEnvironment(currentEnvironmentHandle);
    }

    return null;
  }
}
