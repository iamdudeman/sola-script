package technology.sola.script.interpreter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;

/**
 * ValueUtils contains various utility methods for sola-script values.
 */
public class ValueUtils {
  /**
   * Method to make a sola-script value into a string.
   * <ul>
   *   <li>null -> "null"</li>
   *   <li>true -> "true"</li>
   *   <li>12.0 -> 12</li>
   *   <li>12.35 -> 12.35</li>
   *   <li>"test" -> "test"</li>
   * </ul>
   *
   * @param value the value to stringify
   * @return the stringified value
   */
  @NonNull
  public static String stringify(@Nullable Object value) {
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

  /**
   * Checks to see if a value is considered "truthy" or not. A value is considered truthy if it is not null and not a
   * boolean false.
   *
   * @param value the value to check if truthy
   * @return true if truthy
   */
  public static boolean isTruthy(@Nullable Object value) {
    if (value == null) {
      return false;
    }

    if (value instanceof Boolean) {
      return (boolean) value;
    }

    return true;
  }

  static boolean isEqual(@Nullable Object a, @Nullable Object b) {
    if (a == null && b == null) {
      return true;
    }

    if (a == null) {
      return false;
    }

    return a.equals(b);
  }

  static void assertNumberOperand(Token operator, @Nullable Object operand) {
    if (operand instanceof Double) {
      return;
    }

    throw new ScriptInterpretationException(operator, ScriptErrorType.OPERAND_MUST_BE_NUMBER);
  }

  static void assertNumberOperands(Token operator, @Nullable Object left, @Nullable Object right) {
    if (left instanceof Double && right instanceof Double) {
      return;
    }

    throw new ScriptInterpretationException(operator, ScriptErrorType.OPERANDS_MUST_BE_NUMBERS);
  }

  private ValueUtils() {
  }
}
