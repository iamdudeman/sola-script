package technology.sola.script.error;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageTest {
  @Nested
  class formatMessage {
    @Test
    void noArguments() {
      assertEquals(
        "Expect expression.",
        ErrorMessage.EXPECT_EXPRESSION.formatMessage()
      );
    }

    @Test
    void withArguments() {
      assertEquals(
        "Unexpected character 'a'.",
        ErrorMessage.UNEXPECTED_CHARACTER.formatMessage('a')
      );
    }
  }
}
