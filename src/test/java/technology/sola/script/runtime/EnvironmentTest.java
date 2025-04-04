package technology.sola.script.runtime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentTest {
  private final Token identifierToken = new Token(TokenType.IDENTIFIER, "test", "test", 1, 1);

  @Nested
  class get {
    @Test
    void whenNotDefined_shouldThrow() {
      Environment environment = new Environment();

      assertThrows(
        ScriptInterpretationException.class,
        () -> environment.get(identifierToken)
      );
    }

    @Test
    void whenNullValue_shouldNotThrow() {
      Environment environment = new Environment();

      environment.defineVariable(identifierToken.lexeme(), null);

      assertNull(environment.get(identifierToken));
    }
  }

  @Nested
  class define {
    @Test
    void shouldAddVariable() {
      Environment environment = new Environment();

      environment.defineVariable(identifierToken.lexeme(), 12.23d);

      assertEquals(12.23, environment.get(identifierToken));
    }
  }

  @Nested
  class assign {
    @Test
    void whenDefined_shouldAssignToCurrent() {
      Environment environment = new Environment();

      environment.defineVariable(identifierToken.lexeme(), null);
      environment.assign(identifierToken, 12.23d);

      assertEquals(12.23, environment.get(identifierToken));
    }

    @Test
    void whenDefinedInParent_shouldAssignToParent() {
      Environment parent = new Environment();
      Environment environment = new Environment(parent);

      parent.defineVariable(identifierToken.lexeme(), null);
      environment.assign(identifierToken, 12.23d);

      assertEquals(12.23, parent.get(identifierToken));
      assertEquals(12.23, environment.get(identifierToken));
    }

    @Test
    void whenNotDefined_shouldThrow() {
      Environment environment = new Environment();

      assertThrows(
        ScriptInterpretationException.class,
        () -> environment.assign(identifierToken, 12.23d)
      );
    }
  }
}
