package technology.sola.script.library;

import technology.sola.script.interpreter.ValueUtils;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.runtime.SolaScriptCallable;

import java.util.List;

public class StandardLibrary {
  public void addToRuntime(ScriptRuntime scriptRuntime) {
    addPrint(scriptRuntime);
    addClock(scriptRuntime);
  }

  private void addPrint(ScriptRuntime scriptRuntime) {
    scriptRuntime.defineVariable("print", new SolaScriptCallable() {
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
    });
  }

  private void addClock(ScriptRuntime scriptRuntime) {
    scriptRuntime.defineVariable("clock", new SolaScriptCallable() {
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
    });
  }
}
