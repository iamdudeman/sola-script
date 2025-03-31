package technology.sola.script.runtime;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;

import java.util.*;

class ScopeTable {
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  private final Map<Expr, Integer> locals = new HashMap<>();
  private final List<ScriptError> errors = new ArrayList<>();

  Map<Expr, Integer> getLocals() {
    return locals;
  }

  List<ScriptError> getErrors() {
    return errors;
  }

  void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme())) {
        locals.put(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  void beginScope() {
    scopes.push(new HashMap<>());
  }

  void endScope() {
    scopes.pop();
  }

  void declare(Token name) {
    if (scopes.isEmpty()) {
      return;
    }

    var scope = scopes.peek();

    if (scope.containsKey(name.lexeme())) {
      errors.add(new ScriptError(ScriptErrorType.ALREADY_DEFINED_VARIABLE, name, name.lexeme()));
    }

    scope.put(name.lexeme(), false);
  }

  void define(Token name) {
    if (scopes.isEmpty()) {
      return;
    }

    scopes.peek().put(name.lexeme(), true);
  }

  void validateSelfVariableInitialization(Expr.Variable variableExpression) {
    if (!scopes.isEmpty() && scopes.peek().put(variableExpression.name().lexeme(), Boolean.FALSE)) {
      errors.add(new ScriptError(ScriptErrorType.INVALID_SELF_INITIALIZATION, variableExpression.name(), variableExpression.name().lexeme()));
    }
  }
}
