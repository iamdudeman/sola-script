package technology.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
  private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

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

  public Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line));

    return tokens;
  }

  private void scanToken() {
    char c = advance();

    switch (c) {
      case '(': addToken(TokenType.LEFT_PAREN); break;
      case ')': addToken(TokenType.RIGHT_PAREN); break;
      case '{': addToken(TokenType.LEFT_BRACE); break;
      case '}': addToken(TokenType.RIGHT_BRACE); break;
      case ',': addToken(TokenType.COMMA); break;
      case '.': addToken(TokenType.DOT); break;
      case '-': addToken(TokenType.MINUS); break;
      case '+': addToken(TokenType.PLUS); break;
      case ';': addToken(TokenType.SEMICOLON); break;
      case '*': addToken(TokenType.STAR); break;

      case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
      case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
      case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
      case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;

      case '/':
        if (match('/')) {
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
        break;

      case '"':
        string(); break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Lox.error(line, "Unexpected character '" + c + "'");
        }
        break;
    }
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }

    String text = source.substring(start, current);
    TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);

    addToken(type);
  }

  private void number() {
    while (isDigit(peek())) {
      advance();
    }

    if (peek() == '.' && isDigit(peekNext())) {
      advance();

      while (isDigit(peek())) {
        advance();
      }
    }

    addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }

      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    advance();

    String value = source.substring(start + 1, current - 1);

    addToken(TokenType.STRING, value);
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

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);

    tokens.add(new Token(type, text, literal, line));
  }
}

