package technology.sola.script.error;

/**
 * ScriptErrorType holds the various types of errors that happen while parsing and executing a sola script.
 * <p>
 * Order of occurrence:
 * <p>
 * {@link ScriptErrorType#PARSE} -> {@link ScriptErrorType#SEMANTIC} -> {@link ScriptErrorType#RUNTIME}
 */
public enum ScriptErrorType {
  /**
   * Parse errors happen before code is executed. The code is not in a state that makes sense.
   */
  PARSE,

  /**
   * Semantic errors happen after parsing, but before execution. The script is valid syntactically, but some of the
   * code referenced does not make sense to be executed.
   */
  SEMANTIC,

  /**
   * Runtime errors happen while the script is executing. The script is valid, but there is some logical errors in it.
   */
  RUNTIME,
}
