package technology.sola.script.interpreter;

import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.runtime.SolaScriptCallable;
import technology.sola.script.runtime.SolaScriptMap;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

class ExpressionInterpreter implements Expr.Visitor<Object> {
  private final ScriptRuntime scriptRuntime;

  public ExpressionInterpreter(ScriptRuntime scriptRuntime) {
    this.scriptRuntime = scriptRuntime;
  }

  Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object set(Expr.Set expr) {
    Object object = evaluate(expr.object());

    if (object instanceof SolaScriptMap solaScriptMap) {
      Object value = evaluate(expr.value());

      solaScriptMap.set(expr.name(), value);

      return value;
    }

    throw new ScriptInterpretationException(expr.name(), ScriptErrorType.ONLY_MAPS_HAVE_PROPERTIES);
  }

  @Override
  public Object assign(Expr.Assign expr) {
    Object value = evaluate(expr.value());

    scriptRuntime.assignVariable(expr, value);

    return value;
  }

  @Override
  public Object logical(Expr.Logical expr) {
    Object left = evaluate(expr.left());
    var operator = expr.operator().type();

    if (operator == TokenType.BAR_BAR) {
      if (ValueUtils.isTruthy(left)) {
        return left;
      }
    } else if (operator == TokenType.AMP_AMP) {
      if (!ValueUtils.isTruthy(left)) {
        return left;
      }
    } else {
      // should be unreachable
      throw new RuntimeException("Unknown operator token for logical expression: " + operator);
    }

    return evaluate(expr.right());
  }

  @Override
  public Object binary(Expr.Binary expr) {
    Object left = evaluate(expr.left());
    Object right = evaluate(expr.right());
    var operator = expr.operator();

    switch (operator.type()) {
      case GREATER: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left > (double) right;
      }
      case GREATER_EQUAL: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left >= (double) right;
      }
      case LESS: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left < (double) right;
      }
      case LESS_EQUAL: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left <= (double) right;
      }
      case EQUAL_EQUAL: {
        return ValueUtils.isEqual(left, right);
      }
      case BANG_EQUAL: {
        return !ValueUtils.isEqual(left, right);
      }
      case MINUS: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left - (double) right;
      }
      case PLUS: {
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }

        if (left instanceof String || right instanceof String) {
          return ValueUtils.stringify(left) + ValueUtils.stringify(right);
        }

        throw new ScriptInterpretationException(expr.operator(), ScriptErrorType.OPERANDS_MUST_BE_TWO_NUMBERS_OR_ONE_STRING);
      }
      case SLASH: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left / (double) right;
      }
      case STAR: {
        ValueUtils.assertNumberOperands(operator, left, right);
        return (double) left * (double) right;
      }
    }

    // should be unreachable
    throw new RuntimeException("Unknown operator token for binary expression: " + expr.operator());
  }

  @Override
  public Object unary(Expr.Unary expr) {
    Object right = evaluate(expr.right());

    return switch (expr.operator().type()) {
      case BANG -> !ValueUtils.isTruthy(right);
      case MINUS -> {
        ValueUtils.assertNumberOperand(expr.operator(), right);

        yield -(double) right;
      }
      // should be unreachable
      default -> throw new RuntimeException("Unknown operator token for unary expression: " + expr.operator());
    };
  }

  @Override
  public Object call(Expr.Call expr) {
    Object callee = evaluate(expr.callee());
    List<Object> arguments = new ArrayList<>();

    for (Expr argument : expr.arguments()) {
      arguments.add(evaluate(argument));
    }

    if (callee instanceof SolaScriptCallable solaScriptCallable) {
      if (arguments.size() != solaScriptCallable.arity()) {
        throw new ScriptInterpretationException(expr.paren(), ScriptErrorType.EXPECTED_ARGUMENTS_MISMATCH, solaScriptCallable.arity(), arguments.size());
      }

      return solaScriptCallable.call(arguments);
    }

    throw new ScriptInterpretationException(expr.paren(), ScriptErrorType.NOT_CALLABLE);
  }

  @Override
  public Object get(Expr.Get expr) {
    Object object = evaluate(expr.object());

    if (object instanceof SolaScriptMap solaScriptMap) {
      return solaScriptMap.get(expr.name());
    }

    throw new ScriptInterpretationException(expr.name(), ScriptErrorType.ONLY_MAPS_HAVE_PROPERTIES);
  }

  @Override
  public Object thisVisit(Expr.This expr) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public Object superVisit(Expr.Super expr) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public Object variable(Expr.Variable expr) {
    return scriptRuntime.lookUpVariable(expr);
  }

  @Override
  public Object grouping(Expr.Grouping expr) {
    return evaluate(expr.expression());
  }

  @Override
  public Object literal(Expr.Literal expr) {
    return expr.value();
  }
}
