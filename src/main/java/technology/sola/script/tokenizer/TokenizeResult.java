package technology.sola.script.tokenizer;

import technology.sola.script.error.ScriptError;

import java.util.List;

public record TokenizeResult(
  List<Token> tokens,
  List<ScriptError> errors
) {
}
