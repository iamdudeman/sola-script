package technology.sola.script.error;

// todo rename to ScriptErrorType

/**
 * ScriptErrorType contains various expected errors that may occur while parsing and executing a sola script.
 */
public enum ErrorMessage {
  /**
   * Error that happens during tokenization when an unexpected character is found in a script.
   */
  UNEXPECTED_CHARACTER(ScriptErrorStage.PARSE, "Unexpected character '%s'.");

  /**
   * The {@link ScriptErrorStage} where the error occurs.
   */
  public final ScriptErrorStage stage;
  private final String messageFormat;

  ErrorMessage(ScriptErrorStage stage, String messageFormat) {
    this.stage = stage;
    this.messageFormat = messageFormat;
  }

  /**
   * Formats the error message for displaying to the developer.
   *
   * @param args any arguments that need to be formatted
   * @return the formatted error message
   */
  public String formatMessage(Object... args) {
    return String.format(messageFormat, args);
  }
}
