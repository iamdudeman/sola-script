package technology.sola.script.runtime;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * SolaScriptCallable specifies the contract for something that is callable in a sola script.
 */
@NullMarked
public interface SolaScriptCallable {
  /**
   * The number of expected arguments.
   *
   * @return the number of expected arguments
   */
  int arity();

  /**
   * Calls the function passing along the arguments for the call and returning an evaluated value.
   *
   * @param arguments the arguments for the call
   * @return the evaluated value
   */
  @Nullable Object call(List<Object> arguments);
}
