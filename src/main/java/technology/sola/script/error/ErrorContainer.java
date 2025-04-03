package technology.sola.script.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ErrorContainer contains a list of {@link ScriptError}s found for a particular script. It provides several convenience
 * methods for interacting with errors.
 */
public class ErrorContainer {
  private final List<ScriptError> errorList = new ArrayList<>();

  /**
   * Adds an error to the collection.
   *
   * @param error the {@link ScriptError} to add
   */
  public void addError(ScriptError error) {
    errorList.add(error);
  }

  /**
   * Adds a collection of errors to the collection.
   *
   * @param errors the collection of {@link ScriptError}s to add
   */
  public void addErrors(Collection<ScriptError> errors) {
    errorList.addAll(errors);
  }

  /**
   * @return true if there is at least one error
   */
  public boolean hasError() {
    return !errorList.isEmpty();
  }

  /**
   * @return true if there is at least one error with type {@link ScriptErrorStage#RUNTIME}
   */
  public boolean hasRuntimeError() {
    return errorList.stream().anyMatch(error -> error.type().stage == ScriptErrorStage.RUNTIME);
  }

  /**
   * Prints all errors to {@link System#err}.
   */
  public void printErrors() {
    errorList.forEach(System.err::println);
  }
}
