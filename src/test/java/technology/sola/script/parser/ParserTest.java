package technology.sola.script.parser;

import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptError;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
  @Test
  void primary() {
    var source = """
      false;
      """;
    var expected = """
      false
      """.trim();
    var result = visualizeScriptParsing(source);

    assertEquals(0, result.errors.size());
    assertEquals(expected, result.parsedScript);
  }

  private TestResult visualizeScriptParsing(String source) {
    var tokenizer = new Tokenizer(source);
    var parser = new Parser(tokenizer.tokenize().tokens());
    var astPrinter = new AstPrinter();
    var parserResult = parser.parse();
    var parsedScript = parserResult.statements().stream()
      .map(astPrinter::print)
      .collect(Collectors.joining("\n"));

    return new TestResult(
      parsedScript,
      parserResult.errors()
    );
  }

  private record TestResult(String parsedScript, List<ScriptError> errors) {
  }
}
