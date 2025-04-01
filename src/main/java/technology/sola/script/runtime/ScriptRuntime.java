package technology.sola.script.runtime;

import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;

public class ScriptRuntime {
  private final Environment globals = new Environment();
  private Environment environment = globals;
  private final ScopeTable scopeTable = new ScopeTable();

  /**
   * Creates a nested environment with the previous environment as its parent. This can be utilized, for example, when
   * entering a new block of code.
   *
   * @return an {@link EnvironmentHandle} to the previous environment
   */
  public EnvironmentHandle createNestedEnvironment() {
    var previous = this.environment;

    this.environment = new Environment(this.environment);

    return new EnvironmentHandle(previous);
  }

  /**
   * Restores an environment utilizing its {@link EnvironmentHandle}. This can be utilized, for example, when existing
   * a block of code.
   *
   * @param handle the {@link EnvironmentHandle} to restore the environment to
   */
  public void restoreEnvironment(EnvironmentHandle handle) {
    this.environment = handle.environment;
  }

  /**
   * Gets the {@link ScopeTable} for the runtime so it can be updated with local variable resolutions.
   *
   * @return the {@link ScopeTable} for the runtime
   */
  public ScopeTable scopes() {
    return scopeTable;
  }

  public Object lookUpVariable(Token name, Expr expr) {
    Integer distance = scopeTable.getDistance(expr);

    if (distance == null) {
      return globals.get(name);
    } else {
      return environment.getAt(distance, name.lexeme());
    }
  }

  public void defineVariable(String name, Object value) {
    environment.define(name, value);
  }

  public void assignVariable(Expr.Assign expr, Object value) {
    Integer distance = scopeTable.getDistance(expr);

    if (distance == null) {
      globals.assign(expr.name(), value);
    } else {
      environment.assignAt(distance, expr.name(), value);
    }
  }
}
