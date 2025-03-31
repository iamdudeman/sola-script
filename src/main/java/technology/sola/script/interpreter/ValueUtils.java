package technology.sola.script.interpreter;

import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;

public class ValueUtils {
  public static String stringify(Object value) {
    if (value == null) {
      return "null";
    }

    if (value instanceof Double) {
      String text = value.toString();

      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }

      return text;
    }

    return value.toString();
  }

  static void assertNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) {
      return;
    }

    throw new ScriptInterpretationException(operator, ScriptErrorType.OPERAND_MUST_BE_NUMBER);
  }

  static void assertNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) {
      return;
    }

    throw new ScriptInterpretationException(operator, ScriptErrorType.OPERANDS_MUST_BE_NUMBERS);
  }

  static boolean isTruthy(Object value) {
    if (value == null) {
      return false;
    }

    if (value instanceof Boolean) {
      return (boolean) value;
    }

    return true;
  }

  static boolean isEqual(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }

    if (a == null) {
      return false;
    }

    return a.equals(b);
  }

  private ValueUtils() {
  }
}
