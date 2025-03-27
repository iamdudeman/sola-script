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
    public String set(Expr.Set expr) {
      return print(expr.object()) + "." + expr.name().lexeme() + " = " + print(expr.value());
    }

    @Override
    public String assign(Expr.Assign expr) {
      return expr.name().lexeme() + " = " + print(expr.value());
    }

    @Override
    public String logical(Expr.Logical expr) {
      return print(expr.left()) + " " + expr.operator().lexeme() + " " + print(expr.right());
    }

    @Override
    public String binary(Expr.Binary expr) {
      return print(expr.left()) + " " + expr.operator().lexeme() + " " + print(expr.right());
    }

    @Override
    public String unary(Expr.Unary expr) {
      return expr.operator().lexeme() + print(expr.right());
    }

    @Override
    public String call(Expr.Call expr) {
      var arguments = expr.arguments()
        .stream()
        .map(ParserResultPrinter.this::print)
        .collect(Collectors.joining(", "));

      return print(expr.callee()) + "(" + arguments +  ")";
    }

    @Override
    public String get(Expr.Get expr) {
      return print(expr.object()) + "." + expr.name().lexeme();
    }

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
      if (expr.value() == null) {
        return "null";
      }

      var value = expr.value().toString();

      if (expr.value() instanceof Double) {
        return value.endsWith(".0") ? value.substring(0, value.length() - 2) : value;
      }

      return value;
    }
  }
}
