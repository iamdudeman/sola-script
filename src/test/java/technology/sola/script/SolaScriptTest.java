package technology.sola.script;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import technology.sola.script.interpreter.ValueUtils;
import technology.sola.script.library.ScriptModule;
import technology.sola.script.library.StandardLibraryScriptModule;
import technology.sola.script.runtime.SolaScriptCallable;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@NullMarked
public class SolaScriptTest {
  private Map<String, String> buildTests() {
    final Map<String, String> testFileExpectedResults = new HashMap<>();

    testFileExpectedResults.put("conditions", """
      true
      false
      true
      false
      correct
      """);
    testFileExpectedResults.put("function", """
      hello test!
      hello test!
      """);
    testFileExpectedResults.put("loops", """
      0
      1
      2
      3
      4
      """);
    testFileExpectedResults.put("map", """
      test
      nestedProp
      """);
    testFileExpectedResults.put("nullish", """
      hello world
      hello world
      """);
    testFileExpectedResults.put("variables-and-blocks", """
      test
      new
      test
      """);

    return testFileExpectedResults;
  }

  @TestFactory
  Stream<DynamicTest> test() {
    return buildTests().entrySet().stream().map(
      entry -> {
        return DynamicTest.dynamicTest(entry.getKey(), () -> {
          String fileName = "test_programs/sola/" + entry.getKey() + ".sola";
          byte[] bytes = Files.readAllBytes(Paths.get(fileName));
          String script = new String(bytes, Charset.defaultCharset());

          assertStandardOutputForScript(fileName, entry.getValue(), script);
        });
      }
    );
  }

  private void assertStandardOutputForScript(String fileName, String expected, String script) {
    TestScriptModule testScriptModule = new TestScriptModule();
    SolaScript solaScript = new SolaScript(List.of(new StandardLibraryScriptModule(), testScriptModule));

    var errorContainer = solaScript.execute(script);

    if (errorContainer.hasError()) {
      errorContainer.printErrors();
      fail("Expected that there were no runtime errors in " + fileName + ". See errors messages above.");
    }

    assertEquals(expected.trim(), testScriptModule.getOutput().trim(), "Error in " + fileName + ".");
  }

  private static class TestScriptModule implements ScriptModule {
    private final List<String> standardOutput = new ArrayList<>();

    public String getOutput() {
      return String.join("\n", standardOutput);
    }

    @Override
    public Map<String, Object> variables() {
      return Map.of(
        "print", print()
      );
    }

    @Override
    public Map<String, Object> constants() {
      return Map.of();
    }

    public SolaScriptCallable print() {
      return new SolaScriptCallable() {
        @Override
        public int arity() {
          return 1;
        }

        @Override
        @Nullable
        public Object call(List<@Nullable Object> arguments) {
          standardOutput.add(ValueUtils.stringify(arguments.get(0)));

          return null;
        }

        @Override
        public String toString() {
          return "<native fn>";
        }
      };
    }
  }
}
