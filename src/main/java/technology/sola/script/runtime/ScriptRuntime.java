package technology.sola.script.runtime;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import technology.sola.script.library.ScriptModule;
import technology.sola.script.parser.Expr;

/**
 * ScriptRuntime contains environment state information as well as a {@link ScopeTable} to keep track of nested scope
 * variable resolutions.
 */
@NullMarked
public class ScriptRuntime {
  private final Environment globals = new Environment();
  Environment environment = globals;
  private final ScopeTable scopeTable = new ScopeTable();

  /**
   * Imports functionality provided by a {@link ScriptModule} into the globals of this runtime.
   *
   * @param scriptModule the module to import
   */
  public void importModule(ScriptModule scriptModule) {
    for (var entry : scriptModule.variables().entrySet()) {
      defineVariable(entry.getKey(), entry.getValue());
    }

    for (var entry : scriptModule.constants().entrySet()) {
      defineConstant(entry.getKey(), entry.getValue());
    }
  }

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

  /**
   * Defines a variable with desired value in the current environment.
   *
   * @param name  the name of the variable
   * @param value the value for the variable
   */
  public void defineVariable(String name, @Nullable Object value) {
    environment.defineVariable(name, value);
  }

  /**
   * Defines a constant with desired value in the current environment.
   *
   * @param name  the name of the constant
   * @param value the value for the constant
   */
  public void defineConstant(String name, @Nullable Object value) {
    environment.defineConstant(name, value);
  }

  /**
   * Looks up a variable's value utilizing the variable resolutions in the {@link ScopeTable} for the runtime.
   * <p>
   * If there is not a scope resolution for the expression then it will be retrieved as a global.
   *
   * @param expr the {@link Expr.Variable} to get the value for
   * @return the variable's value
   */
  @Nullable
  public Object lookUpVariable(Expr.Variable expr) {
    var distance = scopeTable.getDistance(expr);
    var name = expr.name();

    if (distance == null) {
      return globals.get(name);
    } else {
      return environment.getAt(distance, name.lexeme());
    }
  }

  /**
   * Assigns a variable's value utilizing the variable resolutions in the {@link ScopeTable} for the runtime.
   * <p>
   * If there is not a scope resolution for the expression then it will be assigned as a global.
   *
   * @param expr  the {@link Expr.Assign} expression
   * @param value the value to assign to the variable
   */
  public void assignVariable(Expr.Assign expr, @Nullable Object value) {
    Integer distance = scopeTable.getDistance(expr);

    if (distance == null) {
      globals.assign(expr.name(), value);
    } else {
      environment.assignAt(distance, expr.name(), value);
    }
  }
}
