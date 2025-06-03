package technology.sola.script.interpreter;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import technology.sola.script.error.ScriptInterpretationException;
import technology.sola.script.parser.Expr;
import technology.sola.script.parser.Parser;
import technology.sola.script.parser.Stmt;
import technology.sola.script.runtime.ScriptRuntime;
import technology.sola.script.runtime.SolaScriptCallable;
import technology.sola.script.runtime.SolaScriptMap;
import technology.sola.script.tokenizer.Token;
import technology.sola.script.tokenizer.TokenType;
import technology.sola.script.tokenizer.Tokenizer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionInterpreterTest {
  private ScriptRuntime scriptRuntime;

  @BeforeEach
  void setup() {
    scriptRuntime = new ScriptRuntime();
  }

  @Nested
  class set {
    @Test
    void whenNotMap_shouldThrow() {
      scriptRuntime.defineVariable("test", 5d);

      assertThrows(
        ScriptInterpretationException.class,
        () -> evaluateExpressionStatementSource("test.prop = 5;")
      );
    }

    @Test
    void test() {
      SolaScriptMap map = new SolaScriptMap();

      scriptRuntime.defineVariable("test", map);

      assertEvaluation("test.prop = 5;", 5d);
    }
  }

  @Nested
  class assign {
    @Test
    void test() {
      scriptRuntime.defineVariable("test", null);

      assertEvaluation("test = 5;", 5d);

      var variable = new Expr.Variable(new Token(TokenType.IDENTIFIER, "test", null, 1, 1));
      var value = scriptRuntime.lookUpVariable(variable);

      assertEquals(5d, value);
    }
  }

  @Nested
  class nullishCoalescence {
    @Test
    void whenLeftNull_shouldReturnRight() {
      assertEvaluation("null ?? 5;", 5d);
      assertEvaluation("null ?? true;", true);
      assertEvaluation("null ?? null;", null);
    }

    @Test
    void whenLeftFalse_shouldReturnLeft() {
      assertEvaluation("false ?? 5;", false);
    }

    @Test
    void whenLeftTruthy_shouldReturnLeft() {
      assertEvaluation("true ?? 5;", true);
      assertEvaluation("4 ?? 5;", 4d);
    }
  }

  @Nested
  class logical {
    @Nested
    class or {
      @Test
      void whenLeftTruthy_shouldReturnLeft() {
        assertEvaluation("true || 5;", true);
        assertEvaluation("5 || true;", 5d);
      }

      @Test
      void whenLeftNotTruthy_shouldReturnRight() {
        assertEvaluation("false || 5;", 5d);
        assertEvaluation("null || true;", true);
      }
    }

    @Nested
    class and {
      @Test
      void whenLeftNotTruthy_shouldReturnLeft() {
        assertEvaluation("false && 5;", false);
        assertEvaluation("null && true;", null);
      }

      @Test
      void whenLeftTruthy_shouldReturnRight() {
        assertEvaluation("true && 5;", 5d);
        assertEvaluation("5 && true;", true);
      }
    }
  }

  @Nested
  class binary {
    @Nested
    class greater {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true > 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 > 3;", true);
        assertEvaluation("3 > 5;", false);
        assertEvaluation("5 > 5;", false);
      }
    }

    @Nested
    class greaterEqual {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true >= 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 >= 3;", true);
        assertEvaluation("3 >= 5;", false);
        assertEvaluation("5 >= 5;", true);
      }
    }

    @Nested
    class less {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true < 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 < 3;", false);
        assertEvaluation("3 < 5;", true);
        assertEvaluation("5 < 5;", false);
      }
    }

    @Nested
    class lessEqual {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true <= 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 <= 3;", false);
        assertEvaluation("3 <= 5;", true);
        assertEvaluation("5 <= 5;", true);
      }
    }

    @Nested
    class equalEqual {
      @Test
      void test() {
        assertEvaluation("null == null;", true);
        assertEvaluation("5 == 5;", true);
        assertEvaluation("\"test\" == \"test\";", true);

        assertEvaluation("null == 5;", false);
        assertEvaluation("5 == 3;", false);
        assertEvaluation("\"test\" == \"test2\";", false);
      }
    }

    @Nested
    class bangEqual {
      @Test
      void test() {
        assertEvaluation("null != null;", false);
        assertEvaluation("5 != 5;", false);
        assertEvaluation("\"test\" != \"test\";", false);

        assertEvaluation("null != 5;", true);
        assertEvaluation("5 != 3;", true);
        assertEvaluation("\"test\" != \"test2\";", true);
      }
    }

    @Nested
    class minus {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true - 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 - 3;", 2d);
        assertEvaluation("3 - 5;", -2d);
        assertEvaluation("5 - 5;", 0d);
      }
    }

    @Nested
    class plus {
      @Test
      void whenNotNumbersOrOneString_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true / 5;")
        );
      }

      @Test
      void whenNumbers_shouldAdd() {
        assertEvaluation("6 + 3;", 9d);
        assertEvaluation("3 + 6;", 9d);
        assertEvaluation("5 + 5;", 10d);
      }

      @Test
      void whenLeftOrRightString_shouldConcatenate() {
        assertEvaluation("\"test\" + 3;", "test3");
        assertEvaluation("3 + \"test\";", "3test");
        assertEvaluation("false + \"test\";", "falsetest");
        assertEvaluation("null + \"test\";", "nulltest");
      }
    }

    @Nested
    class slash {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true / 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("6 / 3;", 2d);
        assertEvaluation("3 / 6;", 0.5d);
        assertEvaluation("5 / 5;", 1d);
      }
    }

    @Nested
    class star {
      @Test
      void whenNotNumbers_shouldThrow() {
        assertThrows(
          ScriptInterpretationException.class,
          () -> evaluateExpressionStatementSource("true * 5;")
        );
      }

      @Test
      void test() {
        assertEvaluation("5 * 3;", 15d);
        assertEvaluation("3 * 5;", 15d);
        assertEvaluation("5 * 5;", 25d);
      }
    }
  }

  @Nested
  class unary {
    @Test
    void bang() {
      assertEvaluation("!true;", false);
      assertEvaluation("!false;", true);
    }

    @Test
    void minus_whenNotNumber_shouldThrow() {
      assertThrows(
        ScriptInterpretationException.class,
        () -> evaluateExpressionStatementSource("-false;")
      );
    }

    @Test
    void minus() {
      assertEvaluation("-5;", -5d);
      assertEvaluation("--5;", 5d);
    }
  }

  @Nested
  class call {
    @Test
    void whenWrongArguments_shouldThrow() {
      scriptRuntime.defineVariable("test", new SolaScriptCallable() {
        @Override
        public int arity() {
          return 0;
        }

        @Override
        public Object call(@NonNull List<Object> arguments) {
          return null;
        }
      });

      assertThrows(
        ScriptInterpretationException.class,
        () -> evaluateExpressionStatementSource("test(5);")
      );
    }

    @Test
    void whenNotCallable_shouldThrow() {
      scriptRuntime.defineVariable("test", 5d);

      assertThrows(
        ScriptInterpretationException.class,
        () -> evaluateExpressionStatementSource("test();")
      );
    }

    @Test
    void valid() {
      scriptRuntime.defineVariable("test", new SolaScriptCallable() {
        @Override
        public int arity() {
          return 1;
        }

        @Override
        public Object call(@NonNull List<Object> arguments) {
          return arguments.get(0);
        }
      });

      assertEquals(5d, evaluateExpressionStatementSource("test(5);"));
    }
  }

  @Nested
  class get {
    @Test
    void whenNotMap_shouldThrow() {
      scriptRuntime.defineVariable("test", 5d);

      assertThrows(
        ScriptInterpretationException.class,
        () -> evaluateExpressionStatementSource("test.prop;")
      );
    }

    @Test
    void test() {
      SolaScriptMap map = new SolaScriptMap();

      map.set("prop", 5d);

      scriptRuntime.defineVariable("test", map);

      assertEvaluation("test.prop;", 5d);
    }
  }

  @Nested
  class variable {
    @Test
    void test() {
      scriptRuntime.defineVariable("test", 5d);

      assertEvaluation("test;", 5d);
    }
  }

  @Nested
  class grouping {
    @Test
    void test() {
      assertEvaluation("( 3 );", 3d);
    }
  }

  @Nested
  class literal {
    @Test
    void test() {
      assertEvaluation("3;", 3d);
    }
  }

  private void assertEvaluation(String source, Object expectedValue) {
    var result = evaluateExpressionStatementSource(source);

    assertEquals(expectedValue, result);
  }

  private Object evaluateExpressionStatementSource(String source) {
    var tokenizer = new Tokenizer(source);
    var parser = new Parser(tokenizer.tokenize().tokens());
    var expressionInterpreter = new ExpressionInterpreter(scriptRuntime);
    var statements = parser.parse().statements();

    if (statements.size() != 1) {
      fail("Expecting exactly one statement for the test.");
      return null;
    }

    if (statements.get(0) instanceof Stmt.Expression statement) {
      return expressionInterpreter.evaluate(statement.expr());
    } else {
      fail("Test source code is not an expression statement.");
      return null;
    }
  }
}
