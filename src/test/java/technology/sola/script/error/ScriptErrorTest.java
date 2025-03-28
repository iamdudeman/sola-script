package technology.sola.script.error;

import org.junit.jupiter.api.Test;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScriptErrorTest {
  @Test
  void shouldFormatWithExpectedType() {
    var token = new Token(TokenType.EOF, "", null, 10, 1);
    var scriptError = new ScriptError(ScriptErrorType.EXPECT_EXPRESSION, token);

    assertEquals(
      "[10:1] PARSE: Expect expression.",
      scriptError.toString()
    );
  }
}
