package technology.sola.script.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorContainerTest {
  @Test
  void hasError() {
    var errorContainer = new ErrorContainer();

    assertFalse(errorContainer.hasError());
    assertFalse(errorContainer.hasRuntimeError());

    errorContainer.addError(
      new ScriptError(ScriptErrorType.PARSE, 10, 1, "Test message")
    );

    assertTrue(errorContainer.hasError());
    assertFalse(errorContainer.hasRuntimeError());
  }

  @Test
  void hasRuntimeError() {
    var errorContainer = new ErrorContainer();

    assertFalse(errorContainer.hasError());
    assertFalse(errorContainer.hasRuntimeError());

    errorContainer.addError(
      new ScriptError(ScriptErrorType.RUNTIME, 10, 1, "Test message")
    );

    assertTrue(errorContainer.hasError());
    assertTrue(errorContainer.hasRuntimeError());
  }
}
