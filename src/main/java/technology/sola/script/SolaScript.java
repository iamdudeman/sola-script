package technology.sola.script;

import technology.sola.script.error.ErrorContainer;
import technology.sola.script.interpreter.Interpreter;
import technology.sola.script.library.ScriptModule;
import technology.sola.script.library.StandardLibraryScriptModule;
import technology.sola.script.parser.Parser;
import technology.sola.script.resolver.Resolver;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.List;

/**
 * SolaScript is a runtime container for executing sola scripts.
 */
public class SolaScript {
  private final ScriptRuntime scriptRuntime = new ScriptRuntime();

  /**
   * Creates an instance of the SolaScript runtime with the {@link StandardLibraryScriptModule} imported.
   */
  public SolaScript() {
    scriptRuntime.importModule(new StandardLibraryScriptModule());
  }

  /**
   * Creates an instance of the SolaScript runtime with the desired {@link ScriptModule}s imported.
   *
   * @param modules the modules to import
   */
  public SolaScript(List<ScriptModule> modules) {
    modules.forEach(scriptRuntime::importModule);
  }

  /**
   * Executes a sola script using the current state of the {@link SolaScript} runtime.
   *
   * @param source the sola script to execute
   * @return the {@link ErrorContainer} for the result of the execution
   */
  public ErrorContainer execute(String source) {
    var errorContainer = new ErrorContainer();
    var tokenizer = new Tokenizer(source);
    var tokenizeResult = tokenizer.tokenize();
    var parser = new Parser(tokenizeResult.tokens());
    var parserResult = parser.parse();

    errorContainer.addErrors(tokenizeResult.errors());
    errorContainer.addErrors(parserResult.errors());

    var resolver = new Resolver(scriptRuntime);
    var interpreter = new Interpreter(scriptRuntime);

    var resolverErrors = resolver.resolve(parserResult.statements());
    var interpretationErrors = interpreter.interpret(parserResult.statements());

    errorContainer.addErrors(resolverErrors);
    errorContainer.addErrors(interpretationErrors);

    return errorContainer;
  }
}
