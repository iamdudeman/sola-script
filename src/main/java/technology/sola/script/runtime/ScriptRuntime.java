package technology.sola.script.runtime;

import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;

public class ScriptRuntime {
  private final Environment globals = new Environment();
  private Environment environment = globals;
  private final ScopeTable scopeTable = new ScopeTable();

  // todo maybe method to access scope table?

  public Object lookUpVariable(Token name, Expr expr) {
    Integer distance = scopeTable.getLocals().get(expr);

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
    Integer distance = scopeTable.getLocals().get(expr);

    if (distance == null) {
      globals.assign(expr.name(), value);
    } else {
      environment.assignAt(distance, expr.name(), value);
    }
  }

  public EnvironmentHandle newEnvironment() {
    var previous = this.environment;

    this.environment = new Environment(this.environment);

    return new EnvironmentHandle(previous);
  }

  public void restoreEnvironment(EnvironmentHandle handle) {
    this.environment = handle.environment;
  }
}
