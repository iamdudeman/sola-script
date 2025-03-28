package technology.sola.script.tokenizer;

import technology.sola.script.error.ErrorMessage;
import technology.sola.script.error.ScriptError;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer provides functionality for tokenizing a source string into {@link Token}s to be parsed and interpreted.
 */
public class Tokenizer {
  private final KeywordMap keywordMap = new KeywordMap();
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private final List<ScriptError> errors = new ArrayList<>();
  private int start = 0;
  private int startColumn = 1;
  private int current = 0;
  private int line = 1;
  private int column = 1;

  /**
   * Creates a Tokenizer instance for desired source string.
   *
   * @param source the source
   */
  public Tokenizer(String source) {
    this.source = source;
  }

  /**
   * Tokenizes the source string and includes any errors found.
   *
   * @return the {@link TokenizeResult}
   */
  public TokenizeResult tokenize() {
    if (!tokens.isEmpty()) {
      return new TokenizeResult(tokens, errors);
    }

    while (!isAtEnd()) {
      start = current;
      startColumn = column;
      nextToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line, startColumn));

    return new TokenizeResult(tokens, errors);
  }

  private void nextToken() {
    char c = advance();

    switch (c) {
      // single character
      case '(':
        addToken(TokenType.LEFT_PAREN);
        break;
      case ')':
        addToken(TokenType.RIGHT_PAREN);
        break;
      case '{':
        addToken(TokenType.LEFT_BRACE);
        break;
      case '}':
        addToken(TokenType.RIGHT_BRACE);
        break;
      case ',':
        addToken(TokenType.COMMA);
        break;
      case '.':
        addToken(TokenType.DOT);
        break;
      case '-':
        addToken(TokenType.MINUS);
        break;
      case '+':
        addToken(TokenType.PLUS);
        break;
      case ';':
        addToken(TokenType.SEMICOLON);
        break;
      case '*':
        addToken(TokenType.STAR);
        break;

      // single or double character
      case '!':
        addToken(advanceExpected('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        break;
      case '=':
        addToken(advanceExpected('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        break;
      case '<':
        addToken(advanceExpected('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        break;
      case '>':
        addToken(advanceExpected('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        break;

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

      case '"':
        tokenString();
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
        if (isDigit(c)) {
          tokenNumber();
        } else if (isAlpha(c)) {
          tokenIdentifier();
        } else {
          errors.add(new ScriptError(ErrorMessage.UNEXPECTED_CHARACTER, line, startColumn, c));
        }

        break;
    }
  }

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

    tokens.add(new Token(type, lexeme, literal, line, startColumn));
  }

  private void tokenString() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
        column = 1;
      }

      advance();
    }

    if (isAtEnd()) {
      errors.add(new ScriptError(ErrorMessage.UNTERMINATED_STRING, line, startColumn));
      return;
    }

    advance();

    String value = source.substring(start + 1, current - 1);

    addToken(TokenType.STRING, value);
  }

  private void tokenNumber() {
    while (isDigit(peek())) {
      advance();
    }

    if (peek() == '.' && isDigit(peekNext())) {
      do {
        advance();
      } while (isDigit(peek()));
    }

    addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void tokenIdentifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }

    String lexeme = source.substring(start, current);
    TokenType type = keywordMap.getTokenType(lexeme);

    addToken(type);
  }
}
