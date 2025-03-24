package technology.sola.script.parser;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
  private final List<Token> tokens;
  private final List<Stmt> statements = new ArrayList<>();
  private final List<ScriptError> errors = new ArrayList<>();
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public ParserResult parse() {
    if (!statements.isEmpty()) {
      return new ParserResult(statements, errors);
    }

    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return new ParserResult(statements, errors);
  }


  // declarations, statements, expressions below -----------------------------------------------------------

  private Stmt declaration() {
    try {
      // todo implement
      throw new UnsupportedOperationException("Not supported yet.");
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }


  // "hardware" methods below ------------------------------------------------------------------------------

  private Token eat(TokenType tokenType, String message) {
    if (check(tokenType)) {
      return advance();
    }

    errors.add(new ScriptError(ScriptErrorType.PARSE, previous(), message));

    throw new ParseError();
  }

  private Token advance() {
    if (!isAtEnd()) {
      current++;
    }

    return previous();
  }

  private boolean advanceExpected(TokenType... expected) {
    for (TokenType tokenType : expected) {
      if (check(tokenType)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean check(TokenType tokenType) {
    return peek().type() == tokenType;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private boolean isAtEnd() {
    return peek().type() == TokenType.EOF;
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type() == TokenType.SEMICOLON) {
        return;
      }

      switch (peek().type()) {
        // declarations
        case CLASS:
        case FUN:
        case VAR:
        case VAL:
          // "top level" statements
        case IF:
        case FOR:
        case RETURN:
        case WHILE:
          return;
      }

      advance();
    }
  }

  private static class ParseError extends RuntimeException {
  }
}
