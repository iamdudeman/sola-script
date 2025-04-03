package technology.sola.script.error;

import technology.sola.script.tokenizer.Token;

/**
 * ScriptErrorException is a runtime exception that is thrown while trying to interpret a sola script containing
 * information about the {@link ScriptErrorType} cause and the {@link Token} it was found at.
 */
public class ScriptInterpretationException extends RuntimeException {
  /**
   * The {@link Token} where the error occurred.
   */
  public final Token token;
  /**
   * The {@link ScriptErrorType} of the error.
   */
  public final ScriptErrorType errorType;
  /**
   * The additional details about the error.
   */
  public final Object[] errorsArgs;

  /**
   * Creates an instance of this exception.
   *
   * @param token      the {@link Token} where the error occurred
   * @param errorType  the {@link ScriptErrorType} of the error
   * @param errorsArgs the additional details about the error
   */
  public ScriptInterpretationException(Token token, ScriptErrorType errorType, Object... errorsArgs) {
    super(errorType.formatMessage(errorsArgs));
    this.token = token;
    this.errorType = errorType;
    this.errorsArgs = errorsArgs;
  }
}
