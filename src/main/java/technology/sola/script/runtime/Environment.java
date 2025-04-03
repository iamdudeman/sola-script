package technology.sola.script.runtime;

import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap<>();
  private final Environment parent;

  Environment() {
    this(null);
  }

  Environment(Environment parent) {
    this.parent = parent;
  }

  void define(String name, Object value) {
    values.put(name, value);
  }

  /**
   * Updates a variable's value only if it has already been defined. Otherwise, throws a runtime
   * {@link ScriptInterpretationException}.
   *
   * @param name  the {@link Token} of the name of the variable
   * @param value the value assigned to the variable
   */
  void assign(Token name, Object value) {
    // assign to current scope if present
    if (values.containsKey(name.lexeme())) {
      values.put(name.lexeme(), value);
      return;
    }

    // otherwise try to assign to parent scope
    if (parent != null) {
      parent.assign(name, value);
      return;
    }

    // if not in any scope then it is undefined
    throw new ScriptInterpretationException(name, ScriptErrorType.UNDEFINED_VARIABLE, name.lexeme());
  }

  void assignAt(int distance, Token name, Object value) {
    getParent(distance).values.put(name.lexeme(), value);
  }

  /**
   * Gets a variable's value only if it has already been defined. Otherwise, throws a runtime
   * {@link ScriptInterpretationException}.
   *
   * @param name the {@link Token} of the name of the variable
   * @return the value assigned to the variable
   */
  Object get(Token name) {
    if (values.containsKey(name.lexeme())) {
      return values.get(name.lexeme());
    }

    if (parent != null) {
      return parent.get(name);
    }

    throw new ScriptInterpretationException(name, ScriptErrorType.UNDEFINED_VARIABLE, name.lexeme());
  }

  Object getAt(int distance, String name) {
    return getParent(distance).values.get(name);
  }

  private Environment getParent(int distance) {
    Environment environment = this;

    for (int i = 0; i < distance; i++) {
      environment = environment.parent;
    }

    return environment;
  }
}
