package technology.sola.script.tokenizer;

import org.jspecify.annotations.NullMarked;
import technology.sola.script.error.ScriptError;

import java.util.List;

/**
 * The result from {@link Tokenizer#tokenize()} containing the parsed {@link Token}s as well as any {@link ScriptError}s.
 *
 * @param tokens the parsed tokens
 * @param errors the errors found while tokenizing
 */
@NullMarked
public record TokenizeResult(
  List<Token> tokens,
  List<ScriptError> errors
) {
}
