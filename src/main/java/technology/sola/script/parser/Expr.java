package technology.sola.script.parser;

import org.jspecify.annotations.Nullable;
import technology.sola.script.tokenizer.Token;

import java.util.List;

/**
 * Expr represents code that evaluates to a value.
 */
public interface Expr {
  /**
   * Evaluate this expression via the desired {@link Visitor}
   *
   * @param visitor the {@link Visitor} to evaluate this expression
   * @param <R>     the return type
   * @return the evaluated value
   */
  @Nullable
  <R> R accept(Visitor<R> visitor);

  /**
   * Defines operations for the various {@link Expr} implementations.
   *
   * @param <R> the return type of the evaluated value
   */
  interface Visitor<R> {
    /**
     * Evaluates a {@link Set} expression and returns the value.
     *
     * @param expr the {@link Set} expression
     * @return the evaluated value
     */
    R set(Set expr);

    /**
     * Evaluates a {@link Assign} expression and returns the value.
     *
     * @param expr the {@link Assign} expression
     * @return the evaluated value
     */
    R assign(Assign expr);

    /**
     * Evaluates a {@link Ternary} expression and returns the value.
     *
     * @param expr the {@link Ternary} expression
     * @return the evaluated value
     */
    R ternary(Ternary expr);

    /**
     * Evaluates a {@link NullishCoalescence} expression and returns the value.
     *
     * @param expr the {@link NullishCoalescence} expression
     * @return the evaluated value
     */
    R nullishCoalescence(NullishCoalescence expr);

    /**
     * Evaluates a {@link Logical} expression and returns the value.
     *
     * @param expr the {@link Logical} expression
     * @return the evaluated value
     */
    R logical(Logical expr);

    /**
     * Evaluates a {@link Binary} expression and returns the value.
     *
     * @param expr the {@link Binary} expression
     * @return the evaluated value
     */
    R binary(Binary expr);

    /**
     * Evaluates a {@link Unary} expression and returns the value.
     *
     * @param expr the {@link Unary} expression
     * @return the evaluated value
     */
    R unary(Unary expr);

    /**
     * Evaluates a {@link Call} expression and returns the value.
     *
     * @param expr the {@link Call} expression
     * @return the evaluated value
     */
    R call(Call expr);

    /**
     * Evaluates a {@link CallOptional} expression and returns the value.
     *
     * @param expr the {@link CallOptional} expression
     * @return the evaluated value
     */
    R callOptional(CallOptional expr);

    /**
     * Evaluates a {@link Get} expression and returns the value.
     *
     * @param expr the {@link Get} expression
     * @return the evaluated value
     */
    R get(Get expr);

    /**
     * Evaluates a {@link GetOptional} expression and returns the value.
     *
     * @param expr the {@link GetOptional} expression
     * @return the evaluated value
     */
    R getOptional(GetOptional expr);

    /**
     * Evaluates a {@link Variable} expression and returns the value.
     *
     * @param expr the {@link Variable} expression
     * @return the evaluated value
     */
    R variable(Variable expr);

    /**
     * Evaluates a {@link Grouping} expression and returns the value.
     *
     * @param expr the {@link Grouping} expression
     * @return the evaluated value
     */
    R grouping(Grouping expr);

    /**
     * Evaluates a {@link Literal} expression and returns the value.
     *
     * @param expr the {@link Literal} expression
     * @return the evaluated value
     */
    R literal(Literal expr);
  }

  record Set(Expr object, Token name, Expr value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.set(this);
    }
  }

  record Assign(Token name, Expr value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.assign(this);
    }
  }

  record Ternary(Expr condition, Expr trueExpr, Expr falseExpr) implements Expr {
    @Override
    public <R> @Nullable R accept(Visitor<R> visitor) {
      return visitor.ternary(this);
    }
  }

  record NullishCoalescence(Expr left, Token operator, Expr right) implements Expr {
    @Override
    public <R> @Nullable R accept(Visitor<R> visitor) {
      return visitor.nullishCoalescence(this);
    }
  }

  record Logical(Expr left, Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.logical(this);
    }
  }

  record Binary(Expr left, Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.binary(this);
    }
  }

  record Unary(Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.unary(this);
    }
  }

  record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.call(this);
    }
  }

  record CallOptional(Expr callee, Token paren, List<Expr> arguments) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.callOptional(this);
    }
  }

  record Get(Expr object, Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.get(this);
    }
  }

  record GetOptional(Expr object, Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.getOptional(this);
    }
  }

  /**
   * Variable expressions hold a name of a variable to evaluate the value of.
   *
   * @param name the {@link Token} name of the variable
   */
  record Variable(Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.variable(this);
    }
  }

  /**
   * Grouping expressions warps an expression to change when it is evaluated.
   *
   * @param expression the expression to evaluate
   */
  record Grouping(Expr expression) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.grouping(this);
    }
  }

  /**
   * Literal expressions hold a value such as string, number, boolean, map or null.
   *
   * @param value the literal value
   */
  record Literal(@Nullable Object value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.literal(this);
    }
  }
}
