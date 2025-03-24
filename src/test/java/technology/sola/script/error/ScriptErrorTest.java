package technology.sola.script.error;

import org.junit.jupiter.api.Test;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScriptErrorTest {
  @Test
  void shouldFormatWithExpectedType() {
    var token = new Token(TokenType.EOF, "", null, 10, 1);
    var message = "Test error message";
    var scriptError = new ScriptError(ScriptErrorType.SEMANTIC, token, message);

    assertEquals(
      "[10:1] SEMANTIC: Test error message",
      scriptError.toString()
    );
  }
}
