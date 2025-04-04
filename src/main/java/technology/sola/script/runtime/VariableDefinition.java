package technology.sola.script.runtime;

class VariableDefinition {
  final boolean isConstant;
  Object value;

  VariableDefinition(Object value) {
    this(false, value);
  }

  VariableDefinition(boolean isConstant, Object value) {
    this.isConstant = isConstant;
    this.value = value;
  }
}
