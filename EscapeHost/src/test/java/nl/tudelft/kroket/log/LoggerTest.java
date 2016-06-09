package nl.tudelft.kroket.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import nl.tudelft.kroket.log.Logger.LogLevel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for testing the Logger class.
 * @author Kroket
 *
 */
public class LoggerTest {

  /** The singleton reference to the Logger instance. */
  private static Logger log = Logger.getInstance();

  /** Get current class name, used for logging output. */
  private final String className = this.getClass().getSimpleName();

  private final ByteArrayOutputStream messages = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errorMessages = new ByteArrayOutputStream();

  private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  private String msgFormat = "%s %s: %s";

  /**
   * This method sets up streams so we can check what
   * is printed to the console.
   */
  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(messages));
    System.setErr(new PrintStream(errorMessages));
  }

  /**
   * This method cleans up the streams.
   */
  @After
  public void cleanUpStreams() {
    System.setOut(null);
    System.setErr(null);
  }

  /**
   * Test method for testLevel method.
   */
  @Test
  public void testLevel() {
    log.setLevel(Logger.LogLevel.DEBUG);
    assertEquals(Logger.LogLevel.DEBUG, log.getLevel());
  }

  /**
   * Test method for {@link nl.tudelft.kroket.log.Logger#getInstance()}.
   */
  @Test
  public void testGetInstance() {
    assertFalse(Logger.getInstance() == null);
  }

  /**
   * Test method for info method.
   */
  @Test
  public void testInfo() {
    String time = "[" + timeFormat.format(new Date()) + "]: ";
    int ti = time.length();
    log.info(className, "Test info message.");
    String expected = String.format(msgFormat, LogLevel.INFO, className, "Test info message.");
    String message = messages.toString().trim();
    message = message.substring(ti, message.toString().length());
    assertEquals(expected, message);
  }

  /**
   * Test method for debug method.
   */
  @Test
  public void testDebug() {
    String time = "[" + timeFormat.format(new Date()) + "]: ";
    int ti = time.length();
    log.debug(className, "Test debug message.");
    String expected = String.format(msgFormat, LogLevel.DEBUG, className, "Test debug message.");
    String message = messages.toString().trim();
    message = message.substring(ti, message.toString().length());
    assertEquals(expected, message);
  }

  /**
   * Test method for error method.
   */
  @Test
  public void testError() {
    String time = "[" + timeFormat.format(new Date()) + "]: ";
    int ti = time.length();
    log.error(className, "Test error message.");
    String expected = String.format(msgFormat, LogLevel.ERROR, className, "Test error message.");
    String errorMessage = errorMessages.toString().trim();
    errorMessage = errorMessage.substring(ti, errorMessage.toString().length());
    assertEquals(expected, errorMessage);
  }
  
  /**
   * Test method for when the loglevel is none.
   */
  @Test
  public void testPrintNone() {
    log.setLevel(LogLevel.NONE);
    log.info(className, "Test loglevel none.");
    String message = messages.toString().trim();
    assertEquals("", message);
  }

}
