package nl.tudelft.kroket;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.Settings;

/**
 * Launches the host.
 * 
 * @author Team Kroket
 */
public class Launcher {

  /** The logger. */
  static final Logger log = Logger.getInstance();

  /** The class name string. */
  private static final String className = "EscapeHost";

  private static int tickCounter = 0;

  /**
   * Main method that runs the launcher.
   * 
   * @param args
   *          standard - not used.
   */
  public static void main(String[] args) {

    log.info(className, "EscapeServer by Team Kroket");
    log.info(className, String.format("Listening port is set to %d", Settings.PORTNUM));

    GameHost host = initHost();

    log.info(className, "Entering main loop...");
    mainLoop(host);

    log.info(className, "Exiting...");
  }

  private static void mainLoop(GameHost host) {

    boolean breakLoop = false;

    while (!breakLoop) {

      host.update();

      if ((tickCounter % Settings.INTERVAL_REPORT_STATUS) == 0) {
        host.printStatus();
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      tickCounter += 1;

    }
  }

  private static GameHost initHost() {

    log.info(className, "Initializing...");

    GameHost host = new GameHost(Settings.PORTNUM);

    // spawn thread
    Thread serverThread = new Thread(host);

    serverThread.start();

    while (!host.isInitialized()) {

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (host.isInitialized()) {
        log.info(className, "Server ready.");
      } else {
        log.info(className, "Server not ready. Is the port in use?");
      }

      tickCounter += 1;
    }

    return host;
  }

}
