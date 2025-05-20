package technology.sola.script.parser;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import technology.sola.script.interpreter.Interpreter;
import technology.sola.script.tokenizer.Token;

import java.util.List;

/**
 * Stmt represents a piece of code that performs an action or controls the flow of the program.
 */
@NullMarked
public interface Stmt {
  /**
   * Executes this statement via the desired {@link Stmt.Visitor}
   *
   * @param visitor the {@link Stmt.Visitor} to execute this statement
   * @param <R>     the return type
   * @return the return value from the executed statement
   */
  <R> R accept(Visitor<R> visitor);

  /**
   * Defines operations for the various {@link Stmt} implementations.
   *
   * @param <R> the return type of the executed statement
   */
  interface Visitor<R> {
    /**
     * Executes a {@link Function} statement and returns the value.
     *
     * @param stmt the {@link Function} statement
     * @return the evaluated value
     */
    R function(Function stmt);

    /**
     * Executes a {@link Var} statement and returns the value.
     *
     * @param stmt the {@link Var} statement
     * @return the evaluated value
     */
    R var(Var stmt);

    /**
     * Executes a {@link Val} statement and returns the value.
     *
     * @param stmt the {@link Val} statement
     * @return the evaluated value
     */
    R val(Val stmt);

    /**
     * Executes an {@link Expression} statement and returns the value.
     *
     * @param stmt the {@link Expression} statement
     * @return the evaluated value
     */
    R expression(Expression stmt);

    /**
     * Executes an {@link If} statement and returns the value.
     *
     * @param stmt the {@link If} statement
     * @return the evaluated value
     */
    R ifVisit(If stmt);

    /**
     * Executes a {@link Return} statement and returns the value.
     *
     * @param stmt the {@link Return} statement
     * @return the evaluated value
     */
    R returnVisit(Return stmt);

    /**
     * Executes a {@link While} statement and returns the value.
     *
     * @param stmt the {@link While} statement
     * @return the evaluated value
     */
    R whileVisit(While stmt);

    /**
     * Executes a {@link Block} and returns the value.
     *
     * @param stmt the {@link Block} statement
     * @return the evaluated value
     */
    R block(Block stmt);
  }

  /**
   * Function statement declares a function in the current scope with desired parameters and body.
   *
   * @param name       the {@link Token} name of the function
   * @param parameters the list of parameter names for the function
   * @param body       the list of {@link Stmt} that make up the body of the function
   */
  record Function(Token name, List<Token> parameters, List<Stmt> body) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.function(this);
    }
  }

  /**
   * Var statement declares a variable in the current scope with an optional initializer.
   *
   * @param name        the {@link Token} name of the variable
   * @param initializer the expression to initialize the variable with
   */
  record Var(Token name, @Nullable Expr initializer) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.var(this);
    }
  }

  /**
   * Val statement declares a constant in the current scope with an initializer.
   *
   * @param name        the {@link Token} name of the constant
   * @param initializer the expression to initialize the constant with
   */
  record Val(Token name, Expr initializer) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.val(this);
    }
  }

  /**
   * Expression statement evaluates an expression.
   *
   * @param expr the expression to evaluate
   */
  record Expression(Expr expr) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.expression(this);
    }
  }

  /**
   * If statement is a conditional branching statement. If the condition evaluates
   * to {@link technology.sola.script.interpreter.ValueUtils#isTruthy(Object)} then the thenBranch {@link Stmt} will be
   * executed. If the condition evaluates to not truthy then the optional else branch will execute if it is provided.
   *
   * @param condition  the condition to branch from
   * @param thenBranch the truthy branch to execute
   * @param elseBranch the optional not truthy branch to execute
   */
  record If(Expr condition, Stmt thenBranch, @Nullable Stmt elseBranch) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.ifVisit(this);
    }
  }

  /**
   * Return statement exits a function or method while optionally returning a value for the caller to use.
   *
   * @param keyword the {@link Token} for the return keyword
   * @param value   the value to return to the caller or null
   */
  record Return(Token keyword, @Nullable Expr value) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.returnVisit(this);
    }

    /**
     * Return is an exception utilized by the {@link Interpreter} to throw when
     * a {@link Stmt.Return} is being interpreted so that the wrapping
     * {@link technology.sola.script.runtime.SolaScriptFunction} can catch it and return the value as the result
     * of the {@link Expr.Call}.
     */
    public static class Exception extends RuntimeException {
      /**
       * The value returned by the {@link Stmt.Return}.
       */
      @Nullable
      public final Object value;

      /**
       * Creates an instance of this exception with desired return value.
       *
       * @param value the return value of the call
       */
      public Exception(@Nullable Object value) {
        super(null, null, false, false);
        this.value = value;
      }
    }
  }

  /**
   * While statement is a conditional looping statement. If the condition evaluates
   * to {@link technology.sola.script.interpreter.ValueUtils#isTruthy(Object)} then the body will be executed. After
   * the body is executed the condition will be evaluated again until it is not truthy.
   *
   * @param condition the condition to verify if the loop should continue
   * @param body      the body to execute while the condition is truthy
   */
  record While(Expr condition, Stmt body) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.whileVisit(this);
    }
  }

  /**
   * Block contains a list of {@link Stmt}s that are executed in their own scope.
   *
   * @param statements the list of {@link Stmt}s to execute
   */
  record Block(List<Stmt> statements) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.block(this);
    }
  }
}
