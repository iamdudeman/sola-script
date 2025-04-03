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
      new ScriptError(ScriptErrorType.EXPECT_EXPRESSION, 10, 1)
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
      new ScriptError(ScriptErrorType.ONLY_INSTANCES_HAVE_PROPERTIES, 10, 1)
    );

    assertTrue(errorContainer.hasError());
    assertTrue(errorContainer.hasRuntimeError());
  }
}
