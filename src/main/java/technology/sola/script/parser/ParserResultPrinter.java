package technology.sola.script.parser;

import java.util.stream.Collectors;

/**
 * ParserResultPrinter serializes a {@link ParserResult} in a human-readable way.
 */
public class ParserResultPrinter {
  private final StmtPrinter stmtPrinter = new StmtPrinter();
  private final ExprPrinter exprPrinter = new ExprPrinter();

  /**
   * Converts a {@link ParserResult} into a human-readable string.
   *
   * @param result the {@link ParserResult}
   * @return the stringified parser result
   */
  public String print(ParserResult result) {
    return result.statements().stream()
      .map(this::print)
      .collect(Collectors.joining("\n"));
  }

  private String print(Stmt stmt) {
    return stmt.accept(stmtPrinter);
  }

  private String print(Expr expr) {
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
