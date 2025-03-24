package technology.sola.script.tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {
  private final KeywordMap keywordMap = new KeywordMap();
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;
  private int column = 1;

  public Tokenizer(String source) {
    this.source = source;
  }

  public List<Token> tokenize() {
    while (!isAtEnd()) {
      start = current;
      nextToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line, column));

    return tokens;
  }

  private void nextToken() {
    char c = advance();

    switch (c) {

      // whitespace
      case '\n':
        line++;
        column = 1;
        break;
    }

    throw new UnsupportedOperationException("not implemented yet");
  }

  // todo tokenString
  // todo tokenNumber
  // todo tokenIdentifier

  private char advance() {
    return source.charAt(current++);
  }

  private boolean advanceExpected(char expected) {
    if (isAtEnd()) {
      return false;
    }

    if (source.charAt(current) != expected) {
      return false;
    }

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }

    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) {
      return '\0';
    }

    return source.charAt(current + 1);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlpha(char c) {
    return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }
}
