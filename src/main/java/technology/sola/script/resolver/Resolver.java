package technology.sola.script.resolver;

import technology.sola.script.error.ScriptError;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.ArrayList;
import java.util.List;

public class Resolver {
  private final ScriptRuntime scriptRuntime;
  private final StatementResolver statementResolver = new StatementResolver();
  private final ExpressionResolver expressionResolver = new ExpressionResolver();
  private List<ScriptError> errors = new ArrayList<>();

  public Resolver(ScriptRuntime scriptRuntime) {
    this.scriptRuntime = scriptRuntime;
  }

  public List<ScriptError> resolve(List<Stmt> statements) {
    for (var statement : statements) {
      statement.accept(statementResolver);
    }

    return errors;
  }

  private class StatementResolver implements Stmt.Visitor<Void> {
    @Override
    public Void var(Stmt.Var stmt) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void expression(Stmt.Expression stmt) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void block(Stmt.Block stmt) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }

  private class ExpressionResolver implements Expr.Visitor<Void> {
    @Override
    public Void set(Expr.Set expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void assign(Expr.Assign expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void logical(Expr.Logical expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void binary(Expr.Binary expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void unary(Expr.Unary expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void call(Expr.Call expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void get(Expr.Get expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void thisVisit(Expr.This expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void superVisit(Expr.Super expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void variable(Expr.Variable expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void grouping(Expr.Grouping expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void literal(Expr.Literal expr) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
