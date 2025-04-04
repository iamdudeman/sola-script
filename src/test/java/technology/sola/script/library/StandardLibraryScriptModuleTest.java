package technology.sola.script.library;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardLibraryScriptModuleTest {
  @Nested
  class print {
    @Test
    void test() {
      var function = new StandardLibraryScriptModule().print();

      assertEquals(1, function.arity());
    }
  }

  @Nested
  class readLine {
    @Test
    void test() {
      var function = new StandardLibraryScriptModule().readLine();

      assertEquals(0, function.arity());
    }
  }

  @Nested
  class clock {
    @Test
    void test() {
      var start = System.currentTimeMillis();
      var function = new StandardLibraryScriptModule().clock();

      assertEquals(0, function.arity());

      var result = function.call(List.of());

      if (result instanceof Double doubleValue) {
        assertTrue(doubleValue >= start);
      } else {
        fail("clock should return a double (number) value");
      }
    }
  }
}
