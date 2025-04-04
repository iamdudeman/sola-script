package technology.sola.script.tokenizer;

/**
 * TokenType contains all recognized token types for sola script.
 */
public enum TokenType {
  // single character tokens
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
  COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

  // One or two character tokens
  BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,
  AMP_AMP, BAR_BAR,

  // Literals
  IDENTIFIER, STRING, NUMBER,

  // Keywords - declarations
  FUN, VAR, VAL,

  // Keywords - statements
  ELSE, FOR, IF, RETURN, WHILE,

  // Keywords - values
  FALSE, NULL, TRUE,

  // Special end of file
  EOF
}
