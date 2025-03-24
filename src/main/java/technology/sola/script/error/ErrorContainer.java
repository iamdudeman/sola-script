package technology.sola.script.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorContainer {
  private final List<ScriptError> errorList = new ArrayList<>();

  public void addError(ScriptError error) {
    errorList.add(error);
  }

  public boolean hasErrors() {
    return !errorList.isEmpty();
  }

  public boolean hasRuntimeError() {
    return errorList.stream().anyMatch(error -> error.type() == ScriptErrorType.RUNTIME);
  }

  public void printErrors() {
    errorList.forEach(error -> {
      String where = "[" + error.line() + ":" + error.column() + "]";
      String kind = error.type().name();

      System.err.println(where + " " + kind + " " + error.message());
    });
  }
}
