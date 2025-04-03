package technology.sola.script.interpreter;

/**
 * Return is an exception utilized by the {@link Interpreter} to throw when
 * a {@link technology.sola.script.parser.Stmt.Return} is being interpreted so that the
 * wrapping {@link technology.sola.script.runtime.SolaScriptFunction} can catch it and return the value as the result
 * of the {@link technology.sola.script.parser.Expr.Call}.
 */
public class Return extends RuntimeException {
  public final Object value;

  /**
   * Creates an instance of this exception with desired return value.
   *
   * @param value the return value of the call
   */
  public Return(Object value) {
    super(null, null, false, false);
    this.value = value;
  }
}
