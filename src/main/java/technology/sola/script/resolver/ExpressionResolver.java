package technology.sola.script.resolver;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.parser.Expr;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.List;

class ExpressionResolver implements Expr.Visitor<Void> {
  private final ScriptRuntime scriptRuntime;
  private final List<ScriptError> errors;

  ExpressionResolver(ScriptRuntime scriptRuntime, List<ScriptError> errors) {
    this.scriptRuntime = scriptRuntime;
    this.errors = errors;
  }

  @Override
  public Void set(Expr.Set expr) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Void assign(Expr.Assign expr) {
    // todo
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Void logical(Expr.Logical expr) {
    expr.left().accept(this);
    expr.right().accept(this);

    return null;
  }

  @Override
  public Void binary(Expr.Binary expr) {
    expr.left().accept(this);
    expr.right().accept(this);

    return null;
  }

  @Override
  public Void unary(Expr.Unary expr) {
    expr.right().accept(this);

    return null;
  }

  @Override
  public Void call(Expr.Call expr) {
    expr.callee().accept(this);

    for (var argument : expr.arguments()) {
      argument.accept(this);
    }

    return null;
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
    var scopes = scriptRuntime.scopes();

    if (scopes.isSelfReferenceVariableInitialization(expr)) {
      errors.add(new ScriptError(ScriptErrorType.INVALID_SELF_INITIALIZATION, expr.name(), expr.name().lexeme()));
    }

    scopes.resolveLocal(expr, expr.name());

    return null;
  }

  @Override
  public Void grouping(Expr.Grouping expr) {
    expr.expression().accept(this);

    return null;
  }

  @Override
  public Void literal(Expr.Literal expr) {
    return null;
  }
}
