package technology.sola.script.parser;

/**
 * Stmt represents a piece of code that performs an action or controls the flow of the program.
 */
public interface Stmt {
  /**
   * Executes this statement via the desired {@link Stmt.Visitor}
   *
   * @param visitor the {@link Stmt.Visitor} to execute this statement
   * @param <R>     the return type
   * @return the return value from the executed statement
   */
  <R> R accept(Visitor<R> visitor);

  /**
   * Defines operations for the various {@link Stmt} implementations.
   *
   * @param <R> the return type of the executed statement
   */
  interface Visitor<R> {
    /**
     * Executes an {@link Expression} statement and returns the value.
     *
     * @param stmt the {@link Expression} statement
     * @return the evaluated value
     */
    R expression(Expression stmt);
  }

  record Expression(Expr expr) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.expression(this);
    }
  }
}
