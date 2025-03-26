package technology.sola.script.parser;

import technology.sola.script.error.ScriptError;

import java.util.List;

/**
 * ParserResult holds the parsed {@link Stmt}s and any {@link ScriptError}s found while parsing.
 *
 * @param statements the list of {@link Stmt}s
 * @param errors     the list of {@link ScriptError}s
 */
public record ParserResult(
  List<Stmt> statements,
  List<ScriptError> errors
) {
}
