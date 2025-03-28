package technology.sola.script.error;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScriptErrorTypeTest {
  @Nested
  class formatMessage {
    @Test
    void noArguments() {
      assertEquals(
        "Expect expression.",
        ScriptErrorType.EXPECT_EXPRESSION.formatMessage()
      );
    }

    @Test
    void withArguments() {
      assertEquals(
        "Unexpected character 'a'.",
        ScriptErrorType.UNEXPECTED_CHARACTER.formatMessage('a')
      );
    }
  }
}
