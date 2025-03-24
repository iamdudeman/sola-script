package technology.sola.script.error;

import technology.sola.script.tokenizer.Token;

public record RuntimeError(int line, int column, String message) implements ScriptError {
  public RuntimeError(Token token, String message) {
    this(token.line(), token.column(), message);
  }

  @Override
  public ScriptErrorType type() {
    return ScriptErrorType.RUNTIME;
  }
}
