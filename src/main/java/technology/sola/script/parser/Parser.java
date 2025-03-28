package technology.sola.script.parser;

import technology.sola.script.error.ScriptError;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser takes a list of {@link Token}s and parses them into {@link Stmt}s that can be later interpreted.
 */
public class Parser {
  private final List<Token> tokens;
  private final List<Stmt> statements = new ArrayList<>();
  private final List<ScriptError> errors = new ArrayList<>();
  private int current = 0;

  /**
   * Creates an instance for a desired list of {@link Token}s.
   *
   * @param tokens the {@link Token}s for the program
   */
  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Parses the {@link Token}s into a {@link ParserResult} containing the list of identified {@link Stmt}s.
   *
   * @return the parsing result
   */
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


  // declarations, statements below ------------------------------------------------------------------------------------

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


  // expressions below -------------------------------------------------------------------------------------------------

  private Expr expression() {
    return exprAssignment();
  }

  private Expr exprAssignment() {
    Expr expr = exprLogicOr();

    if (advanceExpected(TokenType.EQUAL)) {
      Token equals = previous();
      Expr value = exprAssignment();

      if (expr instanceof Expr.Variable varExpr) {
        Token name = varExpr.name();

        return new Expr.Assign(name, value);
      } else if (expr instanceof Expr.Get getExpr) {
        return new Expr.Set(getExpr.object(), getExpr.name(), value);
      }

      errors.add(new ScriptError(ScriptErrorType.PARSE, equals, "Invalid assignment target."));
    }

    return expr;
  }

  private Expr exprLogicOr() {
    Expr expr = exprLogicAnd();

    while (advanceExpected(TokenType.BAR_BAR)) {
      Token operator = previous();
      Expr right = exprLogicAnd();

      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr exprLogicAnd() {
    Expr expr = exprEquality();

    while (advanceExpected(TokenType.AMP_AMP)) {
      Token operator = previous();
      Expr right = exprEquality();

      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr exprEquality() {
    Expr expr = exprComparison();

    while (advanceExpected(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = exprComparison();

      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr exprComparison() {
    Expr expr = exprTerm();

    while (advanceExpected(TokenType.GREATER, TokenType.LESS, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL)) {
      Token operator = previous();
      Expr right = exprTerm();

      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr exprTerm() {
    Expr expr = exprFactor();

    while (advanceExpected(TokenType.MINUS, TokenType.PLUS)) {
      Token operator = previous();
      Expr right = exprFactor();

      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr exprFactor() {
    Expr expr = exprUnary();

    while (advanceExpected(TokenType.SLASH, TokenType.STAR)) {
      Token operator = previous();
      Expr right = exprUnary();

      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr exprUnary() {
    if (advanceExpected(TokenType.BANG, TokenType.MINUS)) {
      Token operator = previous();
      Expr right = exprUnary();

      return new Expr.Unary(operator, right);
    }

    return exprCall();
  }

  private Expr exprCall() {
    Expr expr = exprPrimary();

    while (true) {
      if (advanceExpected(TokenType.LEFT_PAREN)) {
        List<Expr> arguments = new ArrayList<>();

        if (!check(TokenType.RIGHT_PAREN)) {
          do {
            if (arguments.size() >= ParserConstants.MAX_ARGUMENTS) {
              errors.add(new ScriptError(ScriptErrorType.SEMANTIC, peek(), "Can't have more than " + ParserConstants.MAX_ARGUMENTS + " arguments."));
            }

            arguments.add(expression());
          } while (advanceExpected(TokenType.COMMA));
        }

        Token paren = eat(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");

        expr = new Expr.Call(expr, paren, arguments);
      } else if (advanceExpected(TokenType.DOT)) {
        Token name = eat(TokenType.IDENTIFIER, "Expect property name after '.'.");

        expr = new Expr.Get(expr, name);
      } else {
        break;
      }
    }

    return expr;
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

    if (advanceExpected(
      TokenType.STAR, TokenType.SLASH,
      TokenType.PLUS, // note MINUS is okay since it is unary
      TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL,
      TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL,
      TokenType.AMP_AMP, TokenType.BAR_BAR,
      TokenType.EQUAL
    )) {

      errors.add(new ScriptError(ScriptErrorType.PARSE, previous(), "Binary expression missing left operand."));

      throw new ParseError();
    }

    errors.add(new ScriptError(ScriptErrorType.PARSE, previous(), "Expect expression."));

    throw new ParseError();
  }


  // "hardware" methods below ------------------------------------------------------------------------------------------

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
