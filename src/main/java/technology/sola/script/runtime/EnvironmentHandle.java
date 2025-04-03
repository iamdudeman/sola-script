package technology.sola.script.runtime;

/**
 * EnvironmentHandle holds an internal reference to environment state information where variable values are stored.
 */
public class EnvironmentHandle {
  final Environment environment;

  EnvironmentHandle(Environment environment) {
    this.environment = environment;
  }
}
