package technology.sola.script.error;

import technology.sola.script.tokenizer.Token;

public record ScriptError(
  ScriptErrorType type,
  int line,
  int column,
  String message
) {
  public ScriptError(ScriptErrorType type, Token token, String message) {
    this(type, token.line(), token.column(), message);
  }

  @Override
  public String toString() {
    String where = "[" + line() + ":" + column() + "]";
    String kind = type().name();

    return where + " " + kind + ": " + message();
  }
}
