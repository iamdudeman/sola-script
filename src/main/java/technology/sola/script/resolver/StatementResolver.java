package technology.sola.script.resolver;

import technology.sola.script.error.ScriptError;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.List;

class StatementResolver implements Stmt.Visitor<Void> {
  private final ScriptRuntime scriptRuntime;
  private final ExpressionResolver expressionResolver;
  private final List<ScriptError> errors;

  StatementResolver(ScriptRuntime scriptRuntime, ExpressionResolver expressionResolver, List<ScriptError> errors) {
    this.scriptRuntime = scriptRuntime;
    this.expressionResolver = expressionResolver;
    this.errors = errors;
  }

  @Override
  public Void var(Stmt.Var stmt) {
    // todo
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Void expression(Stmt.Expression stmt) {
    stmt.expr().accept(expressionResolver);

    return null;
  }

  @Override
  public Void block(Stmt.Block stmt) {
    // todo
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
