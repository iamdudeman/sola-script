package technology.sola.script.parser;

import technology.sola.script.tokenizer.Token;

import java.util.List;

/**
 * Stmt represents a piece of code that performs an action or controls the flow of the program.
 */
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
     * Executes a {@link Var} statement and returns the value.
     *
     * @param stmt the {@link Var} statement
     * @return the evaluated value
     */
    R var(Var stmt);

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
   * Var statement declares a variable in the current scope with an optional initializer.
   *
   * @param name        the {@link Token} name of the variable
   * @param initializer the expression to initialize the variable with
   */
  record Var(Token name, Expr initializer) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.var(this);
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
  record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.ifVisit(this);
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
