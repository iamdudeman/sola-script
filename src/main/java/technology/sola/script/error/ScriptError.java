package technology.sola.script.error;

public interface ScriptError {
  int line();

  int column();

  String message();

  ScriptErrorType type();
}
