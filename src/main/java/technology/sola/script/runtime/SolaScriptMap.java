package technology.sola.script.runtime;

import technology.sola.script.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

public class SolaScriptMap {
  private final Map<String, Object> fields = new HashMap<>();

  public Object get(Token name) {
    return fields.get(name.lexeme());
  }

  public void set(Token name, Object value) {
    set(name.lexeme(), value);
  }

  public void set(String name, Object value) {
    fields.put(name, value);
  }
}
