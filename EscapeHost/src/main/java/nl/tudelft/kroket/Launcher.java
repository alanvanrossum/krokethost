package nl.tudelft.kroket;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.Settings;

public class Launcher {

  /** The logger. */
  static final Logger log = Logger.getInstance();

  /** The class name string. */
  private static final String className = "EscapeHost";

  public static void main(String[] args) {

    log.info(className, "EscapeServer by Team Kroket");
    log.info(className, String.format("Listening port is set to %d", Settings.PORTNUM));
    log.info(className, "Initializing...");

    boolean breakLoop = false;

    // spawn thread
    GameHost server = new GameHost(Settings.PORTNUM);

    Thread serverThread = new Thread(server);

    serverThread.start();

    int tickCounter = 0;

    while (!server.isInitialized()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (server.isInitialized()) {
        break;
      }
      
      if ((tickCounter % Settings.INTERVAL_REPORT_STATUS) == 0) {
        log.info(className, "Server not ready. Is the port in use?");
      }
      tickCounter += 1;
    }

    while (!breakLoop) {

      server.startSession();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if ((tickCounter % Settings.INTERVAL_REPORT_STATUS) == 0) {
        server.printStatus();
      }
      tickCounter += 1;

    // System.out.println("tickCounter = " + tickCounter);
    }
    log.info(className, "Exiting...");
  }
}