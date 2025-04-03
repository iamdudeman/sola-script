package technology.sola.script.runtime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScriptRuntimeTest {
  @Nested
  class importModule {
    @Test
    void test() {
      var runtime = new ScriptRuntime();
      final var token = new Token(TokenType.IDENTIFIER, "test", null, 1, 1);

      runtime.importModule(() -> Map.of(token.lexeme(), 12.35d));

      assertEquals(12.35d, runtime.lookUpVariable(new Expr.Variable(token)));
    }
  }

  @Nested
  class global {
    @Test
    void shouldBeAbleToDefineVariable() {
      var runtime = new ScriptRuntime();
      var variableToken = new Token(TokenType.IDENTIFIER, "myVar", null, 1, 1);

      runtime.defineVariable(variableToken.lexeme(), "myValue");

      assertEquals("myValue", runtime.lookUpVariable(new Expr.Variable(variableToken)));
    }

    @Test
    void shouldBeAbleToAssignVariable() {
      var runtime = new ScriptRuntime();
      var variableToken = new Token(TokenType.IDENTIFIER, "myVar", null, 1, 1);

      runtime.defineVariable(variableToken.lexeme(), "myValue");
      runtime.assignVariable(new Expr.Assign(variableToken, new Expr.Literal("newValue")), "newValue");

      assertEquals("newValue", runtime.lookUpVariable(new Expr.Variable(variableToken)));
    }

    @Test
    void whenDefinedInNested_shouldNotBeInGlobal() {
      var runtime = new ScriptRuntime();
      var variableToken = new Token(TokenType.IDENTIFIER, "myVar", null, 1, 1);
      var handle = runtime.createNestedEnvironment();

      runtime.defineVariable(variableToken.lexeme(), "myValue");
      runtime.restoreEnvironment(handle);

      assertThrows(
        ScriptInterpretationException.class,
        () -> runtime.lookUpVariable(new Expr.Variable(variableToken))
      );
    }
  }

  @Nested
  class nested {
    @Test
    void whenNotDefinedInNested_shouldFallbackToGlobal() {
      var runtime = new ScriptRuntime();
      var variableToken = new Token(TokenType.IDENTIFIER, "myVar", null, 1, 1);

      runtime.defineVariable(variableToken.lexeme(), "myValue");

      runtime.createNestedEnvironment();

      assertEquals("myValue", runtime.lookUpVariable(new Expr.Variable(variableToken)));
    }

    /**
     * Test program
     * <pre>
     * var myVar = "myValue";
     * {
     *   var myVar = "nestedMyValue";
     *
     *   myVar = "nestedMyValue";
     * }
     * </pre>
     */
    @Test
    void shouldBeAbleToDefineVariableAtScope() {
      var runtime = new ScriptRuntime();
      var variableToken = new Token(TokenType.IDENTIFIER, "myVar", null, 1, 1);
      var variableExpression = new Expr.Variable(variableToken);

      // resolve scoping
      runtime.scopes().beginScope();
      runtime.scopes().declare(variableToken);
      runtime.scopes().define(variableToken);
      runtime.scopes().resolveLocal(variableExpression, variableExpression.name());
      runtime.scopes().endScope();

      // interpret
      runtime.defineVariable(variableToken.lexeme(), "myValue");
      var handle = runtime.createNestedEnvironment();
      runtime.defineVariable(variableToken.lexeme(), "nestedMyValue");

      assertEquals("nestedMyValue", runtime.lookUpVariable(variableExpression));

      runtime.restoreEnvironment(handle);
      assertEquals("myValue", runtime.lookUpVariable(new Expr.Variable(variableToken)));
    }

    /**
     * Test program
     * <pre>
     * var myVar = "myValue";
     * {
     *   var myVar = "myValue";
     *
     *   myVar = "nestedMyValue";
     * }
     * </pre>
     */
    @Test
    void shouldBeAbleToAssignVariableAtScope() {
      var runtime = new ScriptRuntime();
      var variableToken = new Token(TokenType.IDENTIFIER, "myVar", null, 1, 1);
      var variableExpression = new Expr.Variable(variableToken);
      var assignExpression = new Expr.Assign(variableToken, new Expr.Literal("nestedMyValue"));

      // resolve scoping
      runtime.scopes().beginScope();
      runtime.scopes().declare(variableToken);
      runtime.scopes().define(variableToken);
      runtime.scopes().resolveLocal(variableExpression, variableExpression.name());
      runtime.scopes().resolveLocal(assignExpression, assignExpression.name());
      runtime.scopes().endScope();

      // interpret
      runtime.defineVariable(variableToken.lexeme(), "myValue");
      var handle = runtime.createNestedEnvironment();
      runtime.defineVariable(variableToken.lexeme(), "myValue");
      runtime.assignVariable(assignExpression, "nestedMyValue");

      assertEquals("nestedMyValue", runtime.lookUpVariable(variableExpression));

      runtime.restoreEnvironment(handle);
      assertEquals("myValue", runtime.lookUpVariable(new Expr.Variable(variableToken)));
    }
  }
}
