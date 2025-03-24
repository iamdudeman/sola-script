package technology.sola.script.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ErrorContainer {
  private final List<ScriptError> errorList = new ArrayList<>();

  public void addError(ScriptError error) {
    errorList.add(error);
  }

  public void addErrors(Collection<ScriptError> errors) {
    errorList.addAll(errors);
  }

  public boolean hasErrors() {
    return !errorList.isEmpty();
  }

  public boolean hasRuntimeError() {
    return errorList.stream().anyMatch(error -> error.type() == ScriptErrorType.RUNTIME);
  }

  public void printErrors() {
    errorList.forEach(System.err::println);
  }
}
