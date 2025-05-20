package technology.sola.script.parser;

import org.jspecify.annotations.NullMarked;
import technology.sola.script.tokenizer.Token;

import java.util.stream.Collectors;

/**
 * ParserResultPrinter serializes a {@link ParserResult} in a human-readable way.
 */
@NullMarked
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
    public String function(Stmt.Function stmt) {
      return "fun " + stmt.name().lexeme()
        + "(" + stmt.parameters().stream().map(Token::lexeme).collect(Collectors.joining(",")) + ")"
        + " {\n" + stmt.body().stream().map(ParserResultPrinter.this::print).collect(Collectors.joining("\n"))
        + "\n}";
    }

    @Override
    public String var(Stmt.Var stmt) {
      if (stmt.initializer() == null) {
        return "var " + stmt.name().lexeme();
      }

      return "var " + stmt.name().lexeme() + " = " + print(stmt.initializer());
    }

    @Override
    public String val(Stmt.Val stmt) {
      return "val " + stmt.name().lexeme() + " = " + print(stmt.initializer());
    }

    @Override
    public String expression(Stmt.Expression stmt) {
      return print(stmt.expr());
    }

    @Override
    public String ifVisit(Stmt.If stmt) {
      var serialized = "if (" + print(stmt.condition()) + ") " + print(stmt.thenBranch());

      if (stmt.elseBranch() != null) {
        serialized = serialized + "\nelse " + print(stmt.elseBranch());
      }

      return serialized;
    }

    @Override
    public String returnVisit(Stmt.Return stmt) {
      var value = stmt.value() == null ? "" : " " + print(stmt.value());

      return "return" + value;
    }

    @Override
    public String whileVisit(Stmt.While stmt) {
      return "while (" + print(stmt.condition()) + ") " + print(stmt.body());
    }

    @Override
    public String block(Stmt.Block stmt) {
      if (stmt.statements().isEmpty()) {
        return "{}";
      }

      return "{\n  " + stmt.statements().stream().map(ParserResultPrinter.this::print).collect(Collectors.joining("\n  ")) + "\n}";
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
