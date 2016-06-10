package nl.tudelft.kroket.server;

/**
 * Settings for EscapeServer. This class simply contains all settings used to control the server.
 * 
 * @author Team Kroket
 *
 */
public class Settings {

  /** Port number to bind server to. */
  public static final int PORTNUM = 1234;

  /** Number of VR clients required to start the game. */
  public static final int REQUIRED_VIRTUAL = 1;

  /** Number of mobile clients required to start the game. */
  public static final int REQUIRED_MOBILE = 2;

  /** Number of seconds before each status print. */
  public static final int INTERVAL_REPORT_STATUS = 120;

  /** Number of seconds until retrying to bind to the socket. */
  public static final int INTERVAL_SOCKET_RETRY = 5;

  /** Number of threads available in the pool. */
  public static final int THREAD_POOL_MAX = 10;

  public static final int TIMELIMIT = 60;
}
