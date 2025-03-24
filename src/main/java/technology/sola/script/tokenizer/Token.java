package technology.sola.script.tokenizer;

/**
 * Token holds information about a recognized token that sola script understands.
 *
 * @param type    the {@link TokenType}
 * @param lexeme  the text of the token in the source code
 * @param literal the value of the token (ie String for strings and float for numbers)
 * @param line    the line number the token was found on
 * @param column  the column number the token was found at
 */
public record Token(
  TokenType type,
  String lexeme,
  Object literal,
  int line,
  int column
) {
}
