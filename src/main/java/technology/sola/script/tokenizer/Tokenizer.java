package technology.sola.script.tokenizer;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
  private final KeywordMap keywordMap = new KeywordMap();
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private final List<ScriptError> errors = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;
  private int column = 1;

  public Tokenizer(String source) {
    this.source = source;
  }

  public TokenizeResult tokenize() {
    while (!isAtEnd()) {
      start = current;
      nextToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line, column));

    return new TokenizeResult(tokens, errors);
  }

  private void nextToken() {
    char c = advance();

    switch (c) {
      // single character
      case '(': addToken(TokenType.LEFT_PAREN); break;

      // single or double character
      case '!': addToken(advanceExpected('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;

      // double character
      case '&':
        if (advanceExpected('&')) {
          addToken(TokenType.AMP_AMP);
        }
        break;
      case '|':
        if (advanceExpected('|')) {
          addToken(TokenType.BAR_BAR);
        }
        break;

      // special cases
      case '/':
        if (advanceExpected('/')) {
          while (peek() != '\n' && !isAtEnd()) {
            advance();
          }
        } else {
          addToken(TokenType.SLASH);
        }

        break;

      // whitespace
      case ' ':
      case '\r':
      case '\t':
        break;
      case '\n':
        line++;
        column = 1;
        break;

      default:
        errors.add(new ScriptError(ScriptErrorType.PARSE, line, column, "Unexpected character '" + c + "'"));
        break;
    }
  }

  // todo tokenString
  // todo tokenNumber
  // todo tokenIdentifier

  private char advance() {
    column++;
    return source.charAt(current++);
  }

  private boolean advanceExpected(char expected) {
    if (isAtEnd()) {
      return false;
    }

    if (source.charAt(current) != expected) {
      return false;
    }

    column++;
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

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String lexeme = source.substring(start, current);

    tokens.add(new Token(type, lexeme, literal, line, column));
  }
}
