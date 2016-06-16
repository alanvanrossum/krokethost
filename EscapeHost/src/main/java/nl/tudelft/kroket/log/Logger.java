package nl.tudelft.kroket.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger class.
 * 
 * @author Team Kroket
 */
public final class Logger {

  /** The current LogLevel. Default is ALL. */
  private LogLevel level = LogLevel.ALL;

  /** The message format. */
  private static final String MSGFORMAT = "%s %s: %s";

  /** Singleton instance. */
  private static final Logger INSTANCE = new Logger();

  /** The time format. */
  private static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm:ss");

  /**
   * Logger object constructor.
   */
  private Logger() {
  }

  /**
   * Enum Loglevel.
   */
  public enum LogLevel {
    NONE, INFO, ERROR, DEBUG, ALL
  }

  /**
   * Sets the log level.
   * 
   * @param level
   *          the level
   */
  public void setLevel(final LogLevel level) {
    this.level = level;
  }

  /**
   * Gets the log level.
   */
  public LogLevel getLevel() {
    return level;
  }

  /**
   * Singleton instance.
   * 
   * @return the logger object
   */
  public static Logger getInstance() {
    return INSTANCE;
  }

  /**
   * Print an info message.
   * 
   * @param tag
   *          the tag
   * @param message
   *          the message
   */
  public void info(final String tag, final String message) {

    print(LogLevel.INFO, tag, message);
  }

  /**
   * Print a debug message.
   * 
   * @param tag
   *          the tag
   * @param message
   *          the message
   */
  public void debug(final String tag, final String message) {

    print(LogLevel.DEBUG, tag, message);
  }

  /**
   * Print an error message.
   *
   * @param tag
   *          the tag
   * @param message
   *          the message
   */
  public void error(final String tag, final String message) {
    print(LogLevel.ERROR, tag, message);
  }

  /**
   * Print the message to standard output.
   * 
   * @param level
   *          - the level of the message
   * @param tag
   *          - the tag of the message
   * @param message
   *          - the actual message
   */
  void print(final LogLevel level, final String tag, final String message) {

    if (getLevel() == LogLevel.NONE) {
      return;
    }
    if (level.ordinal() <= getLevel().ordinal()) {
      final String output = "[" + TIMEFORMAT.format(new Date()) + "]: "
          + String.format(MSGFORMAT, level, tag, message);

      if (level == LogLevel.ERROR) {
        System.err.println(output);
      } else {
        System.out.println(output);
      }
    }
  }

}
