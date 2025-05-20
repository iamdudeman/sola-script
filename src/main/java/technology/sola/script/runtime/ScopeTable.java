package technology.sola.script.runtime;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;

import java.util.*;

/**
 * ScopeTable holds variable resolutions at various depths of scope.
 */
@NullMarked
public class ScopeTable {
  // If variable is in scopes and is false then it has been declared but not defined
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  private final Map<Expr, Integer> locals = new HashMap<>();

  /**
   * Gets the distance needed to resolve the correct value for the desired variable.
   *
   * @param expr the {@link Expr} to get the value for
   * @return the distance to resolve the variable's value
   */
  @Nullable
  public Integer getDistance(Expr expr) {
    return locals.get(expr);
  }

  /**
   * Begins a new scope for variables to be resolved.
   */
  public void beginScope() {
    scopes.push(new HashMap<>());
  }

  /**
   * Ends the current scope for variables to be resolved.
   */
  public void endScope() {
    scopes.pop();
  }

  /**
   * Resolves the nested depth of a local variable usage for an {@link Expr}.
   *
   * @param expr the expression to resolve the local variable usage for
   * @param name the {@link Token} for the name of the variable
   */
  public void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme())) {
        locals.put(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  /**
   * Marks a variable as declared for the current scope. A declared variable cannot be used to initialize itself.
   *
   * @param name the {@link Token} for the name of the variable
   */
  public void declare(Token name) {
    if (scopes.isEmpty()) {
      return;
    }

    scopes.peek().put(name.lexeme(), false);
  }

  /**
   * Marks a variable is defined for the current scope.
   *
   * @param name the {@link Token} for the name of the variable
   */
  public void define(Token name) {
    define(name.lexeme());
  }

  /**
   * Marks a variable is defined for the current scope.
   *
   * @param name the name of the variable
   */
  public void define(String name) {
    if (scopes.isEmpty()) {
      return;
    }

    scopes.peek().put(name, true);
  }

  /**
   * Checks if a variable has been declared in the current scope or not. Return false if there has been no scope
   * started.
   *
   * @param name the {@link Token} for the name of the variable
   * @return true if the variable has already been declared in the current scope
   */
  public boolean isDeclaredInScope(Token name) {
    if (scopes.isEmpty()) {
      return false;
    }

    var scope = scopes.peek();

    return scope.containsKey(name.lexeme());
  }

  /**
   * Checks if a variable has been defined in the current scope or not. Return false if there has been no scope
   * started.
   *
   * @param name the {@link Token} for the name of the variable
   * @return true if the variable has already been defined in the current scope
   */
  public boolean isDefinedInScope(Token name) {
    if (scopes.isEmpty()) {
      return false;
    }

    var scope = scopes.peek();

    return scope.get(name.lexeme()) == Boolean.TRUE;
  }

  /**
   * Utility method to check if a {@link Expr.Variable} expression is being used to initialize itself.
   *
   * @param expression the variable expression
   * @return true if it is a self referencing variable initialization
   */
  public boolean isSelfReferenceVariableInitialization(Expr.Variable expression) {
    return !scopes.isEmpty() && scopes.peek().get(expression.name().lexeme()) == Boolean.FALSE;
  }
}
