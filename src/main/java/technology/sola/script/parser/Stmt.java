package technology.sola.script.parser;

import java.util.List;

public interface Stmt {
  <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R expression(Expression expr);

    R block(Block stmt);
  }

  record Expression(Expr expr) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.expression(this);
    }
  }

  record Block(List<Stmt> statements) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.block(this);
    }
  }
}
