package technology.sola.script.parser;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.error.ScriptError;
import technology.sola.script.runtime.SolaScriptMap;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser takes a list of {@link Token}s and parses them into {@link Stmt}s that can be later interpreted.
 */
@NullMarked
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

    while (hasMoreTokens()) {
      var declaration = declaration();

      if (declaration != null) {
        statements.add(declaration);
      }
    }

    return new ParserResult(statements, errors);
  }


  // declarations, statements below ------------------------------------------------------------------------------------

  @Nullable
  private Stmt declaration() {
    try {
      if (advanceExpected(TokenType.FUN)) {
        return declFun();
      }

      if (advanceExpected(TokenType.VAR)) {
        return declVar();
      }

      if (advanceExpected(TokenType.VAL)) {
        return declVal();
      }

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt declFun() {
    Token name = eat(TokenType.IDENTIFIER, ScriptErrorType.EXPECT_NAME, "function");

    eat(TokenType.LEFT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_FUNCTION_NAME);

    List<Token> parameters = new ArrayList<>();

    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        if (parameters.size() >= ParserConstants.MAX_ARGUMENTS) {
          errors.add(new ScriptError(ScriptErrorType.TOO_MANY_ARGUMENTS, peek()));
        }

        parameters.add(eat(TokenType.IDENTIFIER, ScriptErrorType.EXPECT_NAME, "parameter"));
      } while (advanceExpected(TokenType.COMMA));
    }

    eat(TokenType.RIGHT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_PARAMETERS);
    eat(TokenType.LEFT_BRACE, ScriptErrorType.EXPECT_BRACE_BEFORE_FUNCTION_BODY);

    List<Stmt> body = parseBlockStatements();

    return new Stmt.Function(name, parameters, body);
  }

  private Stmt declVar() {
    Token name = eat(TokenType.IDENTIFIER, ScriptErrorType.EXPECT_NAME, "variable");
    Expr initializer = null;

    if (advanceExpected(TokenType.EQUAL)) {
      initializer = expression();
    }

    eat(TokenType.SEMICOLON, ScriptErrorType.EXPECT_SEMI_AFTER_VARIABLE_DECLARATION);

    return new Stmt.Var(name, initializer);
  }

  private Stmt declVal() {
    Token name = eat(TokenType.IDENTIFIER, ScriptErrorType.EXPECT_NAME, "constant");

    eat(TokenType.EQUAL, ScriptErrorType.EXPECT_INITIALIZER_EXPRESSION);

    Expr initializer = expression();

    eat(TokenType.SEMICOLON, ScriptErrorType.EXPECT_SEMI_AFTER_VARIABLE_DECLARATION);

    return new Stmt.Val(name, initializer);
  }

  private Stmt statement() {
    if (advanceExpected(TokenType.IF)) {
      return stmtIf();
    }

    if (advanceExpected(TokenType.WHILE)) {
      return stmtWhile();
    }

    // todo for

    if (advanceExpected(TokenType.RETURN)) {
      return stmtReturn();
    }

    if (advanceExpected(TokenType.LEFT_BRACE)) {
      return new Stmt.Block(parseBlockStatements());
    }

    return stmtExpression();
  }

  private Stmt stmtIf() {
    eat(TokenType.LEFT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_IF);

    Expr condition = expression();

    eat(TokenType.RIGHT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_CONDITION);

    Stmt thenBranch = statement();
    Stmt elseBranch = null;

    if (advanceExpected(TokenType.ELSE)) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt stmtReturn() {
    var keyword = previous();
    Expr returnExpression = null;

    if (!check(TokenType.SEMICOLON)) {
      returnExpression = expression();
    }

    eat(TokenType.SEMICOLON, ScriptErrorType.EXPECT_SEMI_AFTER_RETURN_VALUE);

    return new Stmt.Return(keyword, returnExpression);
  }

  private Stmt stmtWhile() {
    eat(TokenType.LEFT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_WHILE);

    Expr condition = expression();

    eat(TokenType.RIGHT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_CONDITION);

    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  private Stmt stmtExpression() {
    var expr = expression();

    eat(TokenType.SEMICOLON, ScriptErrorType.EXPECT_SEMI_AFTER_EXPRESSION);

    return new Stmt.Expression(expr);
  }

  private List<Stmt> parseBlockStatements() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(TokenType.RIGHT_BRACE) && hasMoreTokens()) {
      var declaration = declaration();

      if (declaration != null) {
        statements.add(declaration);
      }
    }

    eat(TokenType.RIGHT_BRACE, ScriptErrorType.EXPECT_BRACE_AFTER_BLOCK);

    return statements;
  }

  // expressions below -------------------------------------------------------------------------------------------------

  private Expr expression() {
    return exprAssignment();
  }

  private Expr exprAssignment() {
    Expr expr = exprNullishCoalescence();

    if (advanceExpected(TokenType.QUESTION)) {
      return exprTernary(expr);
    } else if (advanceExpected(TokenType.EQUAL)) {
      Token equals = previous();
      Expr value = exprAssignment();

      if (expr instanceof Expr.Variable varExpr) {
        Token name = varExpr.name();

        return new Expr.Assign(name, value);
      } else if (expr instanceof Expr.Get getExpr) {
        return new Expr.Set(getExpr.object(), getExpr.name(), value);
      }

      errors.add(new ScriptError(ScriptErrorType.INVALID_ASSIGNMENT_TARGET, equals));
    }

    return expr;
  }

  private Expr exprTernary(Expr condition) {
    Expr trueExpr = exprNullishCoalescence();

    eat(TokenType.COLON, ScriptErrorType.EXPECT_COLON_AFTER_TERNARY_TRUE_EXPR);

    Expr falseExpr = exprNullishCoalescence();

    return new Expr.Ternary(condition, trueExpr, falseExpr);
  }

  private Expr exprNullishCoalescence() {
    Expr expr = exprLogicOr();

    while (advanceExpected(TokenType.QUESTION_QUESTION)) {
      Token operator = previous();
      Expr right = exprLogicOr();

      expr = new Expr.NullishCoalescence(expr, operator, right);
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
    boolean isOptional = false;

    while (true) {
      if (advanceExpected(TokenType.QUESTION_DOT)) {
        isOptional = true;
      } else {
        if (advanceExpected(TokenType.LEFT_PAREN)) {
          List<Expr> arguments = new ArrayList<>();

          if (!check(TokenType.RIGHT_PAREN)) {
            do {
              if (arguments.size() >= ParserConstants.MAX_ARGUMENTS) {
                errors.add(new ScriptError(ScriptErrorType.TOO_MANY_ARGUMENTS, peek()));
              }

              arguments.add(expression());
            } while (advanceExpected(TokenType.COMMA));
          }

          Token paren = eat(TokenType.RIGHT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_ARGUMENTS);

          expr = isOptional ? new Expr.CallOptional(expr, paren, arguments) : new Expr.Call(expr, paren, arguments);
          isOptional = false;
        } else if (isOptional || advanceExpected(TokenType.DOT)) {
          Token name = eat(TokenType.IDENTIFIER, ScriptErrorType.EXPECT_PROPERTY_NAME_AFTER_DOT);

          expr = isOptional ? new Expr.GetOptional(expr, name) : new Expr.Get(expr, name);
          isOptional = false;
        } else {
          break;
        }
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

      eat(TokenType.RIGHT_PAREN, ScriptErrorType.EXPECT_PAREN_AFTER_EXPRESSION);

      return new Expr.Grouping(expr);
    }

    if (advanceExpected(TokenType.IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (advanceExpected(TokenType.LEFT_BRACE)) {
      eat(TokenType.RIGHT_BRACE, ScriptErrorType.EXPECT_BRACE_AFTER_MAP_CREATION);

      return new Expr.Literal(new SolaScriptMap());
    }

    if (advanceExpected(
      TokenType.STAR, TokenType.SLASH,
      TokenType.PLUS, // note MINUS is okay since it is unary
      TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL,
      TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL,
      TokenType.AMP_AMP, TokenType.BAR_BAR, TokenType.QUESTION_QUESTION,
      TokenType.EQUAL
    )) {

      errors.add(new ScriptError(ScriptErrorType.INVALID_BINARY_EXPRESSION, previous()));

      throw new ParseError();
    }

    errors.add(new ScriptError(ScriptErrorType.EXPECT_EXPRESSION, previous()));

    throw new ParseError();
  }


  // "hardware" methods below ------------------------------------------------------------------------------------------

  private Token eat(TokenType tokenType, ScriptErrorType type, Object... errorArgs) {
    if (check(tokenType)) {
      return advance();
    }

    errors.add(new ScriptError(type, previous(), errorArgs));

    throw new ParseError();
  }

  private Token advance() {
    if (hasMoreTokens()) {
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

  private boolean hasMoreTokens() {
    return peek().type() != TokenType.EOF;
  }

  private void synchronize() {
    advance();

    while (hasMoreTokens()) {
      if (previous().type() == TokenType.SEMICOLON) {
        return;
      }

      switch (peek().type()) {
        // declarations
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
