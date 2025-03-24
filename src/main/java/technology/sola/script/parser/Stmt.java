package technology.sola.script.parser;

import java.util.List;

public interface Stmt {
  <R> R accept(Visitor<R> visitor);

  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
  }

  record Block(List<Stmt> statements) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }
}
