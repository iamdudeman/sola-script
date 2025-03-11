package technology.sola.script.tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {
  private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int current = 0;

  static {
    KEYWORDS.put("and", TokenType.AND);
    KEYWORDS.put("class", TokenType.CLASS);
    KEYWORDS.put("else", TokenType.ELSE);
    KEYWORDS.put("false", TokenType.FALSE);
    KEYWORDS.put("for", TokenType.FOR);
    KEYWORDS.put("fun", TokenType.FUN);
    KEYWORDS.put("if", TokenType.IF);
    KEYWORDS.put("nil", TokenType.NIL);
    KEYWORDS.put("or", TokenType.OR);
    KEYWORDS.put("print", TokenType.PRINT);
    KEYWORDS.put("return", TokenType.RETURN);
    KEYWORDS.put("super", TokenType.SUPER);
    KEYWORDS.put("this", TokenType.THIS);
    KEYWORDS.put("true", TokenType.TRUE);
    KEYWORDS.put("var", TokenType.VAR);
    KEYWORDS.put("while", TokenType.WHILE);
  }

  public Tokenizer(String source) {
    this.source = source;
  }

  // todo tokenize()

  // todo tokenString
  // todo tokenNumber
  // todo tokenIdentifier

  private char advance() {
    return source.charAt(current++);
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }

    return source.charAt(current);
  }

  private char peekNext() {
    if (current +1 >= source.length()) {
      return '\0';
    }

    return source.charAt(current + 1);
  }

  private boolean match(char expected) {
    if (isAtEnd()) {
      return false;
    }

    if (source.charAt(current) != expected) {
      return false;
    }

    current++;
    return true;
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
