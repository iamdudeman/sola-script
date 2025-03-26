package technology.sola.script.parser;

/**
 * Stmt represents a piece of code that performs an action or controls the flow of the program.
 */
public interface Stmt {
  <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R expression(Expression expr);
  }

  record Expression(Expr expr) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.expression(this);
    }
  }
}
