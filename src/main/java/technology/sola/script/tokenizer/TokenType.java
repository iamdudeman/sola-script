package technology.sola.script.tokenizer;

/**
 * TokenType contains all recognized token types for sola script.
 */
public enum TokenType {
  // single character tokens
  /**
   * Single character token - "("
   */
  LEFT_PAREN,
  /**
   * Single character token - ")"
   */
  RIGHT_PAREN,
  /**
   * Single character token - "{"
   */
  LEFT_BRACE,
  /**
   * Single character token - "}"
   */
  RIGHT_BRACE,
  /**
   * Single character token - ","
   */
  COMMA,
  /**
   * Single character token - "."
   */
  DOT,
  /**
   * Single character token - "-"
   */
  MINUS,
  /**
   * Single character token - "+"
   */
  PLUS,
  /**
   * Single character token - ":"
   */
  COLON,
  /**
   * Single character token - ";"
   */
  SEMICOLON,
  /**
   * Single character token - "/"
   */
  SLASH,
  /**
   * Single character token - "*"
   */
  STAR,

  // One or two character tokens
  /**
   * Single or double character token - "!"
   */
  BANG,
  /**
   * Single or double character token - "!="
   */
  BANG_EQUAL,
  /**
   * Single or double character token - "="
   */
  EQUAL,
  /**
   * Single or double character token - "=="
   */
  EQUAL_EQUAL,
  /**
   * Single or double character token - ">"
   */
  GREATER,
  /**
   * Single or double character token - ">="
   */
  GREATER_EQUAL,
  /**
   * Single or double character token - "<"
   */
  LESS,
  /**
   * Single or double character token - "<="
   */
  LESS_EQUAL,
  /**
   * Single or double character token - "&&"
   */
  AMP_AMP,
  /**
   * Single or double character token - "||"
   */
  BAR_BAR,
  /**
   * Single or double character token - "?"
   */
  QUESTION,
  /**
   * Single or double character token - "??"
   */
  QUESTION_QUESTION,
  /**
   * Single or double character token - "?."
   */
  QUESTION_DOT,

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
