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
   * Single or double character token - "&lt;"
   */
  LESS,
  /**
   * Single or double character token - "&lt;="
   */
  LESS_EQUAL,
  /**
   * Single or double character token - "&amp;&amp;"
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
  /**
   * An identifier for a function, variable or value - [a-zA-Z_][a-zA-Z0-9_]*
   */
  IDENTIFIER,
  /**
   * A string value - \".*\"
   */
  STRING,
  /**
   * A number value - [1-9][0-9]*(\.[0-9]+)?
   */
  NUMBER,

  // Keywords - declarations
  /**
   * Keyword for function declaration - "fun"
   */
  FUN,
  /**
   * Keyword for variable declaration - "var"
   */
  VAR,
  /**
   * Keyword for value (constant) declaration - "val"
   */
  VAL,

  // Keywords - statements
  /**
   * Keyword - "else"
   */
  ELSE,
  /**
   * Keyword - "for"
   */
  FOR,
  /**
   * Keyword - "if"
   */
  IF,
  /**
   * Keyword - "return"
   */
  RETURN,
  /**
   * Keyword - "while"
   */
  WHILE,

  // Keywords - values
  /**
   * Keyword - "false"
   */
  FALSE,
  /**
   * Keyword - "null"
   */
  NULL,
  /**
   * Keyword - "true"
   */
  TRUE,

  /**
   * Special end of file
   */
  EOF
}
