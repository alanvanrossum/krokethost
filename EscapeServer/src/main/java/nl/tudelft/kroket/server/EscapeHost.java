package nl.tudelft.kroket.server;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.user.RegisteredUser;
import nl.tudelft.kroket.user.User;
import nl.tudelft.kroket.user.User.PlayerType;
import nl.tudelft.kroket.user.UserInstance;

/**
 * EscapeServer object.
 * 
 * @author Team Kroket
 *
 */
public class EscapeHost implements Runnable {

  /** List of clients connected to the server. */
  public static HashMap<Socket, UserInstance> clientList = new HashMap<Socket, UserInstance>();

  public static HashMap<Socket, RegisteredUser> userList = new HashMap<Socket, RegisteredUser>();

  /** Value to keep track whether socket was initialized. */
  private static boolean initialized;

  /** The class name string. */
  private static final String CLASSNAME = "EscapeServer";

  /** The game activity boolean. */
  private static boolean gameActive = false;

  /** The serverport int. */
  protected int serverPort;

  /** The client list array. */
  // List<UserInstance> clientList = new ArrayList<UserInstance>();

  /** The logger. */
  static final Logger LOG = Logger.getInstance();

  /** the Server socket. */
  protected ServerSocket serverSocket = null;

  /** Is Stopped boolean. */
  protected boolean isStopped = false;

  /** The running Thread. */
  protected Thread runningThread = null;

  /** The thread pool. */
  protected ExecutorService threadPool = Executors
      .newFixedThreadPool(Settings.THREAD_POOL_MAX);

  /**
   * The main method of the game.
   * 
   * @param args
   *            - String[]
   */
  public static void main(String[] args) {

    LOG.info(CLASSNAME, "EscapeServer by Team Kroket");
    LOG.info(CLASSNAME,
        String.format("Listening port is set to %d", Settings.PORTNUM));
    LOG.info(CLASSNAME, "Initializing...");
    initialized = false;

    EscapeHost server = new EscapeHost(Settings.PORTNUM);

    initLoop(server);
    mainLoop();

    LOG.info(CLASSNAME, "Exiting...");
  }

  private static void mainLoop() {

    int tickCounter = 0;

    if (initialized) {

      boolean breakLoop = false;

      while (!breakLoop) {

        LOG.info(CLASSNAME,
            "Game not ready. Waiting for players to register...");

        while (!gameReady()) {

          tickCounter += 1;

          if (tickCounter % Settings.INTERVAL_REPORT_STATUS == 0) {

            LOG.info(CLASSNAME, "Game not ready.");

            printUsers();
          }

          try {
            Thread.sleep(1000);
          } catch (InterruptedException error) {
            error.printStackTrace();
          }
        }

        LOG.info(CLASSNAME, "Server is ready to host game.");

        startGame();

        gameLoop();

      }

    } else {
      LOG.error(CLASSNAME, "Initialization failed.");
    }

  }

  private static void initLoop(EscapeHost host) {
    while (!initialized) {

      // spawn this server in a new thread
      Thread serverThread = new Thread(host);

      serverThread.start();

      if (initialized) {
        break;
      }

      try {
        Thread.sleep(Settings.INTERVAL_SOCKET_RETRY * 1000);
      } catch (InterruptedException error) {
        error.printStackTrace();
      }
    }
  }

  private static void gameLoop() {
    gameActive = true;

    int tickCounter = 0;

    while (gameActive && gameReady()) {
      tickCounter += 1;

      if (tickCounter % Settings.INTERVAL_REPORT_STATUS == 0) {

        LOG.info(CLASSNAME, "Game is active.");
        printUsers();
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException error) {
        error.printStackTrace();
      }
    }

    gameActive = false;

    LOG.info(CLASSNAME, "Game session has ended.");
  }

  /**
   * Start the game.
   */
  private static void startGame() {

    LOG.info(CLASSNAME, "Starting game...");
    sendAll("Starting game...");
    sendAll(Protocol.COMMAND_START);

  }

  /**
   * Setter for the server port.
   * 
   * @param port
   *            - int
   */
  public EscapeHost(final int port) {
    serverPort = port;
  }

  /**
   * Remove a player based on its socket.
   * 
   * @param clientSocket
   *            the client's socket
   */
  public static void removePlayer(final Socket clientSocket) {

    final RegisteredUser player = userList.get(clientSocket);

    userList.remove(clientSocket);

    if (!clientSocket.isClosed()) {
      try {
        clientSocket.close();
      } catch (IOException error) {
        error.printStackTrace();
      }
    }

    if (player != null) {
      LOG.info(CLASSNAME,
          String.format("Player %s disconnected.", player.getName()));
      sendAll(String.format("Player %s has left the game.",
          player.getName()));
    }
    LOG.info(CLASSNAME, "Socket "
        + clientSocket.toString()
        + " removed.");
  }

  /**
   * Register a player to the server.
   * 
   * @param clientSocket
   *            - the client's socket
   * @param dOutput
   *            - the dataoutputstream
   * @param playername
   *            - the player's name
   */
  public static RegisteredUser registerPlayer(final Socket clientSocket,
      final DataOutputStream dOutput, final String playername) {

    RegisteredUser player = null;

    if (playername.length() > 0) {

      if (getUser(clientSocket) == null) {

        // make sure chosen name is still available
        if (getUser(playername) == null) {

          // create a new player object
          player = new RegisteredUser(clientSocket, dOutput,
              playername);

          // put new player object in playerList
          // key should already exist
          userList.put(clientSocket, player);

          // tell player that he registered succesfully
          player.sendMessage("You are now registered as "
              + player.toString());

          if (!gameReady()) {
            player.sendMessage(userCount() + " players waiting...");
          }

          // let everyone know a new player registered with the server
          sendAll("Player " + player.getName() + " entered the game.");
        } else {

          // player's name was not available, let player know

          LOG.info(CLASSNAME, "Player " + playername
              + " already registered");

          try {
            dOutput.writeBytes("Name already in use.\r\n");
          } catch (IOException error) {
            error.printStackTrace();
          }
        }
      } else {
        LOG.info(CLASSNAME, "Socket already registered.");
        try {
          dOutput.writeBytes("You are already registered.\r\n");
        } catch (IOException error) {
          // TODO Auto-generated catch block
          error.printStackTrace();
        }

      }
    }

    return player;
  }

  /**
   * The run class.
   */
  public void run() {
    synchronized (this) {
      runningThread = Thread.currentThread();
    }

    LOG.info(CLASSNAME, "Creating socket...");

    try {
      serverSocket = new ServerSocket(serverPort);
    } catch (IOException exception) {
      LOG.error(CLASSNAME, "Error: " + exception);
    }

    if (serverSocket == null) {
      initialized = false;
    } else {

      initialized = true;

      // keep listening for incoming connections
      // until we manually terminate the server or
      // some error (we cannot recover from) occurs
      while (!isStopped()) {
        Socket clientSocket = null;
        try {
          LOG.info(CLASSNAME, String.format(
              "Listening for incoming connections on port %d...",
              serverPort));
          clientSocket = serverSocket.accept();

          LOG.info(CLASSNAME,
              "Connection accepted. Incoming connection from "
                  + clientSocket.getRemoteSocketAddress()
                  .toString());

        } catch (IOException error) {
          error.printStackTrace();
        }

        // hand the client's socket to a new thread in the pool and
        // start the thread
        UserInstance instance = new UserInstance(clientSocket);

        clientList.put(clientSocket, instance);

        threadPool.execute(instance);

        // as we just accepted a new connection, immediately print
        // status
        printUsers();
      }

      // ...

      threadPool.shutdown();
    }
  }

  private synchronized boolean isStopped() {
    return isStopped;
  }

  // /** The stop method. */
  // private synchronized void stop() {
  // isStopped = true;
  // try {
  // serverSocket.close();
  // } catch (IOException error) {
  // throw new RuntimeException("Error closing server", error);
  // }
  // }

  /** The count players method. */
  public static int countUsers(PlayerType type) {
    int sum = 0;
    for (Entry<Socket, RegisteredUser> entry : userList.entrySet()) {

      if (entry.getValue().getType() == type) {
        sum += 1;
      }
    }

    return sum;
  }

  /**
   * Count the number of players present in the server.
   * 
   * @return the number of players present
   */
  public static int userCount() {

    return userList.size();
  }

  public static int clientCount() {

    return clientList.size();
  }

  /**
   * Check whether the server is ready to start a game.
   * 
   * @return true when game is ready
   */
  public static boolean gameReady() {
    return countUsers(PlayerType.VIRTUAL) == Settings.REQUIRED_VIRTUAL
        && countUsers(PlayerType.MOBILE) == Settings.REQUIRED_MOBILE;
  }

  /**
   * Get a player by socket.
   * 
   * @param socket
   *            the socket
   * @return Player object
   */
  public static RegisteredUser getUser(final Socket socket) {
    return userList.get(socket);
  }

  /**
   * Get a player by name.
   * 
   * @param playername
   *            the name of the player
   * @return the player object
   */
  private static User getUser(final String playername) {
    for (Entry<Socket, RegisteredUser> entry : userList.entrySet()) {

      // player was found
      if (playername.equals(entry.getValue().getName())) {

        return entry.getValue();
      }
    }

    // no matches, return null
    return null;
  }

  /**
   * Send a message to all connected players. This will omit users without a
   * type.
   * 
   * @param message
   *            the message to be sent
   */
  public static void sendAll(final String message) {
    for (Entry<Socket, RegisteredUser> entry : userList.entrySet()) {

      if (entry.getValue() != null) {
        entry.getValue().sendMessage(message);
      }
    }

  }

  /**
   * Send a message to all connected mobile clients.
   * 
   * @param message
   *            the string to be sent
   */
  public static void sendMobile(final String message) {
    for (Entry<Socket, RegisteredUser> entry : userList.entrySet()) {

      if (entry.getValue() != null
          && entry.getValue().getType() == PlayerType.MOBILE) {
        entry.getValue().sendMessage(message);
      }
    }
  }

  /**
   * Send a message to all connected virtual clients.
   * 
   * @param message
   *            the string to be sent
   */
  public static void sendVirtual(final String message) {
    for (Entry<Socket, RegisteredUser> entry : userList.entrySet()) {

      if (entry.getValue() != null
          && entry.getValue().getType() == PlayerType.VIRTUAL) {
        entry.getValue().sendMessage(message);
      }
    }
  }

  private static void printClients() {
    System.out.println("Connected clients:");

    for (Entry<Socket, UserInstance> entry : clientList.entrySet()) {

      System.out.println("Client: "
          + entry.getKey().getRemoteSocketAddress().toString());

    }
  }

  /**
   * Print all players, registered and unregistered.
   */
  private static void printUsers() {

    printClients();

    if (userList.isEmpty()) {
      System.out.println("No players currently registered.");
    } else {

      System.out.println("Registered players:");

      for (Entry<Socket, RegisteredUser> entry : userList.entrySet()) {

        System.out.println("Registered: \t" + entry.getValue());
      }
    }

  }
  

}