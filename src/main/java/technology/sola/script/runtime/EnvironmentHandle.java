package technology.sola.script.runtime;

import org.jspecify.annotations.NullMarked;

/**
 * EnvironmentHandle holds an internal reference to environment state information where variable values are stored.
 */
@NullMarked
public class EnvironmentHandle {
  final Environment environment;

  EnvironmentHandle(Environment environment) {
    this.environment = environment;
  }
}
