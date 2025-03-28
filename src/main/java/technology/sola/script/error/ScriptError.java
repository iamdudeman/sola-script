package technology.sola.script.error;

import technology.sola.script.tokenizer.Token;

/**
 * ScriptError holds information about what kind of error happened, where it happened, and a longer description for a
 * user to resolve it.
 *
 * @param type    the {@link ScriptErrorType}
 * @param line    the line where the error was detected
 * @param column  the column where the error was detected
 * @param message the additional details about the error
 */
public record ScriptError(
  // todo replace type and message with the new ErrorMessage stuff
  ScriptErrorType type,
  int line,
  int column,
  String message
) {
  /**
   * ScriptError holds information about what kind of error happened, where it happened, and a longer description for a
   * user to resolve it.
   *
   * @param type    the {@link ScriptErrorType}
   * @param token   the {@link Token} where the error was detected
   * @param message the additional details about the error
   */
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
