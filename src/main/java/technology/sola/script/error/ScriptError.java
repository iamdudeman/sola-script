package technology.sola.script.error;

import technology.sola.script.tokenizer.Token;

/**
 * ScriptError holds information about what kind of error happened, where it happened, and a longer description for a
 * user to resolve it.
 *
 * @param type      the {@link ErrorMessage}
 * @param line      the line where the error was detected
 * @param column    the column where the error was detected
 * @param errorArgs the additional details about the error
 */
public record ScriptError(
  ErrorMessage type,
  int line,
  int column,
  Object... errorArgs
) {
  /**
   * ScriptError holds information about what kind of error happened, where it happened, and a longer description for a
   * user to resolve it.
   *
   * @param type      the {@link ErrorMessage}
   * @param token     the {@link Token} where the error was detected
   * @param errorArgs the additional details about the error
   */
  public ScriptError(ErrorMessage type, Token token, Object... errorArgs) {
    this(type, token.line(), token.column(), errorArgs);
  }

  @Override
  public String toString() {
    String where = "[" + line() + ":" + column() + "]";
    String kind = type().name();

    return where + " " + kind + ": " + type.formatMessage(errorArgs);
  }
}
