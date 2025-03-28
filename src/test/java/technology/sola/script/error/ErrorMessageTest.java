package technology.sola.script.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMessageTest {
  @Test
  void unexpectedCharacter() {
    assertEquals(
      "Unexpected character 'a'.",
      ErrorMessage.UNEXPECTED_CHARACTER.formatMessage('a')
    );
  }
}
