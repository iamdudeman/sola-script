package technology.sola.script.runtime;

import java.util.List;

public interface SolaScriptCallable {
  int arity();

  Object call(List<Object> arguments);
}
