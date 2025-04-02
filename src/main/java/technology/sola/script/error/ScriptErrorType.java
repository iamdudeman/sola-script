package technology.sola.script.error;

import technology.sola.script.parser.ParserConstants;

/**
 * ScriptErrorType contains various expected errors that may occur while parsing and executing a sola script.
 */
public enum ScriptErrorType {
  /**
   * Semantic error when a variable was already defined in this scope.
   */
  ALREADY_DEFINED_VARIABLE(ScriptErrorStage.SEMANTIC, "'%s' is already defined in this scope."),

  /**
   * Parsing error when an expression is expected, but not found.
   */
  EXPECT_EXPRESSION(ScriptErrorStage.PARSE, "Expect expression."),

  /**
   * Parsing error when a closing '}' is not found after a block.
   */
  EXPECT_BRACE_AFTER_BLOCK(ScriptErrorStage.PARSE, "Expect '}' after block."),

  /**
   * Parsing error when a '.' property accessor was not found after 'super'.
   */
  EXPECT_DOT_AFTER_SUPER(ScriptErrorStage.PARSE, "Expect '.' after super."),

  /**
   * Parsing error when a semicolon was not found after an expression statement.
   */
  EXPECT_SEMI_AFTER_EXPRESSION(ScriptErrorStage.PARSE, "Expect ';' after expression."),

  /**
   * Parsing error when a semicolon was not found after a variable declaration.
   */
  EXPECT_SEMI_AFTER_VARIABLE_DECLARATION(ScriptErrorStage.PARSE, "Expect ';' after variable declaration."),

  /**
   * Parsing error when a closing parenthesis was not found after function arguments.
   */
  EXPECT_PAREN_AFTER_ARGUMENTS(ScriptErrorStage.PARSE, "Expect ')' after arguments."),

  /**
   * Parsing error when a closing parenthesis was not found after an if statement's condition.
   */
  EXPECT_PAREN_AFTER_CONDITION(ScriptErrorStage.PARSE, "Expect ')' after condition."),

  /**
   * Parsing error when a closing parenthesis was not found after an expression that followed an open parenthesis.
   */
  EXPECT_PAREN_AFTER_EXPRESSION(ScriptErrorStage.PARSE, "Expect ')' after expression."),

  /**
   * Parsing error when an opening parenthesis is not found after an if statement is started.
   */
  EXPECT_PAREN_AFTER_IF(ScriptErrorStage.PARSE, "Expect '(' after if."),

  /**
   * Parsing error when a property name was not found after a '.' property accessor.
   */
  EXPECT_PROPERTY_NAME_AFTER_DOT(ScriptErrorStage.PARSE, "Expect property name after '.'."),

  /**
   * Parsing error when a method name was not found after a superclass accessor.
   */
  EXPECT_SUPERCLASS_METHOD_NAME(ScriptErrorStage.PARSE, "Expect superclass method name."),

  /**
   * Parsing error when a variable name is expected.
   */
  EXPECT_VARIABLE_NAME(ScriptErrorStage.PARSE, "Expect variable name."),

  /**
   * Runtime error when expected number of arguments were not provided.
   */
  EXPECTED_ARGUMENTS_MISMATCH(ScriptErrorStage.RUNTIME, "Expected %s arguments but got %s."),

  /**
   * Parsing error when an assignment operation is detected, but the target is not valid.
   */
  INVALID_ASSIGNMENT_TARGET(ScriptErrorStage.PARSE, "Invalid assignment target."),

  /**
   * Parsing error when a binary operator is detected without a left operand.
   */
  INVALID_BINARY_EXPRESSION(ScriptErrorStage.PARSE, "Binary expression missing left operand."),

  /**
   * Semantic error when a variable is used to initialize itself.
   */
  INVALID_SELF_INITIALIZATION(ScriptErrorStage.SEMANTIC, "Cannot use local variable '%s' in its own initializer."),

  /**
   * Runtime error when a call is detected on something that is not a function or class.
   */
  NOT_CALLABLE(ScriptErrorStage.RUNTIME, "Can only call functions and classes."),

  /**
   * Runtime error when a property accessor is found on something that is not an object.
   */
  ONLY_INSTANCES_HAVE_PROPERTIES(ScriptErrorStage.RUNTIME, "Only instances have properties."),

  /**
   * Runtime error when an operand was expected to be a number.
   */
  OPERAND_MUST_BE_NUMBER(ScriptErrorStage.RUNTIME, "Operand must be a number."),

  /**
   * Runtime error when both operands were expected to be a number.
   */
  OPERANDS_MUST_BE_NUMBERS(ScriptErrorStage.RUNTIME, "Operands must be numbers."),

  /**
   * Runtime error when both operands are not numbers and at least one of the operands is not a string.
   */
  OPERANDS_MUST_BE_TWO_NUMBERS_OR_ONE_STRING(ScriptErrorStage.RUNTIME, "Operands must be two numbers or one must be a string."),

  /**
   * Semantic error when a function or method has been declared with too many arguments. Maximum number of arguments
   * allowed is {@link technology.sola.script.parser.ParserConstants#MAX_ARGUMENTS}.
   */
  TOO_MANY_ARGUMENTS(ScriptErrorStage.SEMANTIC, "Can't have more than " + ParserConstants.MAX_ARGUMENTS + " arguments."),

  /**
   * Runtime error when a variable is used, but not defined in scope.
   */
  UNDEFINED_VARIABLE(ScriptErrorStage.RUNTIME, "Undefined variable '%s'."),

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

  ScriptErrorType(ScriptErrorStage stage, String messageFormat) {
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
