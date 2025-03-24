package technology.sola.script.error;

import technology.sola.script.tokenizer.Token;

public record ParseError(int line, int column, String message) implements ScriptError {
  public ParseError(Token token, String message) {
    this(token.line(), token.column(), message);
  }

  @Override
  public ScriptErrorType type() {
    return ScriptErrorType.COMPILE;
  }
}
