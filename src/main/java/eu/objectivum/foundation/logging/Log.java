package eu.objectivum.foundation.logging;

import ch.qos.logback.classic.LoggerContext;
import jakarta.validation.constraints.NotNull;
import org.slf4j.*;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.toLevel;
import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Entry point for additional (mostly <a href="https://www.slf4j.org/">SLF4J</a> and <a
 * href="http://logback.qos.ch/">Logback</a>-based) logging capabilities.
 *
 * @author Octavian Theodor NITA (https://github.com/octavian-nita/)
 * @version 4.0, Mar 30, 2021
 */
public class Log {

  /**
   * Performs additional logging configuration, like {@link #routeJulToSlf4j() adapting j.u.l to SLF4J}, etc.
   * <p/>
   * Should be invoked early, during the initialization phase of the application.
   */
  public static void config() {
    routeJulToSlf4j();
  }

  /**
   * Route any j.u.l logging request to SLF4J.
   *
   * @see <a href="http://blog.cn-consult.dk/2009/03/bridging-javautillogging-to-slf4j.html">Bridging
   *   java.util.logging to SLF4J</a>
   * @see <a href="http://stackoverflow.com/questions/9117030/jul-to-slf4j-bridge">JUL to SLF4J Bridge</a>
   * @see <a href="https://logback.qos.ch/manual/configuration.html#LevelChangePropagator">LevelChangePropagator
   *   configuration</a>
   */
  public static void routeJulToSlf4j() {
    // We might not need this if we're installing a LevelChangePropagator
    java.util.logging.LogManager.getLogManager().reset();

    SLF4JBridgeHandler.removeHandlersForRootLogger(); // avoid having everything logged twice
    SLF4JBridgeHandler.install();

    java.util.logging.Logger.getGlobal().setLevel(java.util.logging.Level.FINEST);
  }

  public static void setLevel(String level) {
    setLevel((String) null, level);
  }

  public static void setLevel(Logger logger, String level) {
    setLevel(logger == null ? null : logger.getName(), level);
  }

  public static void setLevel(Class<?> clazz, String level) {
    setLevel(clazz == null ? null : clazz.getName(), level);
  }

  /**
   * Setting the log level programmatically requires accessing the actual SLF4J implementation used.
   *
   * @param level the SLF4J implementation-specific name of the desired level
   */
  public static void setLevel(String loggerName, String level) {
    final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    if (loggerFactory instanceof LoggerContext) { // Logback
      ((LoggerContext) loggerFactory)
        .getLogger(loggerName == null ? ROOT_LOGGER_NAME : loggerName)
        .setLevel(toLevel(level, INFO));
    }
  }

  @NotNull
  public static Logger log() {
    return logFor(null);
  }

  /**
   * @param source if {@code null}, a {@link Logger} instance corresponding to the calling class is retrieved
   * @return a {@link Logger} instance <em>corresponding</em> to the provided {@code source} argument
   */
  @NotNull
  public static Logger logFor(Object source) {
    switch (source) {
    case null:
      try {
        return getLogger(StackWalker.getInstance(RETAIN_CLASS_REFERENCE).getCallerClass());
      } catch (Throwable throwable) {
        return getLogger(ROOT_LOGGER_NAME);
      }
    case Class<?> clazz:
      return getLogger(clazz);
    case String string:
      return getLogger(string);
    case Logger logger:
      return logger;
    default:
      return getLogger(source.getClass());
    }
  }
}
