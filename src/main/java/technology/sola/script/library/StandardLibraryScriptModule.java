package technology.sola.script.library;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import technology.sola.script.interpreter.ValueUtils;
import technology.sola.script.runtime.SolaScriptCallable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * StandardLibraryScriptModule contains some standard functionality for making useful programs using sola-script.
 */
@NullMarked
public class StandardLibraryScriptModule implements ScriptModule {
  @Override
  public Map<String, Object> variables() {
    Map<String, Object> variables = new HashMap<>();

    variables.put("print", print());
    variables.put("readLine", readLine());
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
      @Nullable
      public Object call(List<@Nullable Object> arguments) {
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
   * <h4>readLine</h4>
   *
   * Reads a line of text from the commandline.
   * <p>
   *
   * <b>Returns:</b> <i>string</i>
   * <p>
   *
   * <b>Usage:</b>
   * <pre>
   * readLine()
   * </pre>
   *
   * @return {@link SolaScriptCallable} definition for readLine
   */
  public SolaScriptCallable readLine() {
    return new SolaScriptCallable() {
      @Override
      public int arity() {
        return 0;
      }

      @Override
      public Object call(List<@Nullable Object> arguments) {
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
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
      public Object call(List<@Nullable Object> arguments) {
        return (double) System.currentTimeMillis();
      }

      @Override
      public String toString() {
        return "<native fn>";
      }
    };
  }
}
