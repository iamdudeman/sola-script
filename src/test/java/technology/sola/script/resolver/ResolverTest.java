package technology.sola.script.resolver;

import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptErrorType;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResolverTest {
  @Test
  void test() {
    ScriptRuntime scriptRuntime = new ScriptRuntime();
    Resolver resolver = new Resolver(scriptRuntime);

    scriptRuntime.scopes().beginScope();

    var token = new Token(TokenType.IDENTIFIER, "test", null, 1,  1);
    var firstDeclaration = new Stmt.Var(token, null);
    var secondDeclaration = new Stmt.Var(token, null);

    assertFalse(scriptRuntime.scopes().isDeclaredInScope(token));

    var errors = resolver.resolve(List.of(firstDeclaration, secondDeclaration));

    assertEquals(1, errors.size());
    assertEquals(ScriptErrorType.ALREADY_DEFINED_VARIABLE, errors.get(0).type());
    assertTrue(scriptRuntime.scopes().isDeclaredInScope(token));
  }
}
