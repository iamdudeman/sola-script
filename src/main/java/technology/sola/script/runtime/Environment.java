package technology.sola.script.runtime;

import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, VariableDefinition> definitions = new HashMap<>();
  private final Environment parent;

  Environment() {
    this(null);
  }

  Environment(Environment parent) {
    this.parent = parent;
  }

  void defineVariable(String name, Object value) {
    definitions.put(name, new VariableDefinition(false, value));
  }

  /**
   * Updates a variable's value only if it has already been defined. Otherwise, throws a runtime
   * {@link ScriptInterpretationException}.
   *
   * @param name  the {@link Token} of the name of the variable
   * @param value the value assigned to the variable
   */
  void assign(Token name, Object value) {
    var definition = definitions.get(name.lexeme());

    // assign to current scope if present
    if (definition != null) {
      definition.value = value;
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
    var definition = getParent(distance).definitions.get(name.lexeme());

    definition.value = value;
  }

  /**
   * Gets a variable's value only if it has already been defined. Otherwise, throws a runtime
   * {@link ScriptInterpretationException}.
   *
   * @param name the {@link Token} of the name of the variable
   * @return the value assigned to the variable
   */
  Object get(Token name) {
    var definition = definitions.get(name.lexeme());

    if (definition != null) {
      return definition.value;
    }

    if (parent != null) {
      return parent.get(name);
    }

    throw new ScriptInterpretationException(name, ScriptErrorType.UNDEFINED_VARIABLE, name.lexeme());
  }

  Object getAt(int distance, String name) {
    var definition = getParent(distance).definitions.get(name);

    return definition.value;
  }

  private Environment getParent(int distance) {
    Environment environment = this;

    for (int i = 0; i < distance; i++) {
      environment = environment.parent;
    }

    return environment;
  }
}
