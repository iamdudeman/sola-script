package technology.sola.script.parser;

public class AstPrinter {
  private final StmtPrinter stmtPrinter = new StmtPrinter();
  private final ExprPrinter exprPrinter = new ExprPrinter();

  public String print(Stmt stmt) {
    return stmt.accept(stmtPrinter);
  }

  public String print(Expr expr) {
    return expr.accept(exprPrinter);
  }

  private class StmtPrinter implements Stmt.Visitor<String> {
    @Override
    public String expression(Stmt.Expression expr) {
      return print(expr.expr());
    }
  }

  private class ExprPrinter implements Expr.Visitor<String> {
    @Override
    public String thisVisit(Expr.This expr) {
      return "this";
    }

    @Override
    public String superVisit(Expr.Super expr) {
      return "super." + expr.method().lexeme();
    }

    @Override
    public String variable(Expr.Variable expr) {
      return expr.name().lexeme();
    }

    @Override
    public String grouping(Expr.Grouping expr) {
      return "(" + print(expr.expression()) + ")";
    }

    @Override
    public String literal(Expr.Literal expr) {
      return expr.value() == null ? "null" : expr.value().toString();
    }
  }
}
