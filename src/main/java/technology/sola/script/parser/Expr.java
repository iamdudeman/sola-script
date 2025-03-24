package technology.sola.script.parser;

import technology.sola.script.tokenizer.Token;

public interface Expr {
  <R> R visit(Visitor<R> visitor);

  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
  }

  record Assign(Token name, Expr value) implements Expr {
    @Override
    public <R> R visit(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }
  }
}
