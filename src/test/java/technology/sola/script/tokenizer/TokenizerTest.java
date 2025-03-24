package technology.sola.script.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenizerTest {
  @Test
  void whitespace() {
    var source = "\t \n \t \r   \n";

    new TokenizerTester()
      .next(TokenType.EOF)
      .verify(source);
  }

  @Test
  void singleCharacter() {
    var source = """
      (
      """;

    new TokenizerTester()
      .next(TokenType.LEFT_PAREN)
      .next(TokenType.EOF)
      .verify(source);
  }

  @Test
  void singleOrDoubleCharacter() {
    var source = """
      ! !=
      """;

    new TokenizerTester()
      .next(TokenType.BANG)
      .next(TokenType.BANG_EQUAL)
      .next(TokenType.EOF)
      .verify(source);
  }

  @Test
  void doubleCharacter() {
    var source = """
      && ||
      """;

    new TokenizerTester()
      .next(TokenType.AMP_AMP)
      .next(TokenType.BAR_BAR)
      .next(TokenType.EOF)
      .verify(source);
  }

  @Test
  void slashSpecialCase() {
    var source = """
      // this is all ignored
      /
      // this is also ignored
      """;

    new TokenizerTester()
      .next(TokenType.SLASH)
      .next(TokenType.EOF)
      .verify(source);
  }

  private record ExpectedToken(TokenType type, Object literal) {
  }

  private static class TokenizerTester {
    private List<ExpectedToken> expectedTokens = new ArrayList<>();

    TokenizerTester next(TokenType type) {
      expectedTokens.add(new ExpectedToken(type, null));

      return this;
    }

    TokenizerTester next(TokenType type, Object literal) {
      expectedTokens.add(new ExpectedToken(type, literal));

      return this;
    }

    void verify(String source) {
      var tokenizer = new Tokenizer(source);
      var result = tokenizer.tokenize();

      assertEquals(0, result.errors().size(), "Expected no errors.");
      assertEquals(expectedTokens.size(), result.tokens().size());

      for (int i = 0; i < expectedTokens.size(); i++) {
        assertEquals(expectedTokens.get(i).type, result.tokens().get(i).type());
      }
    }
  }
}
