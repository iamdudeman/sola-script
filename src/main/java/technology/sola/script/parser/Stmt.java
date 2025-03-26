package technology.sola.script.parser;

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
