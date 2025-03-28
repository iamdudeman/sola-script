package technology.sola.script.error;

// todo rename to ScriptErrorType

import technology.sola.script.parser.ParserConstants;

/**
 * ScriptErrorType contains various expected errors that may occur while parsing and executing a sola script.
 */
public enum ErrorMessage {
  /**
   * Parsing error when an expression is expected, but not found.
   */
  EXPECT_EXPRESSION(ScriptErrorStage.PARSE, "Expect expression."),

  EXPECT_DOT_AFTER_SUPER(ScriptErrorStage.PARSE, "Expect '.' after super."),

  EXPECT_SEMI_AFTER_EXPRESSION(ScriptErrorStage.PARSE, "Expect ';' after expression."),

  EXPECT_PAREN_AFTER_ARGUMENTS(ScriptErrorStage.PARSE, "Expect ')' after arguments."),

  EXPECT_PAREN_AFTER_EXPRESSION(ScriptErrorStage.PARSE, "Expect ')' after expression."),

  EXPECT_PROPERTY_NAME_AFTER_DOT(ScriptErrorStage.PARSE, "Expect property name after '.'."),

  EXPECT_SUPERCLASS_METHOD_NAME(ScriptErrorStage.PARSE, "Expect superclass method name."),

  /**
   * Parsing error when an assignment operation is detected, but the target is not valid.
   */
  INVALID_ASSIGNMENT_TARGET(ScriptErrorStage.PARSE, "Invalid assignment target."),

  /**
   * Parsing error when a binary operator is detected without a left operand.
   */
  INVALID_BINARY_EXPRESSION(ScriptErrorStage.PARSE, "Binary expression missing left operand."),

  ONLY_INSTANCES_HAVE_PROPERTIES(ScriptErrorStage.RUNTIME, "Only instances have properties."),

  /**
   * Semantic error when a function or method has been declared with too many arguments. Maximum number of arguments
   * allowed is {@link technology.sola.script.parser.ParserConstants#MAX_ARGUMENTS}.
   */
  TOO_MANY_ARGUMENTS(ScriptErrorStage.SEMANTIC, "Can't have more than " + ParserConstants.MAX_ARGUMENTS + " arguments."),

  /**
   * Parsing error that happens during tokenization when an unexpected character is found in a script.
   */
  UNEXPECTED_CHARACTER(ScriptErrorStage.PARSE, "Unexpected character '%s'."),

  /**
   * Parsing error when a {@link technology.sola.script.tokenizer.TokenType#STRING} has been detected, but has not been
   * terminated.
   */
  UNTERMINATED_STRING(ScriptErrorStage.PARSE, "Unterminated string."),

  ;

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
