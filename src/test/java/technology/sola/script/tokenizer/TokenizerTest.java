package technology.sola.script.tokenizer;

import org.junit.jupiter.api.Nested;
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
      ( ) { } , . - + ; *
      """;

    new TokenizerTester()
      .next(TokenType.LEFT_PAREN)
      .next(TokenType.RIGHT_PAREN)
      .next(TokenType.LEFT_BRACE)
      .next(TokenType.RIGHT_BRACE)
      .next(TokenType.COMMA)
      .next(TokenType.DOT)
      .next(TokenType.MINUS)
      .next(TokenType.PLUS)
      .next(TokenType.SEMICOLON)
      .next(TokenType.STAR)
      .next(TokenType.EOF)
      .verify(source);
  }

  @Test
  void singleOrDoubleCharacter() {
    var source = """
      ! !=
      = ==
      < <=
      > >=
      """;

    new TokenizerTester()
      .next(TokenType.BANG)
      .next(TokenType.BANG_EQUAL)
      .next(TokenType.EQUAL)
      .next(TokenType.EQUAL_EQUAL)
      .next(TokenType.LESS)
      .next(TokenType.LESS_EQUAL)
      .next(TokenType.GREATER)
      .next(TokenType.GREATER_EQUAL)
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

  @Nested
  class specialCases {
    @Test
    void slash() {
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

    @Nested
    class string {
      @Test
      void validString() {
        var source = """
          "This is a test string"
        """;

        new TokenizerTester()
          .next(TokenType.STRING, "This is a test string")
          .next(TokenType.EOF)
          .verify(source);
      }

      @Test
      void invalidString() {
        var source = """
            "This is a test string
          """;

        new TokenizerTester()
          .nextError(2, 2, "Unterminated string.")
          .next(TokenType.EOF)
          .verify(source);
      }
    }
  }

  private record ExpectedToken(TokenType type, Object literal) {
  }

  private record ExpectedError(int line, int column, String message) {
  }

  private static class TokenizerTester {
    private final List<ExpectedToken> expectedTokens = new ArrayList<>();
    private final List<ExpectedError> expectedErrors = new ArrayList<>();

    TokenizerTester next(TokenType type) {
      expectedTokens.add(new ExpectedToken(type, null));

      return this;
    }

    TokenizerTester next(TokenType type, Object literal) {
      expectedTokens.add(new ExpectedToken(type, literal));

      return this;
    }

    TokenizerTester nextError(int line, int column, String message) {
      expectedErrors.add(new ExpectedError(line, column, message));

      return this;
    }

    void verify(String source) {
      var tokenizer = new Tokenizer(source);
      var result = tokenizer.tokenize();

      assertEquals(expectedErrors.size(), result.errors().size());

      for (int i = 0; i < expectedErrors.size(); i++) {
        var expectedError = expectedErrors.get(i);

        assertEquals(expectedError.line, result.errors().get(i).line());
        assertEquals(expectedError.column, result.errors().get(i).column());
        assertEquals(expectedError.message, result.errors().get(i).message());
      }

      assertEquals(expectedTokens.size(), result.tokens().size());

      for (int i = 0; i < expectedTokens.size(); i++) {
        assertEquals(expectedTokens.get(i).type, result.tokens().get(i).type());
      }
    }
  }
}
