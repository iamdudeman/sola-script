package technology.sola.script.runtime;

import technology.sola.script.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * SolaScriptMap is the runtime representation of the sola-script map type.
 */
public class SolaScriptMap {
  private final Map<String, Object> properties = new HashMap<>();

  /**
   * Gets a property from the map.
   *
   * @param name the {@link Token} name of the property
   * @return the value associated with the property or null
   */
  public Object get(Token name) {
    return properties.get(name.lexeme());
  }

  /**
   * Sets a property on the map.
   *
   * @param name  the {@link Token} name of the property
   * @param value the value of the property
   */
  public void set(Token name, Object value) {
    set(name.lexeme(), value);
  }

  /**
   * Sets a property on the map.
   *
   * @param name  the string name of the property
   * @param value the value of the property
   */
  public void set(String name, Object value) {
    properties.put(name, value);
  }

  @Override
  public String toString() {
    return "{}";
  }
}
