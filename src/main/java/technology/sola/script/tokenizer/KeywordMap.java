package technology.sola.script.tokenizer;

import java.util.HashMap;
import java.util.Map;

class KeywordMap {
  private final Map<String, TokenType> keywords = new HashMap<>();

  KeywordMap() {
    // declarations
    keywords.put("class", TokenType.CLASS);
    keywords.put("fun", TokenType.FUN);
    keywords.put("var", TokenType.VAR);
    keywords.put("val", TokenType.VAL);

    // statements
    keywords.put("else", TokenType.ELSE);
    keywords.put("for", TokenType.FOR);
    keywords.put("if", TokenType.IF);
    keywords.put("return", TokenType.RETURN);
    keywords.put("while", TokenType.WHILE);

    // values
    keywords.put("false", TokenType.FALSE);
    keywords.put("null", TokenType.NULL);
    keywords.put("super", TokenType.SUPER);
    keywords.put("this", TokenType.THIS);
    keywords.put("true", TokenType.TRUE);
  }

  /**
   * Returns a specific keyword {@link TokenType} if the identifier is a keyword. Otherwise, it will return
   * {@link TokenType#IDENTIFIER}.
   *
   * @param identifier the identifier that might be a keyword
   * @return the specific keyword token type or {@link TokenType#IDENTIFIER}
   */
  TokenType getTokenType(String identifier) {
    return keywords.getOrDefault(identifier, TokenType.IDENTIFIER);
  }
}
