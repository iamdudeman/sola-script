package technology.sola.script.interpreter;

import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.runtime.SolaScriptFunction;

class StatementInterpreter implements Stmt.Visitor<Void> {
  private final ScriptRuntime scriptRuntime;
  private final ExpressionInterpreter expressionInterpreter;

  StatementInterpreter(ScriptRuntime scriptRuntime, ExpressionInterpreter expressionInterpreter) {
    this.scriptRuntime = scriptRuntime;
    this.expressionInterpreter = expressionInterpreter;
  }

  void execute(Stmt statement) {
    statement.accept(this);
  }

  @Override
  public Void function(Stmt.Function stmt) {
    SolaScriptFunction solaScriptFunction = new SolaScriptFunction(this::execute, scriptRuntime, stmt);

    scriptRuntime.defineVariable(stmt.name().lexeme(), solaScriptFunction);

    return null;
  }

  @Override
  public Void var(Stmt.Var stmt) {
    Object value = null;

    if (stmt.initializer() != null) {
      value = expressionInterpreter.evaluate(stmt.initializer());
    }

    scriptRuntime.defineVariable(stmt.name().lexeme(), value);

    return null;
  }

  @Override
  public Void expression(Stmt.Expression stmt) {
    expressionInterpreter.evaluate(stmt.expr());
    return null;
  }

  @Override
  public Void ifVisit(Stmt.If stmt) {
    var condition = stmt.condition().accept(expressionInterpreter);

    if (ValueUtils.isTruthy(condition)) {
      stmt.thenBranch().accept(this);
    } else if (stmt.elseBranch() != null) {
      stmt.elseBranch().accept(this);
    }

    return null;
  }

  @Override
  public Void whileVisit(Stmt.While stmt) {
    while (ValueUtils.isTruthy(stmt.condition().accept(expressionInterpreter))) {
      stmt.body().accept(this);
    }

    return null;
  }

  @Override
  public Void block(Stmt.Block stmt) {
    var handle = scriptRuntime.createNestedEnvironment();

    try {
      for (var statement : stmt.statements()) {
        execute(statement);
      }
    } finally {
      scriptRuntime.restoreEnvironment(handle);
    }

    return null;
  }
}
