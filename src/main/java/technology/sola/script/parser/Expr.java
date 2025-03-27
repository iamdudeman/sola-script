package technology.sola.script.parser;

import technology.sola.script.tokenizer.Token;

import java.util.List;

/**
 * Expr represents code that evaluates to a value.
 */
public interface Expr {
  <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R call(Call expr);

    R get(Get expr);

    R thisVisit(This expr);

    R superVisit(Super expr);

    R variable(Variable expr);

    R grouping(Grouping expr);

    R literal(Literal expr);
  }

  record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.call(this);
    }
  }

  record Get(Expr object, Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.get(this);
    }
  }

  record This(Token keyword) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.thisVisit(this);
    }
  }

  record Super(Token keyword, Token method) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.superVisit(this);
    }
  }

  record Variable(Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.variable(this);
    }
  }

  record Grouping(Expr expression) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.grouping(this);
    }
  }

  record Literal(Object value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.literal(this);
    }
  }
}
