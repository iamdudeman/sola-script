package technology.sola.script.parser;

import technology.sola.script.error.ScriptError;

import java.util.List;

public record ParserResult(
  List<Stmt> statements,
  List<ScriptError> errors
) {
}
