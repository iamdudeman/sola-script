package technology.sola.script.runtime;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

@NullMarked
class Environment {
  private final Map<String, VariableDefinition> definitions = new HashMap<>();
  private final @Nullable Environment parent;

  Environment() {
    this(null);
  }

  Environment(@Nullable Environment parent) {
    this.parent = parent;
  }

  void defineVariable(String name, @Nullable Object value) {
    definitions.put(name, new VariableDefinition(false, value));
  }

  void defineConstant(String name, @Nullable Object value) {
    definitions.put(name, new VariableDefinition(true, value));
  }

  /**
   * Updates a variable's value only if it has already been defined. Otherwise, throws a runtime
   * {@link ScriptInterpretationException}.
   *
   * @param name  the {@link Token} of the name of the variable
   * @param value the value assigned to the variable
   */
  void assign(Token name, @Nullable Object value) {
    var definition = definitions.get(name.lexeme());

    // assign to current scope if present
    if (definition != null) {
      if (definition.isConstant) {
        throw new ScriptInterpretationException(name, ScriptErrorType.CANNOT_ASSIGN_TO_CONSTANT, name.lexeme());
      }

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

  void assignAt(int distance, Token name, @Nullable Object value) {
    var definition = getParent(distance).definitions.get(name.lexeme());

    if (definition.isConstant) {
      throw new ScriptInterpretationException(name, ScriptErrorType.CANNOT_ASSIGN_TO_CONSTANT, name.lexeme());
    }

    definition.value = value;
  }

  /**
   * Gets a variable's value only if it has already been defined. Otherwise, throws a runtime
   * {@link ScriptInterpretationException}.
   *
   * @param name the {@link Token} of the name of the variable
   * @return the value assigned to the variable
   */
  @Nullable Object get(Token name) {
    var definition = definitions.get(name.lexeme());

    if (definition != null) {
      return definition.value;
    }

    if (parent != null) {
      return parent.get(name);
    }

    throw new ScriptInterpretationException(name, ScriptErrorType.UNDEFINED_VARIABLE, name.lexeme());
  }

  @Nullable Object getAt(int distance, String name) {
    var definition = getParent(distance).definitions.get(name);

    return definition.value;
  }

  private Environment getParent(int distance) {
    Environment environment = this;

    for (int i = 0; i < distance; i++) {
      environment = environment.parent;

      if (environment == null) {
        throw new IllegalStateException("Environment has no parent at distance " + distance);
      }
    }

    return environment;
  }
}
