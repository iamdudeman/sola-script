package technology.sola.script.library;

import technology.sola.script.interpreter.ValueUtils;
import technology.sola.script.runtime.SolaScriptCallable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StandardLibraryScriptModule contains some standard functionality for making useful programs using sola-script.
 */
public class StandardLibraryScriptModule implements ScriptModule {
  @Override
  public Map<String, Object> variables() {
    Map<String, Object> variables = new HashMap<>();

    variables.put("print", print());
    variables.put("clock", clock());

    return variables;
  }

  @Override
  public Map<String, Object> constants() {
    return Map.of();
  }

  /**
   * <h4>print</h4>
   *
   * Prints a value to the commandline.
   * <p>
   *
   * <b>Arguments:</b>
   * <ul>
   *   <li>the value to print</li>
   * </ul>
   *
   * <b>Returns:</b> <i>null</i>
   * <p>
   *
   * <b>Usage:</b>
   * <pre>
   * print("hello world")
   * </pre>
   *
   * @return {@link SolaScriptCallable} definition for print
   */
  public SolaScriptCallable print() {
    return new SolaScriptCallable() {
      @Override
      public int arity() {
        return 1;
      }

      @Override
      public Object call(List<Object> arguments) {
        System.out.println(ValueUtils.stringify(arguments.get(0)));

        return null;
      }

      @Override
      public String toString() {
        return "<native fn>";
      }
    };
  }

  /**
   * <h4>clock</h4>
   *
   * Returns the current milliseconds since epoch.
   * <p>
   *
   * <b>Returns:</b> <i>number</i>
   * <p>
   *
   * <b>Usage:</b>
   * <pre>
   * clock()
   * </pre>
   *
   * @return {@link SolaScriptCallable} definition for clock
   */
  public SolaScriptCallable clock() {
    return new SolaScriptCallable() {
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
}
