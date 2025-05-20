package technology.sola.script.library;

import org.jspecify.annotations.NullMarked;

import java.util.Map;

/**
 * ScriptModule defines the api for a module to be imported into a {@link technology.sola.script.runtime.ScriptRuntime}.
 */
@NullMarked
public interface ScriptModule {
  /**
   * Gets the variable definitions within this module.
   *
   * @return the variable definitions within this module
   */
  Map<String, Object> variables();

  /**
   * Gets the constant definitions within this module.
   *
   * @return the constant definitions within this module
   */
  Map<String, Object> constants();
}
