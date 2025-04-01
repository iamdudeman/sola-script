package technology.sola.script.runtime;

import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;

import java.util.*;

public class ScopeTable {
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  private final Map<Expr, Integer> locals = new HashMap<>();

  ScopeTable() {
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

    var scope = scopes.peek();

    scope.put(name.lexeme(), false);
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

  public boolean isDeclaredInScope(Token name) {
    if (scopes.isEmpty()) {
      return false;
    }

    var scope = scopes.peek();

    return scope.containsKey(name.lexeme());
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

  // todo package only
  Map<Expr, Integer> getLocals() {
    return locals;
  }


  void beginScope() {
    scopes.push(new HashMap<>());
  }

  void endScope() {
    scopes.pop();
  }
}
