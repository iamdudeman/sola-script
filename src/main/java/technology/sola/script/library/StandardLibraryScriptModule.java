package technology.sola.script.library;

import technology.sola.script.interpreter.ValueUtils;
import technology.sola.script.runtime.SolaScriptCallable;

import java.util.List;
import java.util.Map;

public class StandardLibraryScriptModule implements ScriptModule {
  @Override
  public Map<String, Object> variables() {
    return Map.of(
      "print", printCallable,
      "clock", clockCallable
    );
  }

  private final SolaScriptCallable printCallable = new SolaScriptCallable() {
    @Override
    public int arity() {
      return 1;
    }

    @Override
    public Object call(List<Object> arguments) {
      System.out.println(ValueUtils.stringify(arguments.get(0).toString()));

      return null;
    }

    @Override
    public String toString() {
      return "<native fn>";
    }
  };
  private final SolaScriptCallable clockCallable = new SolaScriptCallable() {
    @Override
    public int arity() {
      return 0;
    }

    @Override
    public Object call(List<Object> arguments) {
      return (double) System.currentTimeMillis();
    }

    @Override
    public String toString() {
      return "<native fn>";
    }
  };
}
