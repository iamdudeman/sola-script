package technology.sola.script.resolver;

import org.jspecify.annotations.NullMarked;
import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;

import java.util.List;

@NullMarked
class StatementResolver implements Stmt.Visitor<Void> {
  private final ScriptRuntime scriptRuntime;
  private final ExpressionResolver expressionResolver;
  private final List<ScriptError> errors;
  private FunctionType currentFunction = FunctionType.NONE;

  StatementResolver(ScriptRuntime scriptRuntime, ExpressionResolver expressionResolver, List<ScriptError> errors) {
    this.scriptRuntime = scriptRuntime;
    this.expressionResolver = expressionResolver;
    this.errors = errors;
  }

  @Override
  public Void function(Stmt.Function stmt) {
    var scopes = scriptRuntime.scopes();

    scopes.declare(stmt.name());
    scopes.define(stmt.name());

    resolveFunction(stmt);

    return null;
  }

  @Override
  public Void var(Stmt.Var stmt) {
    var scopes = scriptRuntime.scopes();

    if (scopes.isDeclaredInScope(stmt.name())) {
      errors.add(new ScriptError(ScriptErrorType.ALREADY_DEFINED_VARIABLE, stmt.name(), stmt.name().lexeme()));
    }

    scopes.declare(stmt.name());

    if (stmt.initializer() != null) {
      stmt.initializer().accept(expressionResolver);
    }

    scopes.define(stmt.name());

    return null;
  }

  @Override
  public Void val(Stmt.Val stmt) {
    var scopes = scriptRuntime.scopes();

    if (scopes.isDeclaredInScope(stmt.name())) {
      errors.add(new ScriptError(ScriptErrorType.ALREADY_DEFINED_VARIABLE, stmt.name(), stmt.name().lexeme()));
    }

    scopes.declare(stmt.name());

    stmt.initializer().accept(expressionResolver);

    scopes.define(stmt.name());

    return null;
  }

  @Override
  public Void expression(Stmt.Expression stmt) {
    stmt.expr().accept(expressionResolver);

    return null;
  }

  @Override
  public Void ifVisit(Stmt.If stmt) {
    stmt.condition().accept(expressionResolver);
    stmt.thenBranch().accept(this);

    if (stmt.elseBranch() != null) {
      stmt.elseBranch().accept(this);
    }

    return null;
  }

  @Override
  public Void returnVisit(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      errors.add(new ScriptError(ScriptErrorType.CANNOT_RETURN_FROM_TOP_LEVEL, stmt.keyword()));
    }

    if (stmt.value() != null) {
      stmt.value().accept(expressionResolver);
    }

    return null;
  }

  @Override
  public Void whileVisit(Stmt.While stmt) {
    stmt.condition().accept(expressionResolver);
    stmt.body().accept(this);

    return null;
  }

  @Override
  public Void block(Stmt.Block stmt) {
    var scopes = scriptRuntime.scopes();

    scopes.beginScope();

    for (var statement : stmt.statements()) {
      statement.accept(this);
    }

    scopes.endScope();

    return null;
  }

  private void resolveFunction(Stmt.Function stmt) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = FunctionType.FUNCTION;

    var scopes = scriptRuntime.scopes();

    scopes.beginScope();

    // declare params and resolve body
    for (var parameter : stmt.parameters()) {
      scopes.declare(parameter);
      scopes.define(parameter);
    }

    for (var statement : stmt.body()) {
      statement.accept(this);
    }

    scopes.endScope();
    currentFunction = enclosingFunction;
  }

  private enum FunctionType {
    NONE,
    FUNCTION,
  }
}
