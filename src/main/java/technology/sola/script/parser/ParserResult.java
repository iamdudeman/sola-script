package technology.sola.script.parser;

import technology.sola.script.error.ScriptError;

import java.util.List;

public record ParserResult(
// todo  List<Stmt> statements,
  List<ScriptError> errors
) {
}
