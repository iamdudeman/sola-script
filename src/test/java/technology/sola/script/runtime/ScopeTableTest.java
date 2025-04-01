package technology.sola.script.runtime;

import org.junit.jupiter.api.Test;
import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import static org.junit.jupiter.api.Assertions.*;

class ScopeTableTest {
  @Test
  void whenNoScope_shouldDoNothing() {
    Token token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
    Expr.Variable variable = new Expr.Variable(token);
    ScopeTable scopeTable = new ScopeTable();

    scopeTable.resolveLocal(variable, token);
    scopeTable.declare(token);
    scopeTable.define(token);
    assertFalse(scopeTable.isDeclaredInScope(token));
    assertFalse(scopeTable.isSelfReferenceVariableInitialization(variable));
  }

  @Test
  void whenScopeCreated_shouldBeAbleToDeclare() {
    Token token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
    ScopeTable scopeTable = new ScopeTable();

    scopeTable.beginScope();
    scopeTable.declare(token);

    assertTrue(scopeTable.isDeclaredInScope(token));

    scopeTable.endScope();

    assertFalse(scopeTable.isDeclaredInScope(token));
  }

  @Test
  void whenScopeCreated_shouldBeAbleToDefine() {
    Token token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
    ScopeTable scopeTable = new ScopeTable();

    scopeTable.beginScope();
    scopeTable.define(token);

    assertTrue(scopeTable.isDeclaredInScope(token));

    scopeTable.endScope();

    assertFalse(scopeTable.isDeclaredInScope(token));
  }

  @Test
  void whenDeclaredButNotDefined_shouldIdentifyAsSelfVariableInitialization() {
    Token token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);
    Expr.Variable variable = new Expr.Variable(token);
    ScopeTable scopeTable = new ScopeTable();

    scopeTable.beginScope();
    scopeTable.declare(token);

    assertTrue(scopeTable.isSelfReferenceVariableInitialization(variable));
    scopeTable.define(token);
    assertFalse(scopeTable.isSelfReferenceVariableInitialization(variable));
  }

  @Test
  void whenResolved_shouldGetCorrectDistance() {
    Token tokenFirst = new Token(TokenType.IDENTIFIER, "first", null, 1, 1);
    Token tokenSecond = new Token(TokenType.IDENTIFIER, "second", null, 2, 1);
    Token tokenThird = new Token(TokenType.IDENTIFIER, "third", null, 3, 1);
    Token tokenThirdMissing = new Token(TokenType.IDENTIFIER, "third", null, 4, 1);
    Expr.Variable variableFirst = new Expr.Variable(tokenFirst);
    Expr.Variable variableSecond = new Expr.Variable(tokenSecond);
    Expr.Variable variableThird = new Expr.Variable(tokenThird);
    Expr.Variable variableThirdMissing = new Expr.Variable(tokenThirdMissing);
    ScopeTable scopeTable = new ScopeTable();

    scopeTable.beginScope();
    scopeTable.declare(tokenFirst);
    scopeTable.declare(tokenSecond);

    scopeTable.beginScope();
    scopeTable.resolveLocal(variableFirst, tokenFirst); // 1 below

    scopeTable.beginScope();
    scopeTable.resolveLocal(variableSecond, tokenSecond); // 2 below
    scopeTable.declare(tokenThird);
    scopeTable.resolveLocal(variableThird, tokenThird); // 0
    scopeTable.endScope();

    scopeTable.resolveLocal(variableThirdMissing, tokenThirdMissing); // 1 above (not declared in scope)

    assertEquals(1, scopeTable.getDistance(variableFirst));
    assertEquals(2, scopeTable.getDistance(variableSecond));
    assertEquals(0, scopeTable.getDistance(variableThird));
    assertNull(scopeTable.getDistance(variableThirdMissing));
  }
}
