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
      var declaration = stmtDeclaration();

      if (declaration != null) {
        statements.add(declaration);
      }
    }

    return new ParserResult(statements, errors);
  }


  // declarations, statements, expressions below -----------------------------------------------------------

  private Stmt stmtDeclaration() {
    try {
      // todo function
      // todo class
      // todo var
      // todo val

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt statement() {
    // todo if
    // todo while
    // todo for
    // todo return
    // todo block

    return stmtExpression();
  }

  private Stmt stmtExpression() {
    var expr = expression();

    eat(TokenType.SEMICOLON, "Expect ';' after expression.");

    return new Stmt.Expression(expr);
  }

  private Expr expression() {
    // todo replace with real implementation
    return exprPrimary();
  }

  private Expr exprPrimary() {
    if (advanceExpected(TokenType.FALSE)) {
      return new Expr.Literal(false);
    }

    if (advanceExpected(TokenType.TRUE)) {
      return new Expr.Literal(true);
    }

    if (advanceExpected(TokenType.NULL)) {
      return new Expr.Literal(null);
    }

    if (advanceExpected(TokenType.NUMBER, TokenType.STRING)) {
      return new Expr.Literal(previous().literal());
    }

    if (advanceExpected(TokenType.LEFT_PAREN)) {
      var expr = expression();

      eat(TokenType.RIGHT_PAREN, "Expect ')' after expression.");

      return new Expr.Grouping(expr);
    }

    if (advanceExpected(TokenType.IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (advanceExpected(TokenType.THIS)) {
      return new Expr.This(previous());
    }

    if (advanceExpected(TokenType.SUPER)) {
      var keyword = previous();

      eat(TokenType.DOT, "Expect '.' after super.");

      var method = eat(TokenType.IDENTIFIER, "Expect superclass method name.");

      return new Expr.Super(keyword, method);
    }

    errors.add(new ScriptError(ScriptErrorType.PARSE, previous(), "Expect expression."));

    throw new ParseError();
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
