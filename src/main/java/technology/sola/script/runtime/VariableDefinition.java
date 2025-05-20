package technology.sola.script.runtime;

import org.jspecify.annotations.Nullable;

class VariableDefinition {
  final boolean isConstant;
  @Nullable Object value;

  VariableDefinition(boolean isConstant, @Nullable Object value) {
    this.isConstant = isConstant;
    this.value = value;
  }
}
