package technology.sola.script.interpreter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import static org.junit.jupiter.api.Assertions.*;

class ValueUtilsTest {
  @Nested
  class assertNumberOperand {
    @Test
    void whenDouble_shouldNotThrow() {
      Token token = new Token(TokenType.BANG, "!", null, 1, 1);

      assertDoesNotThrow(() -> ValueUtils.assertNumberOperand(token, 12.d));
    }

    @Test
    void whenNotDouble_shouldNotThrow() {
      Token token = new Token(TokenType.BANG, "!", null, 1, 1);

      assertThrows(
        ScriptInterpretationException.class,
        () -> ValueUtils.assertNumberOperand(token, "test")
      );
    }
  }

  @Nested
  class assertNumberOperands {
    @Test
    void whenDouble_shouldNotThrow() {
      Token token = new Token(TokenType.MINUS, "-", null, 1, 1);

      assertDoesNotThrow(() -> ValueUtils.assertNumberOperands(token, 12.d, 12.d));
    }

    @Test
    void whenNotDouble_shouldNotThrow() {
      Token token = new Token(TokenType.MINUS, "-", null, 1, 1);

      assertThrows(
        ScriptInterpretationException.class,
        () -> ValueUtils.assertNumberOperands(token, 12.d, "test")
      );
    }
  }

  @Nested
  class isTruthy {
    @Test
    void whenNull_shouldReturnFalse() {
      assertFalse(ValueUtils.isTruthy(null));
    }

    @Test
    void whenBoolean_shouldReturnValue() {
      assertTrue(ValueUtils.isTruthy(true));
      assertFalse(ValueUtils.isTruthy(false));
    }

    @Test
    void whenNotNullOrBoolean_shouldReturnTrue() {
      assertTrue(ValueUtils.isTruthy(12.32));
      assertTrue(ValueUtils.isTruthy("test"));
    }
  }

  @Nested
  class isEqual {
    @Test
    void whenBothNull_shouldReturnTrue() {
      assertTrue(ValueUtils.isEqual(null, null));
    }

    @Test
    void whenLeftNull_shouldReturnTrue() {
      assertFalse(ValueUtils.isEqual(null, "test"));
    }

    @Test
    void whenLeftNotNull_shouldReturnEqualsCheck() {
      assertTrue(ValueUtils.isEqual("test", "test"));
      assertTrue(ValueUtils.isEqual(12.32d, 12.32d));
      assertTrue(ValueUtils.isEqual(true, true));

      assertFalse(ValueUtils.isEqual("test", "test2"));
      assertFalse(ValueUtils.isEqual(12.32d, 1d));
      assertFalse(ValueUtils.isEqual(true, false));
    }
  }

  @Nested
  class stringify {
    @Test
    void whenNull_shouldReturnStringNull() {
      assertEquals("null", ValueUtils.stringify(null));
    }

    @Test
    void whenDoubleWithDecimal_shouldReturnWithDecimal() {
      assertEquals("12.32", ValueUtils.stringify(12.32d));
    }

    @Test
    void whenDoubleWithNoDecimal_shouldReturnWithoutDecimal() {
      assertEquals("12", ValueUtils.stringify(12.0d));
    }

    @Test
    void whenNotNullOrDouble_shouldToString() {
      assertEquals("true", ValueUtils.stringify(true));
      assertEquals("false", ValueUtils.stringify(false));
      assertEquals("test", ValueUtils.stringify("test"));
    }
  }
}
