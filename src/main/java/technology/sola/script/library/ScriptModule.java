package technology.sola.script.library;

import java.util.Map;

/**
 * ScriptModule defines the api for a module to be imported into a {@link technology.sola.script.runtime.ScriptRuntime}.
 */
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
